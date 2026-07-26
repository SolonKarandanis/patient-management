import {patchState, signalStore, withMethods, withProps} from '@ngrx/signals';
import {setError, setLoaded, setLoading, withCallState} from '@core/store/features/call-state.feature';
import {inject} from '@angular/core';
import {I18nTranslationRepository} from '../repositories/i18n-translation.repository';
import {I18nResource, UpdateI18nResource} from '@models/i18n-resource.model';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {pipe, switchMap, tap} from 'rxjs';
import {tapResponse} from '@ngrx/operators';
import {UtilService} from '@core/services/util.service';
import {TranslateService} from '@ngx-translate/core';

export const I18nResourceDetailStore = signalStore(
  {providedIn:'root'},
  withCallState(),
  withProps(()=>({
    i18nRepo:inject(I18nTranslationRepository),
    utilService:inject(UtilService),
    translate:inject(TranslateService),
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
  })),
  withMethods((state)=>{
    const {i18nRepo,utilService,translate} = state;
    return ({
      updateTranslations: rxMethod<{updates:UpdateI18nResource[],row:I18nResource}>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap(({updates,row})=>
            i18nRepo.updateTranslations(updates).pipe(
              tapResponse({
                next:()=>{
                  state.setLoadedState();
                  delete row._translationList;
                  row.editing = false;
                  utilService.showMessage('success',translate.instant('ADMINISTRATION.I18N-MANAGEMENT.MESSAGES.SUCCESS.update-success'));
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                  state.setLoadedState();
                  row.translationList = row._translationList ? [...row._translationList] : [];
                  row.editing = true;
                  utilService.showMessage("error",translate.instant('ADMINISTRATION.I18N-MANAGEMENT.MESSAGES.ERROR.update-failure'))
                }
              })
            )
          )
        )
      ),
    });
  })
);
