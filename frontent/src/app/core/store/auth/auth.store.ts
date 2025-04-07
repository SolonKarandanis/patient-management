import {patchState, signalStore, withComputed, withMethods, withProps, withState} from '@ngrx/signals';
import {AuthState, initialAuthState} from './auth.state';
import {computed, inject, Signal} from '@angular/core';
import {Operation, User} from '@models/user.model';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {pipe, switchMap, tap} from 'rxjs';
import {SubmitCredentialsDTO} from '@models/auth.model';
import {tapResponse} from '@ngrx/operators';
import {JwtUtil} from '@core/services/jwt-util.service';
import {AuthRepository} from '@core/repositories/auth.repository';

export const AuthStore = signalStore(
  { providedIn: 'root' },
  withState<AuthState>(initialAuthState),
  withProps(()=>({
    jwtUtil:inject(JwtUtil),
    authRepo:inject(AuthRepository),
  })),
  withComputed((
    {
      user,
      expires
    },
  )=>({
    getUsername: computed(()=>user()?.username),
    getUser: computed(()=> user()),
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
        patchState(state,{authToken,expires,errorMessage:null,showError:false,loading:false})
      },
      setAccountInfoFromStorage(token:string,expires:string,user:User){
        patchState(state,{authToken:token,expires,isLoggedIn:true,user});
      },
      setAccount(user:User){
        patchState(state,{isLoggedIn:true,errorMessage:null,showError:false,loading:false,user })
      },
      logout(){
        jwtUtil.destroyToken();
        jwtUtil.destroyTokenExpiration();
        patchState(state,initialAuthState)
      },

      setLoading(loading:boolean){
        patchState(state,{loading:loading,showError:false});
      },
      setError(error:string){
        patchState(state,{loading:false,showError:true,errorMessage:'Error'});
      }
    })
  }),
  withMethods((state)=>{
    const authRepo = state.authRepo;
    return ({
      login: rxMethod<SubmitCredentialsDTO>(
        pipe(
          tap(() => {
            state.setLoading(true)
          }),
          switchMap((creadentials)=>
            authRepo.login(creadentials).pipe(
              tapResponse({
                next:({token,expires})=>{
                  state.setTokenDetails(token,expires);

                },
                error: (error:string) =>{
                  state.setError(error)
                }
              }),
              switchMap(()=>
                authRepo.getUserByToken().pipe(
                  tapResponse({
                    next:(response:User)=>{
                      state.setAccount(response)
                    },
                    error: (error:string) =>{
                      state.setError(error)
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
            state.setLoading(true)
          }),
          switchMap(()=>
            authRepo.getUserByToken().pipe(
              tapResponse({
                next:(response:User)=>{
                  state.setAccount(response)
                },
                error: (error:string) =>{
                  state.setError(error)
                }
              })
            )
          )
        )
      )
    })
  }),
);
