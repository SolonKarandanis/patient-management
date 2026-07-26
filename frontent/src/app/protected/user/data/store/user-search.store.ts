import {patchState, signalStore, withMethods, withProps, withState} from '@ngrx/signals';
import {initialUserSearchState, UserSearchState} from './user-search.state';
import {inject} from '@angular/core';
import {UserRepository} from '../repositories/user.repository';
import {UtilService} from '@core/services/util.service';
import {HttpUtil} from '@core/services/http-util.service';
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
import {User} from '@models/user.model';

export const UserSearchStore = signalStore(
  {providedIn:'root'},
  withState<UserSearchState>(initialUserSearchState),
  withCallState(),
  withSearchState(),
  withProps(()=>({
    userRepo:inject(UserRepository),
    utilService:inject(UtilService),
    httpUtil:inject(HttpUtil),
    translate:inject(TranslateService),
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
    })
  })
);
