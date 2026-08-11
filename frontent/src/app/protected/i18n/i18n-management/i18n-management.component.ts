import {ChangeDetectionStrategy, Component, effect, inject, signal, WritableSignal} from '@angular/core';
import {I18nTranslationService} from '../data/services/i18n-translation.service';
import {CommonEntitiesService} from '@core/services/common-entities.service';
import {PageHeaderComponent} from '@components/page-header/page-header.component';
import {TranslatePipe} from '@ngx-translate/core';
import {FieldsetComponent} from '@components/fieldset/fieldset.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SearchButtonsComponent} from '@components/search-buttons/search-buttons.component';
import {SearchType, SearchTypeEnum, SortDirectionEnum} from '@models/search.model';
import {I18nResource, UpdateI18nResource} from '@models/i18n-resource.model';
import {I18nResourceSearchFormModel} from '../forms';
import {Field, FieldTree} from '@angular/forms/signals';
import {HlmInputImports} from '@components/ui/input';
import {HlmSelectImports} from '@components/ui/select';
import {HlmTableImports} from '@components/ui/table';
import {HlmPaginationImports} from '@components/ui/pagination';
import {HlmTextareaImports} from '@components/ui/textarea';
import {HlmButtonImports} from '@components/ui/button';
import {HlmSpinnerImports} from '@components/ui/spinner';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {lucideArrowDown, lucideArrowUp, lucideArrowUpDown, lucideCheck, lucidePencil, lucideX} from '@ng-icons/lucide';

@Component({
  selector: 'app-i18n-management',
  imports: [
    PageHeaderComponent,
    TranslatePipe,
    FieldsetComponent,
    FormsModule,
    ReactiveFormsModule,
    SearchButtonsComponent,
    Field,
    HlmInputImports,
    HlmSelectImports,
    HlmTableImports,
    HlmPaginationImports,
    HlmTextareaImports,
    HlmButtonImports,
    HlmSpinnerImports,
    NgIcon,
  ],
  providers: [provideIcons({lucideArrowDown, lucideArrowUp, lucideArrowUpDown, lucideCheck, lucidePencil, lucideX})],
  template: `
    <div class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-blueGray-100 border-0 text-black">
      <app-page-header>
        {{ 'HEADER.MENU.ADMINISTRATION.i18n-management' | translate }}
      </app-page-header>
      <div class="flex-auto px-4 lg:px-10 py-10 pt-0">
        <div role="search">
          <app-fieldset legend="{{ 'ADMINISTRATION.I18N-MANAGEMENT.search-resources' | translate }}"
                        [toggleable]="true"
                        [collapsed]="criteriaCollapsed()">
            <form >
              <div class="grid gap-6 mt-6 md:grid-cols-3">
                <div class="mb-6">
                  <label class="app-required-label block uppercase text-blueGray-600 text-xs font-bold mb-2" for="module">
                    {{ 'ADMINISTRATION.I18N-MANAGEMENT.search-module' | translate }}
                  </label>
                  <hlm-select
                    [value]="form.module().value()"
                    (valueChange)="form.module().value.set($event ?? null)"
                    class="border-0 !bg-white text-sm shadow w-full">
                    <hlm-select-trigger class="w-full">
                      <hlm-select-value />
                    </hlm-select-trigger>
                    <hlm-select-content *hlmSelectPortal>
                      <hlm-select-group>
                        @for (module of modules(); track module.value) {
                          <hlm-select-item [value]="module.value">{{ module.label }}</hlm-select-item>
                        }
                      </hlm-select-group>
                    </hlm-select-content>
                  </hlm-select>
                </div>
                <div class="mb-6">
                  <label class="app-required-label block uppercase text-blueGray-600 text-xs font-bold mb-2" for="language">
                    {{ 'ADMINISTRATION.I18N-MANAGEMENT.search-language' | translate }}
                  </label>
                  <hlm-select
                    [value]="form.language().value()"
                    (valueChange)="form.language().value.set($event ?? null)"
                    class="border-0 !bg-white text-sm shadow w-full">
                    <hlm-select-trigger class="w-full">
                      <hlm-select-value />
                    </hlm-select-trigger>
                    <hlm-select-content *hlmSelectPortal>
                      <hlm-select-group>
                        @for (language of languages(); track language.value) {
                          <hlm-select-item [value]="language.value">{{ language.label }}</hlm-select-item>
                        }
                      </hlm-select-group>
                    </hlm-select-content>
                  </hlm-select>
                </div>
                <div class="mb-6">
                  <label class="block uppercase text-blueGray-600 text-xs font-bold mb-2" for="term">
                    {{ 'ADMINISTRATION.I18N-MANAGEMENT.search-term' | translate }}
                  </label>
                  <input
                    id="term"
                    hlmInput
                    type="text"
                    class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                    [field]="form.term"
                    autocomplete="term"/>
                </div>
              </div>
              <app-search-buttons #searchBtns
                                  [searchType]="searchType"
                                  [enableSaveSearch]="true"
                                  [searchForm]="form"
                                  (searchClicked)="search()"
                                  (resetClicked)="resetForm()"/>
            </form>
          </app-fieldset>
          @if (resultsVisible()) {
            <div class="mt-6" [class.fade-in]="hasSearched()" [class.fade-out]="!hasSearched()">
              <div class="relative">
                <table hlmTable>
                  <thead hlmTHead>
                    <tr hlmTr>
                      <th hlmTh scope="col" class="w-[16%] bg-blueGray-100">
                        <button type="button" class="inline-flex items-center gap-1 font-medium" (click)="handleSort('key')">
                          <span>{{ 'ADMINISTRATION.I18N-MANAGEMENT.TABLE.resource-key' | translate }}</span>
                          <ng-icon [name]="sortIconName('key')" />
                        </button>
                      </th>
                      <th hlmTh scope="col" class="w-[58%] bg-blueGray-100">
                        <button type="button" class="inline-flex items-center gap-1 font-medium" (click)="handleSort('value')">
                          <span>{{ 'ADMINISTRATION.I18N-MANAGEMENT.TABLE.resource-value' | translate }}</span>
                          <ng-icon [name]="sortIconName('value')" />
                        </button>
                      </th>
                      <th hlmTh scope="col" class="w-[8%] bg-blueGray-100">
                        {{ 'ADMINISTRATION.I18N-MANAGEMENT.TABLE.action' | translate }}
                      </th>
                    </tr>
                  </thead>
                  <tbody hlmTBody>
                    @for (row of results(); track row.id) {
                      <tr hlmTr>
                        <td hlmTd>{{ row.key }}</td>
                        <td hlmTd>
                          <table class="w-full">
                            <tbody>
                              @for (translation of row.translationList; track translation.value; let idx = $index) {
                                <tr>
                                  <td class="align-top pr-2 py-1">{{ getLanguageLabel(translation.lang) }}</td>
                                  <td class="py-1">
                                    @if (row.editing) {
                                      <textarea
                                        hlmTextarea
                                        [(ngModel)]="translation.value"
                                        class="w-full"
                                        name="translationValue{{ idx }}"
                                        rows="3"
                                      ></textarea>
                                    } @else {
                                      {{ translation.value }}
                                    }
                                  </td>
                                </tr>
                              }
                            </tbody>
                          </table>
                        </td>
                        <td hlmTd>
                          <div class="flex items-center justify-center gap-2">
                            @if (!row.editing) {
                              <button hlmBtn variant="ghost" size="icon" type="button" (click)="onRowEditInit(row)">
                                <ng-icon name="lucidePencil" />
                              </button>
                            } @else {
                              <button hlmBtn variant="ghost" size="icon" type="button" (click)="onRowEditSave(row)">
                                <ng-icon name="lucideCheck" />
                              </button>
                              <button hlmBtn variant="ghost" size="icon" type="button" (click)="onRowEditCancel(row, row.id)">
                                <ng-icon name="lucideX" />
                              </button>
                            }
                          </div>
                        </td>
                      </tr>
                    } @empty {
                      <tr>
                        <td [attr.colspan]="3" hlmTd>
                          {{ 'GLOBAL.TABLES.no-results' | translate }}
                        </td>
                      </tr>
                    }
                  </tbody>
                </table>
                @if (loading()) {
                  <div class="absolute inset-0 z-10 flex items-center justify-center bg-background/60">
                    <hlm-spinner class="text-4xl" />
                  </div>
                }
              </div>
              <hlm-numbered-pagination
                [currentPage]="currentPageNumber()"
                (currentPageChange)="handlePageNumberChange($event)"
                [itemsPerPage]="form.rows().value()"
                (itemsPerPageChange)="handleItemsPerPageChange($event)"
                [totalItems]="totalCount()"
                [pageSizes]="[10, 20, 50]"
              />
            </div>
          }
        </div>
      </div>
    </div>
  `,
  styleUrl: './i18n-management.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class I18nManagementComponent {
  private i18nResourceService = inject(I18nTranslationService);
  protected commonEntitiesService = inject(CommonEntitiesService);

  protected results = this.i18nResourceService.searchResults;
  protected totalCount = this.i18nResourceService.totalCount;
  protected criteriaCollapsed = this.i18nResourceService.criteriaCollapsed;
  protected tableLoading = this.i18nResourceService.tableLoading;
  protected loading = this.i18nResourceService.isSearchLoading;
  protected hasSearched = this.i18nResourceService.hasSearched;
  protected languages = this.i18nResourceService.languagesAsSelectItems;
  protected modules = this.i18nResourceService.modulesAsSelectItems;

  protected readonly searchType: SearchType = SearchTypeEnum.RESOURCES;
  protected resultsVisible: WritableSignal<boolean> = signal(false);
  protected animationTimer: any;

  form!: FieldTree<I18nResourceSearchFormModel, string | number>;


  constructor() {
    this.initForm();
    this.initResourceBundleData();
    effect(() => {
      clearTimeout(this.animationTimer);
      if (this.hasSearched()) {
        this.resultsVisible.set(true);
      } else {
        this.animationTimer = setTimeout(() => {
          this.resultsVisible.set(false);
        }, 300); // Must match the animation duration in CSS
      }
    });
  }

  protected search(): void {
    this.i18nResourceService.executeSearchResources(this.form);
  }

  protected resetForm(): void {
    this.form().reset();
    this.search();
  }

  protected handleSort(field: string): void {
    const isSameFieldAscending = this.form.sortField().value() === field && this.form.sortOrder().value() === SortDirectionEnum.ASC;
    this.form.sortField().value.set(field);
    this.form.sortOrder().value.set(isSameFieldAscending ? SortDirectionEnum.DESC : SortDirectionEnum.ASC);
    this.search();
  }

  protected sortIconName(field: string): string {
    if (this.form.sortField().value() !== field) {
      return 'lucideArrowUpDown';
    }
    return this.form.sortOrder().value() === SortDirectionEnum.ASC ? 'lucideArrowUp' : 'lucideArrowDown';
  }

  protected currentPageNumber(): number {
    const rows = this.form.rows().value();
    return rows > 0 ? Math.floor(this.form.first().value() / rows) + 1 : 1;
  }

  protected handlePageNumberChange(page: number): void {
    this.form.first().value.set((page - 1) * this.form.rows().value());
    this.search();
  }

  protected handleItemsPerPageChange(pageSize: number): void {
    this.form.first().value.set(0);
    this.form.rows().value.set(pageSize);
    this.search();
  }

  protected getLanguageLabel(langId: unknown): string {
    return this.languages().find(lang => lang.value.toString() === langId)?.label ?? '';
  }

  protected onRowEditInit(row: I18nResource){
    row.editing = true;
    row._translationList = row.translationList ? row.translationList.map(t => ({ ...t })) : [];
  }

  protected onRowEditSave(row: I18nResource){
    const updates: UpdateI18nResource[] = row.translationList.map(t => ({
      resourceId: row.id,
      textValue: t.value,
      languageId: Number(t.lang)
    }));
    this.i18nResourceService.executeUpdateResources(updates,row);
  }

  protected onRowEditCancel(row: I18nResource, rowId: number){
    this.results().find((item) => item.id === rowId)!.translationList = row._translationList ? [...row._translationList] : [];
    delete row._translationList;
    row.editing = false;
  }

  private initForm(): void{
    this.form = this.i18nResourceService.initSearchI18nResourceForm();
  }

  private initResourceBundleData(): void{
    this.i18nResourceService.executeGetLanguages();
    this.i18nResourceService.executeGetModules();
  }
}
