import {ChangeDetectionStrategy, Component, inject, input, output, signal, TemplateRef} from '@angular/core';
import {AuthService} from '@core/services/auth.service';
import {SavedSearch, SearchType} from '@models/search.model';
import {FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ButtonDirective} from 'primeng/button';
import {Ripple} from 'primeng/ripple';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {MessageService} from 'primeng/api';
import {SearchService} from '@core/services/search.service';
import {Tooltip} from 'primeng/tooltip';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {NgTemplateOutlet} from '@angular/common';
import {FieldTree} from '@angular/forms/signals';

@Component({
  selector: 'app-search-buttons',
  imports: [
    ButtonDirective,
    Ripple,
    TranslatePipe,
    Tooltip,
    FloatLabel,
    InputText,
    ReactiveFormsModule,
    FormsModule,
    NgTemplateOutlet
  ],
  template: `
    <div class="grid sm:grid-cols-1 md:grid-cols-4 lg:grid-cols-10 xl:grid-cols-12 gap-4">
      <div class="sm:col-span-1 md:col-span-1 lg:col-span-2">
        <button
            class="w-full"
            pButton
            pRipple
            type="submit"
            severity="info"
            (click)="handleSearchClick($event)"
            [disabled]="isDisabled() || isLoading()"
            [loading]="isLoading()"
            icon="pi pi-search">
          {{'GLOBAL.BUTTONS.search' | translate}}
        </button>
      </div>
      <div class="sm:col-span-1 md:col-span-2 lg:col-span-6 xl:col-span-8">
        @if (enableSaveSearch()){
          <div class="grid sm:grid-cols-1 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-12 gap-4">
            <div class="col-span-2 sm:col-span-1 md:col-span-2 lg:col-span-2 xl:col-span-4">
              <span class="w-full"
                    pTooltip="{{'SAVED-SEARCHES.LABELS.enter-title-first' | translate}}"
                    [tooltipDisabled]="!!saveSearchTitle()">
                <button
                  class="w-full"
                  pButton
                  pRipple
                  type="button"
                  severity="success"
                  [disabled]="!saveSearchTitle() || saveSearchLoading()"
                  [loading]="saveSearchLoading()"
                  icon="pi pi-save"
                  (click)="handleSaveSearchClick()">
                  {{'GLOBAL.BUTTONS.save-search' | translate}}
                </button>
              </span>
            </div>
            <div class="col-span-3 sm:col-span-1 md:col-span-2 lg:col-span-3 xl:col-span-5">
              <p-float-label variant="on" class="w-full mb-3">
                <input
                  id="saveSearchTitle"
                  pInputText
                  type="text"
                  class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                  [(ngModel)]="saveSearchTitle"
                  autocomplete="saved-searches"/>
                <label for="saveSearchTitle">{{ 'SAVED-SEARCHES.LABELS.with-title' | translate }}:</label>
              </p-float-label>
            </div>
          </div>
        }
      </div>
      <div class="sm:col-span-1 md:col-span-1 lg:col-span-2">
        <button
          class="w-full"
          pButton
          pRipple
          type="button"
          severity="danger"
          [disabled]="saveSearchLoading()"
          [loading]="saveSearchLoading()"
          icon="pi pi-refresh"
          (click)="handleResetClick($event)">
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
  private searchService= inject(SearchService);
  private messageService= inject(MessageService);
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
    this.messageService.add({
      summary: this.translate.instant('ADVANCED-SEARCH.SAVED-SEARCHES.MESSAGES.summary'),
      detail:detailMsg,
      severity:'success'
    });
  }
}
