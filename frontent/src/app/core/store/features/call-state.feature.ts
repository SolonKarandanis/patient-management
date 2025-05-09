import { computed } from '@angular/core';
import {
  signalStoreFeature,
  withComputed,
  withState,
} from '@ngrx/signals';

export type CallState ={
  loading:boolean,
  error:string|null;
  showError:boolean;
};

export const initialCallState: CallState={
  loading:false,
  error:null,
  showError:false,
}

export function withCallState() {
  return signalStoreFeature(
    withState<CallState>(initialCallState),
    withComputed(({loading,error}) => ({
      loading: computed(() => loading()=== true),
      loaded: computed(() => loading() == false),
      error: computed(() => error()),
    }))
  );
}

export function setLoading(): CallState {
  return { loading: true, error: null, showError:false};
}

export function setLoaded(): CallState {
  return { loading: false, error: null, showError:false};
}

export function setError(error: string): CallState {
  return { loading: false, error, showError:true};
}
