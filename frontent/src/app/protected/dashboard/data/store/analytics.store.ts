import {patchState, signalStore, withMethods, withProps, withState} from '@ngrx/signals';
import {AnalyticsState, initialAnalyticsState} from './analytics.state';
import {inject} from '@angular/core';
import {UtilService} from '@core/services/util.service';
import {TranslateService} from '@ngx-translate/core';
import {AnalyticsRepository} from '../repositories/analytics.repository';
import {setError, setLoaded, setLoading, withCallState} from '@core/store/features/call-state.feature';
import {DailyEventCount, DailyPaymentSummary} from '@models/analytics.model';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {pipe, switchMap, tap} from 'rxjs';
import {tapResponse} from '@ngrx/operators';
import {Translation} from '@models/i18n-resource.model';


export const AnalyticsStore = signalStore(
  {providedIn:'root'},
  withState<AnalyticsState>(initialAnalyticsState),
  withCallState(),
  withProps(()=>({
    analyticsRepo:inject(AnalyticsRepository),
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
    setPatientsDailySummary(summary:DailyEventCount[]){
      patchState(state,{patientsDailySummary:summary});
    },
    setUserDailySummary(summary:DailyEventCount[]){
      patchState(state,{userDailySummary:summary});
    },
    setPaymentDailySummary(summary:DailyPaymentSummary[]){
      patchState(state,{paymentDailySummary:summary});
    }
  })),
  withMethods((state)=>{
    const {analyticsRepo,utilService,translate} = state;
    return ({
      getPatientDailySummary:rxMethod<void>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap((request)=>
            analyticsRepo.getPatientDailySummary().pipe(
              tapResponse({
                next:(result)=>{
                  state.setLoadedState();
                  state.setPatientsDailySummary(result);
                },
                error: (error:string) =>{
                  state.setLoadedState();
                  state.setErrorState(error);
                  utilService.showMessage("error",translate.instant('SEARCH.ERRORS.search-failed'))
                }
              })
            )
          )
        )
      ),
      getUserDailySummary:rxMethod<void>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap((request)=>
            analyticsRepo.getUserDailySummary().pipe(
              tapResponse({
                next:(result)=>{
                  state.setLoadedState();
                  state.setUserDailySummary(result);
                },
                error: (error:string) =>{
                  state.setLoadedState();
                  state.setErrorState(error);
                  utilService.showMessage("error",translate.instant('SEARCH.ERRORS.search-failed'))
                }
              })
            )
          )
        )
      ),
      getPaymentDailySummary:rxMethod<void>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap((request)=>
            analyticsRepo.getPaymentDailySummary().pipe(
              tapResponse({
                next:(result)=>{
                  state.setLoadedState();
                  state.setPaymentDailySummary(result);
                },
                error: (error:string) =>{
                  state.setLoadedState();
                  state.setErrorState(error);
                  utilService.showMessage("error",translate.instant('SEARCH.ERRORS.search-failed'))
                }
              })
            )
          )
        )
      ),
    });
  }),
);
