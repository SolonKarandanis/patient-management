import {computed, Resource, ResourceStatus} from '@angular/core';
import {CallStateSignals} from './call-state.feature';

/**
 * Maps any Angular `Resource` onto the existing `CallStateSignals` shape, so
 * resource-backed store fields expose the same loading/loaded/error/status
 * API that `withCallState()`-based stores already use.
 */
export function resourceCallState(resource: Resource<unknown>): CallStateSignals {
  return {
    loading: computed(() => resource.isLoading()),
    loaded: computed(() => resource.status() === 'resolved' || resource.status() === 'local'),
    error: computed(() => resource.error()?.message ?? null),
    status: computed(() => deriveResourceStatus(resource.status())),
  };
}

function deriveResourceStatus(status: ResourceStatus): 'pending' | 'loading' | 'loaded' | 'error' {
  switch (status) {
    case 'error':
      return 'error';
    case 'loading':
    case 'reloading':
      return 'loading';
    case 'resolved':
    case 'local':
      return 'loaded';
    default:
      return 'pending';
  }
}

/**
 * Spreads a `CallStateSignals` group into `${prop}Loading`/`${prop}Loaded`/
 * `${prop}Error`/`${prop}Status` keys, matching the `NamedCallStateSignals<Prop>`
 * naming convention already defined in `call-state.feature.ts`.
 */
export function prefixedCallState<Prop extends string>(
  prop: Prop,
  signals: CallStateSignals,
) {
  return {
    [`${prop}Loading`]: signals.loading,
    [`${prop}Loaded`]: signals.loaded,
    [`${prop}Error`]: signals.error,
    [`${prop}Status`]: signals.status,
  } as {
    [K in Prop as `${K}Loading`]: CallStateSignals['loading'];
  } & {
    [K in Prop as `${K}Loaded`]: CallStateSignals['loaded'];
  } & {
    [K in Prop as `${K}Error`]: CallStateSignals['error'];
  } & {
    [K in Prop as `${K}Status`]: CallStateSignals['status'];
  };
}
