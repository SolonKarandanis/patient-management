import {patchState, signalStore, withComputed, withMethods, withProps, withState} from '@ngrx/signals';
import {AuthState, initialAuthState} from './auth.state';
import {computed, inject, Signal} from '@angular/core';
import {Operation, User} from '@models/user.model';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {EMPTY, map, pipe, switchMap, tap} from 'rxjs';
import {SubmitCredentialsDTO} from '@models/auth.model';
import {tapResponse} from '@ngrx/operators';
import {JwtUtil} from '@core/services/jwt-util.service';
import {AuthRepository} from '@core/repositories/auth.repository';
import {setError, setLoaded, setLoading, withCallState} from '@core/store/features/call-state.feature';
import {UtilService} from '@core/services/util.service';
import {UserRoles} from '@models/constants';
import {NgxPermissionsService} from 'ngx-permissions';

export const AuthStore = signalStore(
  { providedIn: 'root' },
  withState<AuthState>(initialAuthState),
  withCallState(),
  withProps(()=>({
    jwtUtil:inject(JwtUtil),
    authRepo:inject(AuthRepository),
    utilService:inject(UtilService),
    ngxPermissionsService: inject(NgxPermissionsService),
  })),
  withComputed((
    {
      user,
      expires,
    },
  )=>({
    getUserId: computed(()=> user()?.publicId),
    getUsername: computed(()=>user()?.username),
    getUser: computed(()=> user()),
    getRoleIds: computed(()=> extractRoleIds(user())),
    isJwtExpired: computed(()=> isTimestampExpired(expires())),
  })),
  withMethods((state)=>{
    const jwtUtil = state.jwtUtil;
    return ({
      isUserMe: (userId:string| undefined):Signal<boolean>=>
        computed(()=> isCurrentUser(state.isLoggedIn(), state.user(), userId)),
      hasRole: (role:UserRoles):Signal<boolean>=>
        computed(()=> userHasRole(state.isLoggedIn(), state.user(), role)),
      hasAnyAuthority: (authorities: string[] | string): Signal<boolean> =>
        computed(()=> userHasAnyAuthority(state.isLoggedIn(), state.user(), authorities)),
      setTokenDetails(authToken:string,expires:string){
        jwtUtil.saveToken(authToken);
        jwtUtil.saveTokenExpiration(expires);
        patchState(state,{authToken,expires})
      },
      setAccountInfoFromStorage(token:string,expires:string,user:User){
        const roles = user.roles
        patchState(state,{authToken:token,expires,isLoggedIn:true,user,roles});
      },
      setAccount(user:User){
        const roles = user.roles
        patchState(state,{isLoggedIn:true,user, roles })
      },
      setPermissions(permissions:string[]){
        patchState(state,{permissions});
      },
      setLoadingState(){
        patchState(state, setLoading());
      },
      setLoadedState(){
        patchState(state, setLoaded());
      },
      setErrorState(error:string){
        patchState(state, setError(error));
      },
      logout(){
        jwtUtil.destroyToken();
        jwtUtil.destroyTokenExpiration();
        patchState(state,{...initialAuthState, ...setLoaded()})
      },
    })
  }),
  withMethods((state)=>{
    const authRepo = state.authRepo;

    const _loadUserAndPermissions = () => {
      return authRepo.getUserByToken().pipe(
        tapResponse({
          next: (response: User) => {
            state.setAccount(response);
          },
          error: (error: string) => {
            state.setErrorState(error);
          }
        }),
        switchMap(() => {
          const id = state.getUserId()!;
          const  ngxPermissionsService = state.ngxPermissionsService;
          return authRepo.getUserPermissions(id).pipe(
            tapResponse({
              next: (response: string[]) => {
                state.setPermissions(response);
                ngxPermissionsService.loadPermissions(response);
                state.setLoadedState();
              },
              error: (error: string) => {
                state.setErrorState(error);
              }
            })
          );
        })
      );
    }

    return ({
      login: rxMethod<SubmitCredentialsDTO>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap((credentials)=>
            authRepo.login(credentials).pipe(
              tapResponse({
                next:({token,expires})=>{
                  state.setTokenDetails(token,expires);
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                }
              }),
              switchMap(()=> _loadUserAndPermissions())
            )
          )
        )
      ),
      initAuth: rxMethod<void>(
        pipe(
          tap(() => patchState(state, setLoading())),
          map(() => {
            const token = state.jwtUtil.getToken();
            const expirationDate = state.jwtUtil.getTokenExpiration();
            const isExpired = state.jwtUtil.isJwtExpired();

            const shouldLogin = !isExpired && token && expirationDate;
            if (shouldLogin) {
              patchState(state, { authToken: token, expires: expirationDate, isLoggedIn: true });
            }
            return shouldLogin;
          }),
          switchMap((shouldLogin) => {
            if (shouldLogin) {
              return _loadUserAndPermissions();
            }
            state.logout();
            return EMPTY;
          })
        )
      ),
      loadUserAndPermissions: rxMethod<void>(
        pipe(
          tap(() => state.setLoadingState()),
          switchMap(() => _loadUserAndPermissions())
        )
      ),
      getUserPermissions: rxMethod<string>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap((userId)=>{
            const  ngxPermissionsService = state.ngxPermissionsService;
            return authRepo.getUserPermissions(userId).pipe(
              tapResponse({
                next:(response:string[])=>{
                  state.setPermissions(response)
                  ngxPermissionsService.loadPermissions(response);
                  state.setLoadedState();
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                }
              })
            )
          })
        )
      )
    })
  })
);

function extractRoleIds(user: User | undefined): number[] {
  return user ? user.roles.map((role) => role.id) : [];
}

function isTimestampExpired(expiresAtSeconds: string | undefined): boolean {
  if (!expiresAtSeconds) {
    return true;
  }
  const expiryDate = new Date(Number(expiresAtSeconds) * 1000);
  return expiryDate < new Date();
}

function isCurrentUser(isLoggedIn: boolean, currentUser: User | undefined, userId: string | undefined): boolean {
  if (!isLoggedIn || !userId || !currentUser) {
    return false;
  }
  return currentUser.publicId === userId;
}

function userHasRole(isLoggedIn: boolean, user: User | undefined, role: UserRoles): boolean {
  if (!isLoggedIn || !user) {
    return false;
  }
  const found = user.roles.find((r) => r.name === role);
  return !!found;
}

function userHasAnyAuthority(isLoggedIn: boolean, user: User | undefined, authorities: string[] | string): boolean {
  if (!isLoggedIn || !user) {
    return false;
  }
  const authorityList = Array.isArray(authorities) ? authorities : [authorities];
  return user.operations.some((operation: Operation) => authorityList.includes(operation.name));
}
