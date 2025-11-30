import {I18nResource, Language} from '@models/i18n-resource.model';

export type I18nState ={
  readonly languages: Language[];
  readonly modules: Record<number,string>| null;
  readonly searchResults: I18nResource[];
  readonly totalCount:number;
}

export const initialResourceState: I18nState ={
  languages:[],
  modules: null,
  searchResults:[],
  totalCount: 0
}
