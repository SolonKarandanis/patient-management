import {patchState, signalStore, withComputed, withMethods, withProps, withState} from '@ngrx/signals';
import {setError, setLoaded, setLoading, withCallState} from '@core/store/features/call-state.feature';
import {initialI18nLookupState, I18nLookupState} from './i18n-lookup.state';
import {computed, inject} from '@angular/core';
import {I18nTranslationRepository} from '../repositories/i18n-translation.repository';
import {Language} from '@models/i18n-resource.model';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {pipe, switchMap, tap} from 'rxjs';
import {tapResponse} from '@ngrx/operators';
import {UtilService} from '@core/services/util.service';
import {TranslateService} from '@ngx-translate/core';
import {SelectItem} from 'primeng/api';

export const I18nLookupStore = signalStore(
  {providedIn:'root'},
  withState<I18nLookupState>(initialI18nLookupState),
  withCallState(),
  withProps(()=>({
    i18nRepo:inject(I18nTranslationRepository),
    utilService:inject(UtilService),
    translate:inject(TranslateService),
  })),
  withComputed(({
    languages,
    modules
  }) => ({
    getLanguagesAsSelectItems: computed(()=>{
      const langs = languages();
      if(langs && Array.isArray(langs) && langs.length > 0){
        return langs.map((lang: Language) => {
          return {label: lang.label, value: lang.id} as SelectItem;
        });
      }
      return [];
    }),
    getModulesAsSelectItems: computed(()=>{
      const mods = modules();
      if(mods){
        return Object.entries(mods).map(([key,value]) =>({
          label:value,
          value:key
        }));
      }
      return [];
    })
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
    setModules(modules:Record<number,string>){
      patchState(state,{modules})
    },
    setLanguages(languages:Language[]){
      patchState(state,{languages})
    }
  })),
  withMethods((state)=>{
    const {i18nRepo,utilService,translate} = state;
    return ({
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
