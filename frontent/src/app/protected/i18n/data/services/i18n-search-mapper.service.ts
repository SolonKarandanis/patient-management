import {Injectable} from '@angular/core';
import {I18nResourceSearchRequest} from '@models/search.model';
import {I18nResourceSearchFormModel} from '../../forms';
import {FieldTree} from '@angular/forms/signals';

@Injectable({
  providedIn: 'root'
})
export class I18nSearchMapperService {

  public toI18nResourceSearchRequest(form: FieldTree<I18nResourceSearchFormModel, string | number>): I18nResourceSearchRequest {
    const {language, term, module, rows, first, sortField, sortOrder} = form;
    return {
      searchMethod: form.searchMethod().value(),
      languageId: language().value()!,
      term: term().value(),
      moduleId: module().value()!,
      paging: {
        limit: rows().value(),
        page: first().value(),
        sortField: sortField().value(),
        sortDirection: sortOrder().value(),
      }
    };
  }
}
