import {I18nResource} from '@models/i18n-resource.model';

export type I18nResourceSearchState = {
  readonly searchResults: I18nResource[];
  readonly totalCount: number;
}

export const initialI18nResourceSearchState: I18nResourceSearchState = {
  searchResults: [],
  totalCount: 0,
};
