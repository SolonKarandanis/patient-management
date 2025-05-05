import {ChangeDetectionStrategy, Component, inject, input, output, TemplateRef} from '@angular/core';
import {AuthService} from '@core/services/auth.service';
import {SavedSearch, SearchTypeEnum} from '@models/search.model';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'app-search-buttons',
  imports: [],
  template: `
    <p>
      search-buttons works!
    </p>
  `,
  styleUrl: './search-buttons.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchButtonsComponent {

  private authService= inject(AuthService);

  protected saveSearchTitle = '';

  isLoading = input(false);
  isDisabled = input(false);
  searchType = input.required<SearchTypeEnum>();
  searchForm = input.required<FormGroup>();
  resetBtnTemplate = input.required<TemplateRef<Record<string, unknown>>>();

  searchClicked = output<MouseEvent>();
  resetClicked = output<MouseEvent>();
  saveSearchClicked = output<SavedSearch>();

  protected handleSearchClick(event: MouseEvent): void{
    this.searchClicked.emit(event);
  }
  protected handleResetClick(event: MouseEvent): void{
    this.resetClicked.emit(event);
  }

  protected handleSaveSearchClick(): void{
    const savedSearchName = this.saveSearchTitle;
    const searchType = this.searchType;
    const username = this.authService.getUsername() as string;
    // const criteria = this.searchService.getCriteria(this.searchType,this.searchForm);


    // const savedSearch: SavedSearchModel = {
    //   savedSearchName,
    //   searchType,
    //   username,
    //   criteria,
    // };
    // this.saveSearchClicked.emit(savedSearch);
    this.resetSearchTitle();
  }
  private resetSearchTitle(): void {
    this.saveSearchTitle = '';
  }
}
