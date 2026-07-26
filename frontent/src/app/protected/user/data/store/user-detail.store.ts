import {patchState, signalStore, withComputed, withMethods, withProps, withState} from '@ngrx/signals';
import {initialUserDetailState, UserDetailState} from './user-detail.state';
import {computed, inject} from '@angular/core';
import {UserRepository} from '../repositories/user.repository';
import {UtilService} from '@core/services/util.service';
import {ChangePasswordRequest, CreateUserRequest, Role, UpdateUserRequest, User} from '@models/user.model';
import {setError, setLoaded, setLoading, withCallState} from '@core/store/features/call-state.feature';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {pipe, switchMap, tap} from 'rxjs';
import {tapResponse} from '@ngrx/operators';
import {TranslateService} from '@ngx-translate/core';
import {SelectItem} from 'primeng/api';

export const UserDetailStore = signalStore(
  {providedIn:'root'},
  withState<UserDetailState>(initialUserDetailState),
  withCallState(),
  withProps(()=>({
    userRepo:inject(UserRepository),
    utilService:inject(UtilService),
    translate:inject(TranslateService),
  })),
  withComputed((
    {
      selectedUser
    },
  )=>({
    getUsername: computed(()=> selectedUser()?.username),
    getUserId: computed(()=> selectedUser()?.publicId),
    getUser: computed(()=> selectedUser()),
    getUserRolesAsSelectItems: computed(()=> rolesToSelectItems(selectedUser()?.roles)),
  })),
  withMethods((state)=>({
    setLoadingState(){
      patchState(state, setLoading());
    },
    setLoadedState(){
      patchState(state, setLoaded());
    },
    setErrorState(error:string){
      patchState(state, setError(error));
    },
    setSelectedUser(selectedUser:User| null){
      patchState(state,{
        selectedUser,
      })
    },
    setCreatedUserId(createdUserId:string | null){
      patchState(state,{
        createdUserId,
      })
    },
  })),
  withMethods((state)=>{
    const {userRepo,utilService, translate} = state;
    return ({
      getUserById: rxMethod<string>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap((id)=>
            userRepo.getUserById(id).pipe(
              tapResponse({
                next:(result)=>{
                  state.setSelectedUser(result);
                  state.setLoadedState();
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                }
              })
            )
          )
        )
      ),
      registerUser: rxMethod<CreateUserRequest>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap((request)=>
            userRepo.registerUser(request).pipe(
              tapResponse({
                next:(result)=>{
                  state.setCreatedUserId(result.publicId);
                  state.setLoadedState();
                  utilService.showMessage('success',translate.instant('REGISTER.MESSAGES.SUCCESS.user-register-success'));
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                  utilService.showMessage('error',translate.instant('REGISTER.MESSAGES.ERROR.user-register-failure'));
                }
              })
            )
          )
        )
      ),
      updateUser: rxMethod<{id:string, request:UpdateUserRequest}>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap(({id,request})=>
            userRepo.updateUser(id,request).pipe(
              tapResponse({
                next:(result)=>{
                  state.setSelectedUser(result);
                  state.setLoadedState();
                  utilService.showMessage('success',translate.instant('USER.MESSAGES.SUCCESS.user-edit-success'));
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                  utilService.showMessage('error',translate.instant('USER.MESSAGES.ERROR.user-edit-failure'));
                }
              })
            )
          )
        )
      ),
      changeUserPassword: rxMethod<{id:string, request:ChangePasswordRequest}>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap(({id,request})=>
            userRepo.changeUserPassword(id,request).pipe(
              tapResponse({
                next:(result)=>{
                  state.setSelectedUser(result);
                  state.setLoadedState();
                  utilService.showMessage('success',translate.instant('USER.MESSAGES.SUCCESS.user-password-change-success'));
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                  utilService.showMessage('error',translate.instant('USER.MESSAGES.ERROR.user-password-change-failure'));
                }
              })
            )
          )
        )
      ),
      deleteUser: rxMethod<string>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap((id)=>
            userRepo.deleteUser(id).pipe(
              tapResponse({
                next:(result)=>{
                  state.setSelectedUser(null);
                  state.setLoadedState();
                  utilService.showMessage('success',translate.instant('USER.MESSAGES.SUCCESS.user-delete-success'));
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                  utilService.showMessage('error',translate.instant('USER.MESSAGES.ERROR.user-delete-failure'));
                }
              })
            )
          )
        )
      ),
      activateUser: rxMethod<string>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap((id)=>
            userRepo.activateUser(id).pipe(
              tapResponse({
                next:(result)=>{
                  state.setSelectedUser(result);
                  state.setLoadedState();
                  utilService.showMessage('success',translate.instant('USER.MESSAGES.SUCCESS.user-activate-success'));
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                  utilService.showMessage('error',translate.instant('USER.MESSAGES.ERROR.user-activate-failure'));
                }
              })
            )
          )
        )
      ),
      deactivateUser: rxMethod<string>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap((id)=>
            userRepo.deactivateUser(id).pipe(
              tapResponse({
                next:(result)=>{
                  state.setSelectedUser(result);
                  state.setLoadedState();
                  utilService.showMessage('success',translate.instant('USER.MESSAGES.SUCCESS.user-deactivate-success'));
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                  utilService.showMessage('error',translate.instant('USER.MESSAGES.ERROR.user-deactivate-failure'));
                }
              })
            )
          )
        )
      ),
    })
  })
);

function rolesToSelectItems(roles: Role[] | undefined): SelectItem[] {
  if (!roles || roles.length === 0) {
    return [];
  }
  return roles.map((role) => ({ label: role.nameLabel, value: role.name }) as SelectItem);
}
