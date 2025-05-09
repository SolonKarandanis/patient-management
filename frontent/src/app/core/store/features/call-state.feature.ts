import {computed, Signal} from '@angular/core';
import {
  SignalStoreFeature,
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

export type NamedCallStateSignals<Prop extends string> = {
  [K in Prop as `${K}Loading`]: Signal<boolean>;
} & {
  [K in Prop as `${K}Loaded`]: Signal<boolean>;
} & {
  [K in Prop as `${K}Error`]: Signal<string | null>;
};

export type CallStateSignals = {
  loading: Signal<boolean>;
  loaded: Signal<boolean>;
  error: Signal<string | null>
}

export function getCallStateKeys(config?: { collection?: string }) {
  const prop = config?.collection;
  return {
    loadingKey: prop ? `${config.collection}Loading` : 'loading',
    loadedKey: prop ? `${config.collection}Loaded` : 'loaded',
    errorKey: prop ? `${config.collection}Error` : 'error',
  };
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
