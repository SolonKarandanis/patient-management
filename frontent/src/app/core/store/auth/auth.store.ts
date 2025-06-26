import {patchState, signalStore, withComputed, withHooks, withMethods, withProps, withState} from '@ngrx/signals';
import {AuthState, initialAuthState} from './auth.state';
import {computed, inject, Signal} from '@angular/core';
import {Operation, User} from '@models/user.model';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {pipe, switchMap, tap} from 'rxjs';
import {SubmitCredentialsDTO} from '@models/auth.model';
import {tapResponse} from '@ngrx/operators';
import {JwtUtil} from '@core/services/jwt-util.service';
import {AuthRepository} from '@core/repositories/auth.repository';
import {setError, setLoaded, setLoading, withCallState} from '@core/store/features/call-state.feature';
import {UtilService} from '@core/services/util.service';
import {UserRoles} from '@models/constants';

export const AuthStore = signalStore(
  { providedIn: 'root' },
  withState<AuthState>(initialAuthState),
  withCallState(),
  withProps(()=>({
    jwtUtil:inject(JwtUtil),
    authRepo:inject(AuthRepository),
    utilService:inject(UtilService),
  })),
  withComputed((
    {
      user,
      expires,
    },
  )=>({
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
        patchState(state,{authToken:token,expires,isLoggedIn:true,user});
      },
      setAccount(user:User){
        patchState(state,{isLoggedIn:true,user })
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
        patchState(state,initialAuthState)
      },
    })
  }),
  withMethods((state)=>{
    const authRepo = state.authRepo;
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
              switchMap(()=>
                authRepo.getUserByToken().pipe(
                  tapResponse({
                    next:(response:User)=>{
                      state.setAccount(response)
                      state.setLoadedState();
                    },
                    error: (error:string) =>{
                      state.setErrorState(error);
                    }
                  })
                )
              )
            )
          )
        )
      ),
      getUserAccount: rxMethod<void>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap(()=>
            authRepo.getUserByToken().pipe(
              tapResponse({
                next:(response:User)=>{
                  state.setAccount(response)
                  state.setLoadedState();
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                }
              })
            )
          )
        )
      )
    })
  }),
  withHooks((state)=>{
    const {jwtUtil,setAccountInfoFromStorage}= state;
    const setAccountInfoToStore =():void =>{
      const token = jwtUtil.getToken();
      const expirationDate = jwtUtil.getTokenExpiration();
      const userFromStorage = jwtUtil.getUser(token);
      const isExpired =jwtUtil.isJwtExpired();
      if(!isExpired && token && expirationDate && userFromStorage){
        setAccountInfoFromStorage(token,expirationDate,userFromStorage);
      }
    }
    return {
      onInit(){
        setAccountInfoToStore();
      }
    }
  })
);
