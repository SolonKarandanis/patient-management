import {FormControl} from '@angular/forms';
import {SortDirection} from '@models/search.model';

export interface I18nResourceSearchForm{
  language: FormControl<number|null|undefined>;
  module: FormControl<number|null|undefined>;
  term: FormControl<string|null|undefined>;
  rows:FormControl<number>;
  first:FormControl<number>;
  sortField:FormControl<string>;
  sortOrder:FormControl<SortDirection>;
}
