import { computed } from '@angular/core';
import {
  signalStoreFeature,
  withComputed,
  withState,
} from '@ngrx/signals';

export type CallState ={
  isLoading:boolean,
  errorMessage:string|null;
  showError:boolean;
};

export const initialCallState: CallState={
  isLoading:false,
  errorMessage:null,
  showError:false,
}

export function withCallState() {
  return signalStoreFeature(
    withState<CallState>(initialCallState),
    withComputed(({isLoading,errorMessage}) => ({
      loading: computed(() => isLoading()=== true),
      loaded: computed(() => isLoading() == false),
      error: computed(() => errorMessage()),
    }))
  );
}

export function setLoading(): CallState {
  return { isLoading: true, errorMessage: null, showError:false};
}

export function setLoaded(): CallState {
  return { isLoading: false, errorMessage: null, showError:false};
}

export function setError(errorMessage: string): CallState {
  return { isLoading: false, errorMessage, showError:true};
}
