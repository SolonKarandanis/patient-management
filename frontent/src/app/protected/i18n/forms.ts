import {SortDirection} from '@models/search.model';

export interface I18nResourceSearchFormModel{
  language: number|null;
  module:number|null;
  term:string;
  rows:number;
  first:number;
  sortField:string;
  sortOrder:SortDirection;
}
