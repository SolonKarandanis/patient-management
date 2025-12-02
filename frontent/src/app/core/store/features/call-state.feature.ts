import {computed, Signal} from '@angular/core';
import {
  SignalStoreFeature,
  signalStoreFeature,
  withComputed,
  withState,
} from '@ngrx/signals';

export type CallState = {
  status: 'pending' | 'loading' | 'loaded' | { error: string };
};

export const initialCallState: CallState = {
  status: 'pending',
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
    withComputed(({ status }) => ({
      loading: computed(() => status() === 'loading'),
      loaded: computed(() => status() === 'loaded'),
      error: computed(() => {
        const s = status();
        return typeof s === 'object' ? s.error : null
      }),
      status: computed(() => {
        const s = status();
        return typeof s === 'object' ? 'error' : s;
      }),
    }))
  );
}

export function setLoading(): { status: 'loading' } {
  return { status: 'loading' };
}

export function setLoaded(): { status: 'loaded' } {
  return { status: 'loaded' };
}

export function setError(error: string): { status: { error: string } } {
  return { status: { error } };
}
