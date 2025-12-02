import {computed, Signal} from '@angular/core';
import {
  SignalStoreFeature,
  signalStoreFeature,
  withComputed,
  withState,
} from '@ngrx/signals';

export type CallState = {
  callStateStatus: 'pending' | 'loading' | 'loaded' | { error: string };
};

export const initialCallState: CallState = {
  callStateStatus: 'pending',
};

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
  error: Signal<string | null>;
  status: Signal<'pending' | 'loading' | 'loaded' | 'error'>;
};

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
    withComputed(({ callStateStatus }) => ({
      loading: computed(() => callStateStatus() === 'loading'),
      loaded: computed(() => callStateStatus() === 'loaded'),
      error: computed(() => {
        const s = callStateStatus();
        return typeof s === 'object' ? s.error : null
      }),
      status: computed(() => {
        const s = callStateStatus();
        return typeof s === 'object' ? 'error' : s;
      }),
    }))
  );
}

export function setLoading(): { callStateStatus: 'loading' } {
  return { callStateStatus: 'loading' };
}

export function setLoaded(): { callStateStatus: 'loaded' } {
  return { callStateStatus: 'loaded' };
}

export function setError(error: string): { callStateStatus: { error: string } } {
  return { callStateStatus: { error } };
}
