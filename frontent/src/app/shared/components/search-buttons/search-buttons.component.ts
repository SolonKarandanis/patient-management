import {ChangeDetectionStrategy, Component, inject, input, output, signal, TemplateRef} from '@angular/core';
import {AuthService} from '@core/services/auth.service';
import {SavedSearch, SearchType} from '@models/search.model';
import {FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {ToastService} from '@core/services/toast.service';
import {NgTemplateOutlet} from '@angular/common';
import {FieldTree} from '@angular/forms/signals';
import {HlmButtonImports} from '@components/ui/button';
import {HlmTooltipImports} from '@components/ui/tooltip';
import {HlmInputImports} from '@components/ui/input';
import {HlmSpinnerImports} from '@components/ui/spinner';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {lucideRefreshCw, lucideSave, lucideSearch} from '@ng-icons/lucide';

@Component({
  selector: 'app-search-buttons',
  imports: [
    TranslatePipe,
    ReactiveFormsModule,
    FormsModule,
    NgTemplateOutlet,
    HlmButtonImports,
    HlmTooltipImports,
    HlmInputImports,
    HlmSpinnerImports,
    NgIcon,
  ],
  providers: [provideIcons({lucideSearch, lucideSave, lucideRefreshCw})],
  template: `
    <div class="grid sm:grid-cols-1 md:grid-cols-4 lg:grid-cols-10 xl:grid-cols-12 gap-4">
      <div class="sm:col-span-1 md:col-span-1 lg:col-span-2">
        <button
            class="w-full"
            hlmBtn
            type="button"
            variant="default"
            (click)="handleSearchClick($event)"
            [disabled]="isDisabled() || isLoading()">
          @if (isLoading()) {
            <hlm-spinner class="mr-2" />
          } @else {
            <ng-icon name="lucideSearch" />
          }
          {{'GLOBAL.BUTTONS.search' | translate}}
        </button>
      </div>
      <div class="sm:col-span-1 md:col-span-2 lg:col-span-6 xl:col-span-8">
        @if (enableSaveSearch()){
          <div class="grid sm:grid-cols-1 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-12 gap-4">
            <div class="col-span-2 sm:col-span-1 md:col-span-2 lg:col-span-2 xl:col-span-4">
              <span
                class="w-full"
                [hlmTooltip]="'SAVED-SEARCHES.LABELS.enter-title-first' | translate"
                [tooltipDisabled]="!!saveSearchTitle()">
                <button
                  class="w-full"
                  hlmBtn
                  variant="secondary"
                  type="button"
                  [disabled]="!saveSearchTitle() || saveSearchLoading()"
                  (click)="handleSaveSearchClick()">
                  @if (saveSearchLoading()) {
                    <hlm-spinner class="mr-2" />
                  } @else {
                    <ng-icon name="lucideSave" />
                  }
                  {{'GLOBAL.BUTTONS.save-search' | translate}}
                </button>
              </span>
            </div>
            <div class="col-span-3 sm:col-span-1 md:col-span-2 lg:col-span-3 xl:col-span-5">
              <label for="saveSearchTitle" class="block uppercase text-blueGray-600 text-xs font-bold mb-2">
                {{ 'SAVED-SEARCHES.LABELS.with-title' | translate }}:
              </label>
              <input
                id="saveSearchTitle"
                hlmInput
                type="text"
                class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                [(ngModel)]="saveSearchTitle"
                autocomplete="saved-searches"/>
            </div>
          </div>
        }
      </div>
      <div class="sm:col-span-1 md:col-span-1 lg:col-span-2">
        <button
          class="w-full"
          hlmBtn
          variant="destructive"
          type="button"
          [disabled]="saveSearchLoading()"
          (click)="handleResetClick($event)">
            @if (saveSearchLoading()) {
              <hlm-spinner class="mr-2" />
            } @else {
              <ng-icon name="lucideRefreshCw" />
            }
            <ng-container *ngTemplateOutlet="resetBtnTemplate() || defaultResetBtnLabel"></ng-container>
            <ng-template #defaultResetBtnLabel>
              {{ "GLOBAL.BUTTONS.reset" | translate }}
            </ng-template>
        </button>
      </div>
    </div>
  `,
  styleUrl: './search-buttons.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchButtonsComponent<T> {

  private authService= inject(AuthService);
  private toastService= inject(ToastService);
  private translate= inject(TranslateService);

  protected saveSearchTitle = signal('');
  protected saveSearchLoading = signal(false);

  isLoading = input(false);
  isDisabled = input(false);
  enableSaveSearch = input(false);
  searchType = input.required<SearchType>();
  searchForm = input.required<FieldTree<T extends Record<any, any> ? T : never>>();
  resetBtnTemplate = input<TemplateRef<Record<string, unknown>>>();

  searchClicked = output<MouseEvent>();
  resetClicked = output<MouseEvent>();
  saveSearchClicked = output<SavedSearch>();

  protected handleSearchClick(event: MouseEvent): void{
    this.searchClicked.emit(event);
  }
  protected handleResetClick(event: MouseEvent): void{
    this.resetSearchTitle();
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
    this.saveSearchTitle.set('');
  }

  private showSuccessMessage():void{
    const detailMsg = `${this.translate.instant('ADVANCED-SEARCH.SAVED-SEARCHES.MESSAGES.detail-success')} ${this.saveSearchTitle}`;
    this.toastService.success(detailMsg, this.translate.instant('ADVANCED-SEARCH.SAVED-SEARCHES.MESSAGES.summary'));
  }
}
