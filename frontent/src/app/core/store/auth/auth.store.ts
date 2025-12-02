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
    getRoleIds: computed(()=> {
      const loggedUser = user();
      if(loggedUser) {
        return loggedUser.roles.map((role)=> role.id)
      }
      return [];

    }),
    isJwtExpired: computed(()=>{
      const  date= expires()
      if(date){
        const expDate: Date = new Date(Number(date) * 1000);
        const nowDate: Date = new Date();
        return expDate < nowDate;
      }
      return true;
    }),
  })),
  withMethods((state)=>{
    const jwtUtil = state.jwtUtil;
    return ({
      isUserMe: (userId:string| undefined):Signal<boolean>=> computed(()=>{
        if(!state.isLoggedIn() || !userId){
          return false;
        }
        const loggedInUserId =state.user()!.publicId;
        return loggedInUserId ===userId;
      }),
      hasRole: (role:UserRoles):Signal<boolean>=> computed(()=>{
        if(!state.isLoggedIn()){
          return false;
        }
        const roles = state.user()!.roles;
        const found =roles.find((r)=>r.name === role);
        return !!found;
      }),
      hasAnyAuthority: (authorities: string[] | string): Signal<boolean> => computed(() => {
        if(!state.isLoggedIn()){
          return false;
        }
        if(!Array.isArray(authorities)) {
          authorities = [authorities];
        }

        const operations = state.user()!.operations;

        return operations.some((operation:Operation)=> authorities.includes(operation.name));
      }),
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
