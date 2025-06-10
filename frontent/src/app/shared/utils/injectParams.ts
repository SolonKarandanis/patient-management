import {assertInInjectionContext, inject, Signal} from '@angular/core';
import {ActivatedRoute, Params} from '@angular/router';
import {toSignal} from '@angular/core/rxjs-interop';
import {map} from 'rxjs';

export function injectParams(key?: string): Signal<Params| string> {
  assertInInjectionContext(injectParams);
  const route = inject(ActivatedRoute);
  const getParam = (params: Params) => key ? params?.[key] ?? null : params;
  return toSignal(route.params.pipe(map(getParam)), { requireSync: true });
}
