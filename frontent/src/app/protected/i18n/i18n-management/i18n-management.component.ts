import {ChangeDetectionStrategy, Component, effect, inject, OnInit} from '@angular/core';
import {I18nTranslationService} from '../data/services/i18n-translation.service';
import {CommonEntitiesService} from '@core/services/common-entities.service';
import {BaseComponent} from '@shared/abstract/BaseComponent';
import {PageHeaderComponent} from '@components/page-header/page-header.component';
import {TranslatePipe} from '@ngx-translate/core';
import {FieldsetComponent} from '@components/fieldset/fieldset.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {Select} from 'primeng/select';
import {SearchButtonsComponent} from '@components/search-buttons/search-buttons.component';
import {SearchType, SearchTypeEnum} from '@models/search.model';
import {TableLazyLoadEvent, TableModule} from 'primeng/table';
import {I18nResource, UpdateI18nResource} from '@models/i18n-resource.model';
import {NgClass} from '@angular/common';
import {Textarea} from 'primeng/textarea';
import {ButtonDirective} from 'primeng/button';
import {Ripple} from 'primeng/ripple';

@Component({
  selector: 'app-i18n-management',
  imports: [
    PageHeaderComponent,
    TranslatePipe,
    FieldsetComponent,
    FormsModule,
    ReactiveFormsModule,
    FloatLabel,
    InputText,
    Select,
    SearchButtonsComponent,
    NgClass,
    TableModule,
    Textarea,
    ButtonDirective,
    Ripple
  ],
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
            <form [formGroup]="form">
              <div class="grid gap-6 mt-6 md:grid-cols-3">
                <div class="mb-6">
                  <p-float-label variant="on" class="w-full mb-3">
                    <p-select
                      formControlName="module"
                      [options]="modules()"
                      [checkmark]="true"
                      [showClear]="true"
                      class="border-0 !bg-white text-sm shadow w-full"/>
                    <label class="app-required-label" for="name">
                      {{ 'ADMINISTRATION.I18N-MANAGEMENT.search-module' | translate }}
                    </label>
                  </p-float-label>
                </div>
                <div class="mb-6">
                  <p-float-label variant="on" class="w-full mb-3">
                    <p-select
                      formControlName="language"
                      [options]="languages()"
                      [checkmark]="true"
                      [showClear]="true"
                      class="border-0 !bg-white text-sm shadow w-full"/>
                    <label class="app-required-label" for="name">
                      {{ 'ADMINISTRATION.I18N-MANAGEMENT.search-language' | translate }}
                    </label>
                  </p-float-label>
                </div>
                <div class="mb-6">
                  <p-float-label variant="on" class="w-full mb-3">
                    <input
                      id="text"
                      pInputText
                      type="text"
                      class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                      formControlName="term"
                      autocomplete="term"/>
                    <label for="term">{{ 'ADMINISTRATION.I18N-MANAGEMENT.search-term' | translate }}</label>
                  </p-float-label>
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
            <div class="mt-6" [ngClass]="{'fade-in': hasSearched(), 'fade-out': !hasSearched()}">
              <p-table
                [value]="results()"
                dataKey="id"
                editMode="row"
                [paginator]="true"
                [totalRecords]="totalCount()"
                [first]="form.controls['first'].value"
                [rows]="form.controls['rows'].value"
                [rowsPerPageOptions]="[10, 20, 50]"
                [lazy]="true"
                [loading]="loading()"
                (onLazyLoad)="handleTableLazyLoad($event)"
              >
                <ng-template pTemplate="header">
                  <tr class="">
                    <th scope="col" class="flex-initial w-[16%] bg-blueGray-100">{{ 'ADMINISTRATION.I18N-MANAGEMENT.TABLE.resource-key' | translate }}</th>
                    <th scope="col" class="flex-initial w-[58%]  bg-blueGray-100">{{ 'ADMINISTRATION.I18N-MANAGEMENT.TABLE.resource-value' | translate }}</th>
                    <th scope="col" class="flex-initial w-[8%]  bg-blueGray-100">{{ 'ADMINISTRATION.I18N-MANAGEMENT.TABLE.action' | translate }}</th>
                  </tr>
                </ng-template>
                <ng-template pTemplate="emptymessage" let-columns>
                  <tr>
                    <td [attr.colspan]="3">
                      {{ 'GLOBAL.TABLES.no-results' | translate }}
                    </td>
                  </tr>
                </ng-template>
                <ng-template pTemplate="body" let-row let-rowIndex="rowIndex">
                  <tr class="">
                    <td class="">{{row.key}}</td>
                    <td class="">
                      @for(translation of row.translationList; track translation.value;  let idx = $index){
                        <tr class="">
                          <td class="">{{ (getLanguageLabel(translation.lang)) }}</td>
                          <td class="">
                            @if(row.editing){
                              <textarea
                                [(ngModel)]="translation.value"
                                class="w-full"
                                name="translationValue{{ idx}}"
                                [rows]="3"
                                pInputTextarea
                              ></textarea>
                            } @else {
                              {{ translation.value }}
                            }
                          </td>
                        </tr>
                      }
                    </td>
                    <td class="">
                      <div class="flex items-center justify-center gap-2">
                        @if(!row.editing){
                          <button
                            pButton
                            pRipple
                            type="button"
                            icon="pi pi-pencil"
                            (click)="onRowEditInit(row)"
                            text
                            rounded
                            severity="secondary"
                          ></button>
                        } @else {
                          <button
                            pButton
                            pRipple
                            type="button"
                            icon="pi pi-save"
                            (click)="onRowEditSave(row)"
                            text
                            rounded
                            severity="secondary"
                          ></button>
                          <button
                            pButton
                            pRipple
                            type="button"
                            icon="pi pi-times"
                            (click)="onRowEditCancel(row, row?.id)"
                            text
                            rounded
                            severity="secondary"
                          ></button>
                        }
                      </div>
                    </td>
                  </tr>
                </ng-template>
              </p-table>
            </div>
          }
        </div>
      </div>
    </div>
  `,
  styleUrl: './i18n-management.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class I18nManagementComponent extends BaseComponent implements OnInit {
  private i18nResourceService = inject(I18nTranslationService);
  protected commonEntitiesService = inject(CommonEntitiesService);

  protected results = this.i18nResourceService.searchResults;
  protected totalCount = this.i18nResourceService.totalCount;
  protected criteriaCollapsed = this.i18nResourceService.criteriaCollapsed;
  protected tableLoading = this.i18nResourceService.tableLoading;
  protected loading = this.i18nResourceService.isLoading;
  protected hasSearched = this.i18nResourceService.hasSearched;
  protected languages = this.i18nResourceService.languagesAsSelectItems;
  protected modules = this.i18nResourceService.modulesAsSelectItems;

  protected readonly searchType: SearchType = SearchTypeEnum.RESOURCES;


  constructor() {
    super();
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

  ngOnInit(): void {
    this.initForm();
    this.initResourceBundleData();
  }

  protected search(): void {
    this.i18nResourceService.executeSearchResources(this.form);
  }

  protected resetForm(): void {
    this.form.reset();
    this.search();
  }

  protected handleTableLazyLoad(event: TableLazyLoadEvent): void {
    const {first, rows, sortField, sortOrder} = event;
    this.form.patchValue({first, rows, sortField, sortOrder});
    this.search();
  }

  protected getLangValues(values: Record<string, string>) {
    return Object.entries(values).map(([lang, value]) => ({ lang, value }));
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
