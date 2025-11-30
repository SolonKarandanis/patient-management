import {ChangeDetectionStrategy, Component, effect, inject, OnInit, signal, WritableSignal} from '@angular/core';
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
    SearchButtonsComponent
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


  protected resultsVisible: WritableSignal<boolean> = signal(false);
  private animationTimer: any;

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
  }

  private initForm(): void{
    this.form = this.i18nResourceService.initSearchI18nResourceForm();
  }

  private initResourceBundleData(): void{
    this.i18nResourceService.executeGetLanguages();
    this.i18nResourceService.executeGetModules();
  }
}
