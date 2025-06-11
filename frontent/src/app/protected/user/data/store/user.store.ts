import {patchState, signalStore, withComputed, withMethods, withProps, withState} from '@ngrx/signals';
import {initialUserState, UserState} from './user.state';
import {computed, inject} from '@angular/core';
import {UserRepository} from '../repositories/user.repository';
import {UtilService} from '@core/services/util.service';
import {HttpUtil} from '@core/services/http-util.service';
import {CreateUserRequest, UpdateUserRequest, User} from '@models/user.model';
import {setError, setLoaded, setLoading, withCallState} from '@core/store/features/call-state.feature';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {UserSearchRequest} from '@models/search.model';
import {map, pipe, switchMap, tap} from 'rxjs';
import {tapResponse} from '@ngrx/operators';
import {HttpResponse} from '@angular/common/http';
import {GenericFile} from '@models/file.model';
import {
  resetSearchState,
  setTableLoaded,
  setTableLoading,
  withSearchState
} from '@core/store/features/search-state.feature';
import {TranslateService} from '@ngx-translate/core';

export const UserStore = signalStore(
  {providedIn:'root'},
  withState<UserState>(initialUserState),
  withCallState(),
  withSearchState(),
  withProps(()=>({
    userRepo:inject(UserRepository),
    utilService:inject(UtilService),
    httpUtil:inject(HttpUtil),
    translate:inject(TranslateService),
  })),
  withComputed((
    {
      selectedUser
    },
  )=>({
    getUsername: computed(()=>{
      return selectedUser()?.username;
    }),
    getUserId: computed(()=>{
      return selectedUser()?.publicId;
    }),
    getUser: computed(()=>{
      return selectedUser();
    }),
  })),
  withMethods((state)=>({
    setLoadingState(){
      patchState(state, setLoading());
    },
    setTableLoadingState(){
      patchState(state, setTableLoading());
    },
    setLoadedState(){
      patchState(state, setLoaded());
    },
    setTableLoadedState(){
      patchState(state, setTableLoaded());
    },
    setErrorState(error:string){
      patchState(state, setError(error));
    },
    resetSearchResults(){
      patchState(state, resetSearchState());
      patchState(state,{
        searchResults:[],
        totalCount:0,
      });
    },
    setSearchResults(searchResults:User[],totalCount:number){
      patchState(state,{
        searchResults,
        totalCount,
      })
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
    const {userRepo,httpUtil,utilService, translate} = state;
    return ({
      searchUsers: rxMethod<UserSearchRequest>(
        pipe(
          tap(() => {
            state.setLoadingState();
            state.setTableLoadingState();
          }),
          switchMap((request)=>
            userRepo.searchUsers(request).pipe(
              tapResponse({
                next:({list,countRows})=>{
                  state.setLoadedState();
                  state.setTableLoadedState();
                  state.setSearchResults(list,countRows);
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                  state.setTableLoadedState();
                  utilService.showMessage("error",translate.instant('SEARCH.ERRORS.search-failed'))
                }
              })
            )
          )
        )
      ),
      exportUsersToCsv:rxMethod<UserSearchRequest>(
        pipe(
          tap(() => {
            state.setLoadingState();
            state.setTableLoadingState();
          }),
          switchMap((request)=>
            userRepo.exportUsersToCsv(request).pipe(
              map((responseData: HttpResponse<ArrayBuffer>) =>({
                filename: httpUtil.getFileNameForContentDisposition(responseData.headers),
                mimeType: responseData.headers.get('Content-Type')!,
                arrayBuffer: responseData.body!,
                id: 0, // Just a random number...
              })),
              tapResponse({
                next:(fileData:GenericFile)=>{
                  utilService.triggerFileDownLoad(fileData.arrayBuffer, fileData.mimeType!, fileData.filename);
                  state.setLoadedState();
                  state.setTableLoadedState();
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                  state.setTableLoadedState();
                }
              })
            )
          )
        )
      ),
      getUserById: rxMethod<string>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap((id)=>
            userRepo.getUserById(id).pipe(
              tapResponse({
                next:(result)=>{
                  console.log(result)
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
                  state.setSelectedUser(result)
                  state.setCreatedUserId(result.publicId);
                  state.setLoadedState();
                  utilService.showMessage('success','User Successfully Registered');
                },
                error: (error:string) =>{
                  state.setErrorState(error);
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
                },
                error: (error:string) =>{
                  state.setErrorState(error);
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
                },
                error: (error:string) =>{
                  state.setErrorState(error);
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
                },
                error: (error:string) =>{
                  state.setErrorState(error);
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
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                }
              })
            )
          )
        )
      ),
    })
  })
);
