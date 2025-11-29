import {patchState, signalStore, withMethods, withProps, withState} from '@ngrx/signals';
import {setError, setLoaded, setLoading, withCallState} from '@core/store/features/call-state.feature';
import {I18nState, initialResourceState} from './i18n.state';
import {
  resetSearchState,
  setTableLoaded,
  setTableLoading,
  withSearchState
} from '@core/store/features/search-state.feature';
import {inject} from '@angular/core';
import {I18nTranslationRepository} from '../repositories/i18n-translation.repository';
import {I18nResource, UpdateI18nResource} from '@models/i18n-resource.model';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {I18nResourceSearchRequest} from '@models/search.model';
import {pipe, switchMap, tap} from 'rxjs';
import {tapResponse} from '@ngrx/operators';
import {UtilService} from '@core/services/util.service';
import {TranslateService} from '@ngx-translate/core';

export const I18nResourceStore = signalStore(
  {providedIn:'root'},
  withState<I18nState>(initialResourceState),
  withCallState(),
  withSearchState(),
  withProps(()=>({
    i18nRepo:inject(I18nTranslationRepository),
    utilService:inject(UtilService),
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
    setSearchResults(searchResults:I18nResource[],totalCount:number){
      patchState(state,{
        searchResults,
        totalCount,
      })
    },
    setModules(modules:string[]){
      patchState(state,{modules})
    },
    setLanguages(languages:string[]){
      patchState(state,{languages})
    }
  })),
  withMethods((state)=>{
    const {i18nRepo,utilService,translate} = state;
    return ({
      searchResources: rxMethod<I18nResourceSearchRequest>(
        pipe(
          tap(() => {
            state.setLoadingState();
            state.setTableLoadingState();
          }),
          switchMap((request)=>
            i18nRepo.searchResources(request).pipe(
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
      updateTranslations: rxMethod<UpdateI18nResource[]>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap((request)=>
            i18nRepo.updateTranslations(request).pipe(
              tapResponse({
                next:()=>{
                  state.setLoadedState();
                  utilService.showMessage('success',translate.instant('ADMINISTRATION.I18N-MANAGEMENT.MESSAGES.SUCCESS.update-success'));
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                  state.setLoadedState();
                  utilService.showMessage("error",translate.instant('ADMINISTRATION.I18N-MANAGEMENT.MESSAGES.ERROR.update-failure'))
                }
              })
            )
          )
        )
      ),
      getModules: rxMethod<void>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap(()=>
            i18nRepo.getModules().pipe(
              tapResponse({
                next:(result)=>{
                  state.setLoadedState();
                  state.setModules(result)
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                  state.setLoadedState();
                  utilService.showMessage("error",translate.instant('ADMINISTRATION.I18N-MANAGEMENT.MESSAGES.ERROR.get-modules'))
                }
              })
            )
          )
        )
      ),
      getLanguages: rxMethod<void>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap(()=>
            i18nRepo.getLanguages().pipe(
              tapResponse({
                next:(result)=>{
                  state.setLoadedState();
                  state.setLanguages(result)
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                  state.setLoadedState();
                  utilService.showMessage("error",translate.instant('ADMINISTRATION.I18N-MANAGEMENT.MESSAGES.ERROR.get-languages'))
                }
              })
            )
          )
        )
      ),
    });
  })
);
