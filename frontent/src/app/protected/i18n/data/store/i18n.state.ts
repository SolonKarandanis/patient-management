import {I18nResource} from '@models/i18n-resource.model';

export type I18nState ={
  readonly languages: string[];
  readonly modules: string[];
  readonly searchResults: I18nResource[];
  readonly totalCount:number;
}

export const initialResourceState: I18nState ={
  languages:[],
  modules: [],
  searchResults:[],
  totalCount: 0
}
