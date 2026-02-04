import {patchState, signalStore, withMethods, withProps, withState} from '@ngrx/signals';
import {AnalyticsState, initialAnalyticsState} from './analytics.state';
import {inject} from '@angular/core';
import {UtilService} from '@core/services/util.service';
import {TranslateService} from '@ngx-translate/core';
import {AnalyticsRepository} from '../repositories/analytics.repository';
import {setError, setLoaded, setLoading, withCallState} from '@core/store/features/call-state.feature';


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

  })),
);
