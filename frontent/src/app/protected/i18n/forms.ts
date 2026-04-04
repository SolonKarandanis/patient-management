import {SortDirection} from '@models/search.model';
import {SearchTypes} from '@models/constants';

export interface I18nResourceSearchFormModel{
  searchMethod:SearchTypes;
  language: number|null;
  module:number|null;
  term:string;
  rows:number;
  first:number;
  sortField:string;
  sortOrder:SortDirection;
}
