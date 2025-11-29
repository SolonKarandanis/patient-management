import {ChangeDetectionStrategy, Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {I18nTranslationService} from '../data/services/i18n-translation.service';
import {CommonEntitiesService} from '@core/services/common-entities.service';
import {BaseComponent} from '@shared/abstract/BaseComponent';

@Component({
  selector: 'app-i18n-management',
  imports: [],
  template: `
    <p>
      i18n-management works!
    </p>
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

  protected resultsVisible: WritableSignal<boolean> = signal(false);
  private animationTimer: any;

  ngOnInit(): void {
    this.initForm();
    this.initResourceBundleData();
  }

  private initForm(): void{
    this.form = this.i18nResourceService.initSearchI18nResourceForm();
  }

  private initResourceBundleData(): void{
    this.i18nResourceService.executeGetLanguages();
    this.i18nResourceService.executeGetModules();
  }
}
