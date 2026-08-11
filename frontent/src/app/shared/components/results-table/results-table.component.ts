import {ChangeDetectionStrategy, Component, computed, inject, input, output, signal} from '@angular/core';
import {
  injectTable,
  rowPaginationFeature,
  rowSelectionFeature,
  rowSortingFeature,
  tableFeatures,
  type ColumnDef,
  type PaginationState,
  type RowSelectionState,
  type SortingState,
  type Updater,
} from '@tanstack/angular-table';
import {BaseModel} from '@models/base.model';
import {ResultsTableStateEvent, SearchModes, SearchTableColumn} from '@models/search.model';
import {TranslatePipe} from '@ngx-translate/core';
import {LinkComponent} from '@components/link/link.component';
import {DatePipe, DecimalPipe, NgTemplateOutlet} from '@angular/common';
import {CommonEntitiesService} from '@core/services/common-entities.service';
import {FormsModule} from '@angular/forms';
import {FormControlWrapComponent} from '@components/form-control-wrap/form-control-wrap.component';
import {HlmTableImports} from '@components/ui/table';
import {HlmPaginationImports} from '@components/ui/pagination';
import {HlmCheckboxImports} from '@components/ui/checkbox';
import {HlmRadioGroupImports} from '@components/ui/radio-group';
import {HlmInputImports} from '@components/ui/input';
import {HlmDatePickerImports} from '@components/ui/date-picker';
import {HlmButtonImports} from '@components/ui/button';
import {HlmSpinnerImports} from '@components/ui/spinner';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {lucideArrowDown, lucideArrowUp, lucideArrowUpDown, lucideCheck, lucideDownload, lucidePlus, lucideSearch} from '@ng-icons/lucide';

const tableFeatureSet = tableFeatures({
  rowSortingFeature,
  rowPaginationFeature,
  rowSelectionFeature,
});

@Component({
  selector: 'app-results-table',
  imports: [
    TranslatePipe,
    LinkComponent,
    DatePipe,
    NgTemplateOutlet,
    DecimalPipe,
    FormsModule,
    FormControlWrapComponent,
    HlmTableImports,
    HlmPaginationImports,
    HlmCheckboxImports,
    HlmRadioGroupImports,
    HlmInputImports,
    HlmDatePickerImports,
    HlmButtonImports,
    HlmSpinnerImports,
    NgIcon,
  ],
  providers: [provideIcons({lucideArrowDown, lucideArrowUp, lucideArrowUpDown, lucideCheck, lucideDownload, lucidePlus, lucideSearch})],
  template: `
    <div class="flex justify-between items-center">
      @if (showTableFilter) {
        <div class="relative w-[30vw]">
          <div class="search">
            <input
              type="search"
              class="search__input"
              aria-label="search"
              placeholder="search in results....."
              (input)="handleFilterInput($event)">
            <button class="search__submit" type="button" aria-label="submit search">
              <ng-icon name="lucideSearch" id="icon" />
            </button>
          </div>
        </div>
      }
      @if (!showTablePaginator) {
        <hlm-numbered-pagination
          [currentPage]="currentPageNumber()"
          (currentPageChange)="handlePageNumberChange($event)"
          [itemsPerPage]="resultsPerPage()"
          (itemsPerPageChange)="handleItemsPerPageChange($event)"
          [totalItems]="totalRecords()"
          [pageSizes]="rowsPerPageOptions"
        />
      }
    </div>
    @if (showTableToolBar) {
      <button hlmBtn type="button" (click)="tableToolBarAction()">
        <ng-icon name="lucidePlus" />
        {{ 'GLOBAL.TABLES.ACTIONS.add' | translate }}
      </button>
    }

    <div hlmTableContainer class="relative">
      <table hlmTable>
        <thead hlmTHead>
          <tr hlmTr>
            @for (colTitle of colTitles(); track colTitle.field) {
              <th hlmTh [style]="colTitle.style">
                @if (!colTitle.isCheckbox && !colTitle.headerIsIcon) {
                  @if (colTitle.enableSorting) {
                    <button type="button" class="inline-flex items-center gap-1 font-medium" (click)="handleSortClick(colTitle.field)">
                      <span>{{ colTitle.title }}</span>
                      <ng-icon [name]="sortIconName(colTitle.field)" />
                    </button>
                  } @else {
                    <span>{{ colTitle.title }}</span>
                  }
                }
                @if (!colTitle.isCheckbox && colTitle.headerIsIcon) {
                  <span><span class="{{ colTitle.headerIcon }}"></span></span>
                }
                @if (colTitle.isCheckbox) {
                  <hlm-checkbox
                    [checked]="table.getIsAllRowsSelected()"
                    [indeterminate]="table.getIsSomeRowsSelected() && !table.getIsAllRowsSelected()"
                    [disabled]="isCheckboxColumnDisabled"
                    (checkedChange)="handleToggleAllRows($event)"
                  />
                }
              </th>
            }
          </tr>
        </thead>
        <tbody hlmTBody hlmRadioGroup [value]="selectedRadioRowId()" (valueChange)="handleRadioRowSelect($event)">
          @for (row of table.getRowModel().rows; track row.id) {
            <tr hlmTr>
              @for (col of colTitles(); track col.field) {
                <td hlmTd [style]="col.style">
                  @if (!col.isCheckbox && !col.isStaticCheckbox && !col.isCurrencyValue && !col.isRadioButton && !col.isInputText && !col.isInputNumber && !col.isInputDate && !col.isButton && !col.isButtonGroup && !col.isTableActions) {
                    <span>
                      @if (col.isLink) {
                        <app-link [config]="col" [tableItem]="asRecord(row.original)">
                          @if (col.onlyIcon) {
                            <span class="{{ col.icon }}"></span>
                          }
                          @if (!col.onlyIcon) {
                            <span>
                              @if (col.icon) {
                                <span class="{{ col.icon }} mr-2"></span>
                              }
                              {{ getFieldValue(row.original, col.field) }}
                            </span>
                          }
                        </app-link>
                      }
                      @if (!col.isStatus && !col.isLink && !col.isDate && !col.isImage && col.isTranslatable) {
                        <span>{{ getFieldValue(row.original, col.field) | translate }}</span>
                      }
                      @if (!col.isLink && !col.isDate && !col.isImage && !col.isTranslatable) {
                        <span>{{ getFieldValue(row.original, col.field) }}</span>
                      }
                      @if (!col.isLink && col.isDate && !col.isImage) {
                        <span>{{ getFieldValue(row.original, col.field) | date: 'dd/MM/yyyy' }}</span>
                      }
                      @if (!col.isLink && !col.isDate && !col.isImage && !col.isTranslatable && col.isStatus && statusClasses()) {
                        <span class="badge" [class]="getClass(getFieldValue(row.original, col.field))">
                          {{ getFieldValue(row.original, col.field) }}
                        </span>
                      }
                      @if (col.isImage) {
                        @let field = getFieldValue(row.original, col.field);
                        @if (field) {
                          <img [src]="field" width="50" alt="" />
                        }
                      }
                    </span>
                  }
                  @if (col.isCurrencyValue) {
                    <span>
                      {{ getFieldValue(row.original, col.field) | number: currencyDecimalsFormat }}
                    </span>
                  }
                  @if (col.isCheckbox) {
                    <hlm-checkbox
                      [checked]="row.getIsSelected()"
                      [disabled]="isCheckboxColumnDisabled || (col.dataFieldForCheckboxDisabled && getFieldValue(row.original, col.dataFieldForCheckboxDisabled))"
                      (checkedChange)="row.toggleSelected($event)"
                    />
                  }
                  @if (col.isRadioButton) {
                    <hlm-radio
                      [value]="row.id"
                      [disabled]="!!(col.dataFieldForRadioButtonDisabled && getFieldValue(row.original, col.dataFieldForRadioButtonDisabled))"
                    />
                  }
                  @if (col.isStaticCheckbox) {
                    <hlm-checkbox
                      [checked]="!!getFieldValue(row.original, col.field)"
                      [disabled]="true"
                    />
                  }
                  @if (col.isInputText && col.inputTextModelField) {
                    <app-form-control-wrap
                      [editMode]="!(col.dataFieldForInputDisabled && getFieldValue(row.original, col.dataFieldForInputDisabled))"
                      [displayValue]="getFieldValue(row.original, col.inputTextModelField)"
                    >
                      <input
                        type="text"
                        hlmInput
                        [disabled]="!!(col.dataFieldForInputDisabled && getFieldValue(row.original, col.dataFieldForInputDisabled))"
                        [ngModel]="getFieldValue(row.original, col.inputTextModelField)"
                        (ngModelChange)="setFieldValue(row.original, col.inputTextModelField, $event)"
                      />
                    </app-form-control-wrap>
                  }
                  @if (col.isInputNumber && col.inputNumberModelField) {
                    <app-form-control-wrap
                      [editMode]="!(col.dataFieldForInputDisabled && getFieldValue(row.original, col.dataFieldForInputDisabled))"
                      [displayValue]="getFieldValue(row.original, col.inputNumberModelField)"
                    >
                      <input
                        type="number"
                        hlmInput
                        [disabled]="!!(col.dataFieldForInputDisabled && getFieldValue(row.original, col.dataFieldForInputDisabled))"
                        [max]="col.inputNumberMaxModelField ? getFieldValue(row.original, col.inputNumberMaxModelField) : null"
                        [min]="col.inputNumberMinModelField ? getFieldValue(row.original, col.inputNumberMinModelField) : 0"
                        [ngModel]="getFieldValue(row.original, col.inputNumberModelField)"
                        (ngModelChange)="setFieldValue(row.original, col.inputNumberModelField, $event)"
                      />
                    </app-form-control-wrap>
                  }
                  @if (col.isInputDate && col.inputDateModelField) {
                    <app-form-control-wrap
                      [editMode]="!(col.dataFieldForInputDateDisabled && getFieldValue(row.original, col.dataFieldForInputDateDisabled))"
                      [displayValue]="getDateAsString(getFieldValue(row.original, col.inputDateModelField))"
                    >
                      <hlm-date-picker
                        [date]="getFieldValue(row.original, col.inputDateModelField)"
                        (dateChange)="setFieldValue(row.original, col.inputDateModelField, $event)"
                      >
                        <hlm-date-picker-trigger />
                      </hlm-date-picker>
                    </app-form-control-wrap>
                  }
                  @if (col.isButton && (col.fieldForButtonVisibility !== undefined ? !!getFieldValue(row.original, col.fieldForButtonVisibility) : true)) {
                    <button
                      hlmBtn
                      variant="outline"
                      size="icon"
                      type="button"
                      (click)="col.buttonAction(col.dataFieldForButtonAction ? getFieldValue(row.original, col.dataFieldForButtonAction) : null)"
                    >
                      <span class="{{ col.icon }}"></span>
                    </button>
                  }
                  @if (col.isButtonGroup) {
                    @for (groupButton of col.buttonGroup; track groupButton.icon) {
                      <button
                        hlmBtn
                        variant="outline"
                        size="icon"
                        type="button"
                        [disabled]="!!(groupButton.dataFieldForButtonDisabled && getFieldValue(row.original, groupButton.dataFieldForButtonDisabled))"
                        (click)="groupButton.action(groupButton.dataFieldForButtonAction ? getFieldValue(row.original, groupButton.dataFieldForButtonAction) : null)"
                      >
                        <span class="{{ groupButton.icon }}"></span>
                      </button>
                    }
                  }
                  @if (col.isTableActions) {
                    <span class="text-dark fw-bolder mb-1 fs-6">
                      @for (action of col.actions; track action.type) {
                        @switch (action.type) {
                          @case ('VIEW') {
                            <ng-container
                              [ngTemplateOutlet]="viewBlock"
                              [ngTemplateOutletContext]="{tableItem: row.original, action: action}">
                            </ng-container>
                          }
                          @case ('EDIT') {
                            <ng-container
                              [ngTemplateOutlet]="editBlock"
                              [ngTemplateOutletContext]="{tableItem: row.original, action: action}">
                            </ng-container>
                          }
                          @case ('DELETE') {
                            <ng-container
                              [ngTemplateOutlet]="deleteBlock"
                              [ngTemplateOutletContext]="{tableItem: row.original, uuid: row.original.publicId, action: action}">
                            </ng-container>
                          }
                          @default {}
                        }
                      }
                    </span>
                  }
                </td>
              }
            </tr>
          } @empty {
            <tr>
              <td [attr.colspan]="colTitles().length" hlmTd>
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

    @if (showTablePaginator) {
      <hlm-numbered-pagination
        [currentPage]="currentPageNumber()"
        (currentPageChange)="handlePageNumberChange($event)"
        [itemsPerPage]="resultsPerPage()"
        (itemsPerPageChange)="handleItemsPerPageChange($event)"
        [totalItems]="totalRecords()"
        [pageSizes]="rowsPerPageOptions"
      />
    }

    @if (mode() !== 'no-buttons') {
      <div class="flex gap-5 mt-4">
        <button
          hlmBtn
          type="button"
          variant="secondary"
          (click)="handleExportClicked()"
          [disabled]="totalRecords() >= maxResultsCsvExport || loading() || !tableItems() || tableItems().length === 0"
        >
          <ng-icon name="lucideDownload" />
          {{ (overrideDefaultExport() ? exportLabel() : exportButtonLabel) | translate }}
        </button>
        @if (selectionEnabled) {
          <button
            hlmBtn
            type="button"
            (click)="handleSelectItemsClicked()"
            [disabled]="selectedItems().length === 0"
          >
            <ng-icon name="lucideCheck" />
            {{ selectButtonLabelKey() | translate }}
          </button>
        }
      </div>
    }

    <ng-template let-tableItem="tableItem" let-action="action" #viewBlock>
      <app-link
        [config]="action"
        [tableItem]="tableItem">
        <span class="svg-icon svg-icon-3"></span>
      </app-link>
    </ng-template>
    <ng-template let-tableItem="tableItem" let-action="action" #editBlock>
      <button
        hlmBtn
        variant="outline"
        size="icon"
        data-tool-tip="Edit"
        type="button"
        (click)="action.callbackFn(tableItem)">
        <span class="svg-icon svg-icon-3"></span>
      </button>
    </ng-template>
    <ng-template let-tableItem="tableItem" let-uuid="uuid" let-action="action" #deleteBlock>
      @if (action.isButton) {
        <button
          hlmBtn
          variant="outline"
          size="icon"
          data-tool-tip="Delete"
          type="button"
          (click)="action.callbackFn(uuid)">
          <span class="svg-icon svg-icon-3"></span>
        </button>
      }
      @if (action.isLink) {
        <app-link
          [config]="action"
          [tableItem]="tableItem">
          <span class="svg-icon svg-icon-3"></span>
        </app-link>
      }
    </ng-template>
  `,
  styleUrl: './results-table.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ResultsTableComponent {
  private commonEntitiesService = inject(CommonEntitiesService);

  protected rowsPerPageOptions: number[] = [10, 20, 50, 100];

  public showTablePaginator = false;
  public showTableFilter = false;
  public showTableToolBar = false;
  public selectionEnabled = false;
  public isCheckboxColumnDisabled = false;
  public enablePaging = false;

  protected exportButtonLabel: string = 'GLOBAL.BUTTONS.export-to-csv';
  protected maxResultsCsvExport = 100;

  mode = input<SearchModes>("normal");
  tableItems = input.required<BaseModel[]>();
  colTitles = input.required<SearchTableColumn[]>();
  totalRecords = input.required<number>();
  first = input(0);
  resultsPerPage = input(10);
  loading = input(false);
  lazy = input(false);
  exportLabel = input(this.exportButtonLabel);
  statusClasses = input<Map<string, string>>();
  exportFunction = input<Function>();
  callbackFunctionToolBar = input<Function>();
  selectButtonLabelKey = input('GLOBAL.BUTTONS.select');
  overrideDefaultExport = input(false);
  selectionMode = input<TableSelectionMode>('multiple');

  tableStateChanged = output<ResultsTableStateEvent>();
  itemsSelected = output<BaseModel[]>();
  rowSelectionChanged = output<BaseModel[]>();
  rowSingleSelectionChanged = output<BaseModel[]>();

  protected readonly filterTerm = signal('');
  protected readonly sortingState = signal<SortingState>([]);
  protected readonly rowSelectionState = signal<RowSelectionState>({});

  protected readonly filteredTableItems = computed<BaseModel[]>(() => {
    const term = this.filterTerm().trim().toLowerCase();
    const items = this.tableItems();
    if (!term) {
      return items;
    }
    return items.filter(item =>
      Object.values(item as unknown as Record<string, unknown>).some(
        value => value != null && String(value).toLowerCase().includes(term)
      )
    );
  });

  protected readonly columnDefs = computed<ColumnDef<typeof tableFeatureSet, BaseModel>[]>(() =>
    this.colTitles().map(col => ({
      id: col.field,
      header: col.title,
      enableSorting: !!col.enableSorting,
    }))
  );

  protected readonly paginationState = computed<PaginationState>(() => ({
    pageIndex: this.resultsPerPage() > 0 ? Math.floor(this.first() / this.resultsPerPage()) : 0,
    pageSize: this.resultsPerPage(),
  }));

  protected readonly currentPageNumber = computed(() => this.paginationState().pageIndex + 1);

  protected readonly selectedItems = computed<BaseModel[]>(() => {
    const selection = this.rowSelectionState();
    return this.tableItems().filter(item => selection[item.publicId]);
  });

  protected readonly selectedRadioRowId = computed(() => Object.keys(this.rowSelectionState())[0]);

  protected readonly table = injectTable(() => ({
    data: this.filteredTableItems(),
    columns: this.columnDefs(),
    features: tableFeatureSet,
    manualPagination: true,
    manualSorting: true,
    enableMultiRowSelection: this.selectionMode() !== 'single',
    rowCount: this.totalRecords(),
    getRowId: (row: BaseModel) => row.publicId,
    state: {
      pagination: this.paginationState(),
      sorting: this.sortingState(),
      rowSelection: this.rowSelectionState(),
    },
    onPaginationChange: updater => this.handlePaginationChange(updater),
    onSortingChange: updater => this.handleSortingChange(updater),
    onRowSelectionChange: updater => this.handleRowSelectionStateChange(updater),
  }));

  protected getFieldValue(item: BaseModel, field?: string): any {
    return field ? (item as unknown as Record<string, unknown>)[field] : '';
  }

  protected asRecord(item: BaseModel): Record<string, unknown> {
    return item as unknown as Record<string, unknown>;
  }

  protected setFieldValue(item: BaseModel, field: string | undefined, value: unknown): void {
    if (field) {
      (item as unknown as Record<string, unknown>)[field] = value;
    }
  }

  protected handleFilterInput(event: Event): void {
    this.filterTerm.set((event.target as HTMLInputElement).value);
  }

  protected handleSortClick(field: string): void {
    this.table.getColumn(field)?.toggleSorting();
  }

  protected sortIconName(field: string): string {
    const sorted = this.table.getColumn(field)?.getIsSorted();
    if (sorted === 'asc') {
      return 'lucideArrowUp';
    }
    if (sorted === 'desc') {
      return 'lucideArrowDown';
    }
    return 'lucideArrowUpDown';
  }

  protected handlePaginationChange(updater: Updater<PaginationState>): void {
    const next = typeof updater === 'function' ? updater(this.paginationState()) : updater;
    this.emitStateChange({first: next.pageIndex * next.pageSize, rows: next.pageSize});
  }

  protected handleSortingChange(updater: Updater<SortingState>): void {
    const next = typeof updater === 'function' ? updater(this.sortingState()) : updater;
    this.sortingState.set(next);
    this.emitStateChange({});
  }

  protected handleRowSelectionStateChange(updater: Updater<RowSelectionState>): void {
    const next = typeof updater === 'function' ? updater(this.rowSelectionState()) : updater;
    this.rowSelectionState.set(next);
    const selected = this.tableItems().filter(item => next[item.publicId]);
    if (this.selectionMode() === 'single') {
      this.rowSingleSelectionChanged.emit(selected);
    } else {
      this.rowSelectionChanged.emit(selected);
    }
  }

  protected handleToggleAllRows(checked: boolean): void {
    this.table.toggleAllRowsSelected(checked);
  }

  protected handleRadioRowSelect(rowId: string): void {
    this.table.setRowSelection({[rowId]: true});
  }

  protected handlePageNumberChange(page: number): void {
    this.table.setPagination({pageIndex: page - 1, pageSize: this.resultsPerPage()});
  }

  protected handleItemsPerPageChange(pageSize: number): void {
    this.table.setPagination({pageIndex: 0, pageSize});
  }

  protected handleSelectItemsClicked(): void {
    this.itemsSelected.emit(this.selectedItems());
  }

  protected getClass(field: string): string | undefined {
    return this.statusClasses()?.get(field);
  }

  protected handleExportClicked(): void {
    if (this.overrideDefaultExport()) {
      this.exportParentFunction();
      return;
    }
    this.exportCurrentPageAsCsv();
  }

  protected exportParentFunction(): void {
    const suppliedFunction = this.exportFunction();
    if (suppliedFunction) {
      suppliedFunction();
    }
  }

  protected getDateAsString(date: Date): string {
    // return this.getFormattedDate(this.utilService.convertDateObjectsToCcmFormat(date));
    return ''
  }

  protected tableToolBarAction(uuid?: string) {
    // const fun = this.callbackFunctionToolBar(uuid);
    // console.log(fun);
  }

  get currencyDecimalsFormat(): string {
    return this.commonEntitiesService.getBigDecimalScale();
  }

  private emitStateChange(partial: Partial<ResultsTableStateEvent>): void {
    const [sort] = this.sortingState();
    this.tableStateChanged.emit({
      first: this.first(),
      rows: this.resultsPerPage(),
      sortField: sort?.id,
      sortOrder: sort ? (sort.desc ? -1 : 1) : undefined,
      ...partial,
    });
  }

  private exportCurrentPageAsCsv(): void {
    const columns = this.colTitles().filter(col => !!col.field);
    const header = columns.map(col => this.csvEscape(col.title)).join(',');
    const rows = this.tableItems().map(item =>
      columns.map(col => this.csvEscape(String(this.getFieldValue(item, col.field) ?? ''))).join(',')
    );
    const csvContent = [header, ...rows].join('\n');
    const blob = new Blob([csvContent], {type: 'text/csv;charset=utf-8;'});
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = 'export.csv';
    anchor.click();
    URL.revokeObjectURL(url);
  }

  private csvEscape(value: string): string {
    return /[",\n]/.test(value) ? `"${value.replace(/"/g, '""')}"` : value;
  }

}

type Function = (args?: any) => void;

type TableSelectionMode = 'single' | 'multiple' | null | undefined;
