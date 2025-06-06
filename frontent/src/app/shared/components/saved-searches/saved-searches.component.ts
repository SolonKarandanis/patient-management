import {ChangeDetectionStrategy, Component, inject, input, OnInit, output} from '@angular/core';
import {Confirmation, ConfirmationService} from 'primeng/api';
import {SearchService} from '@core/services/search.service';
import {TranslateService} from '@ngx-translate/core';
import {SavedSearch, SearchType} from '@models/search.model';


@Component({
  selector: 'app-saved-searches',
  imports: [],
  template: `
    <p>
      saved-searches works!
    </p>
  `,
  styleUrl: './saved-searches.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SavedSearchesComponent implements OnInit{
  private confirmationService= inject(ConfirmationService);
  private searchService= inject(SearchService);
  private translate= inject(TranslateService);

  protected selectedSavedSearch!: SavedSearch;

  searchType = input.required<SearchType>();

  loadSavedSearch = output<SavedSearch>();

  ngOnInit(): void {
    this.getSavedSearches();
  }

  protected handleLoadClick(): void{
    this.loadSavedSearch.emit(this.selectedSavedSearch);
  }

  protected handleDeleteClick(): void{
    // const confirmationConf: Confirmation = {
    //   message: this.translate.instant('ADVANCED-SEARCH.SAVED-SEARCHES.CONFIRM-DIALOG.message'),
    //   accept: () => {
    //     this.toggleLoadingForDeleteBtn();
    //     const deletedId: number = this.selectedSavedSearch.id!;
    //
    //     this.searchService.deleteSavedSearch(deletedId).subscribe({
    //       next: (savedSearches: SavedSearch[]) => this.afterConfirmDelete(savedSearches),
    //       error: () => this.toggleLoadingForDeleteBtn(),
    //     });
    //   },
    //   acceptLabel: this.translate.instant('GLOBAL.BUTTONS.yes'),
    //   rejectLabel: this.translate.instant('GLOBAL.BUTTONS.no'),
    // };
    // this.confirmationService.confirm(confirmationConf);
  }

  private getSavedSearches(): void{

  }

}
