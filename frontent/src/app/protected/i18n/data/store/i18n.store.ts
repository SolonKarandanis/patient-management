import {signalStore, withProps, withState} from '@ngrx/signals';
import {withCallState} from '@core/store/features/call-state.feature';
import {I18nState, initialResourceState} from './i18n.state';
import {withSearchState} from '@core/store/features/search-state.feature';
import {inject} from '@angular/core';
import {I18nTranslationRepository} from '../repositories/i18n-translation.repository';

export const I18nResourceStore = signalStore(
  {providedIn:'root'},
  withState<I18nState>(initialResourceState),
  withCallState(),
  withSearchState(),
  withProps(()=>({
    i18nRepo:inject(I18nTranslationRepository)
  })),
);
