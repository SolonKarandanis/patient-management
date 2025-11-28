import {ChangeDetectionStrategy, Component, inject, input, output} from '@angular/core';
import {TableLazyLoadEvent, TableModule} from 'primeng/table';
import {BaseModel} from '@models/base.model';
import {SearchModes, SearchTableColumn} from '@models/search.model';
import {UtilService} from '@core/services/util.service';
import {PaginatorModule} from 'primeng/paginator';
import {TranslatePipe} from '@ngx-translate/core';
import {ButtonModule} from 'primeng/button';
import {LinkComponent} from '@components/link/link.component';
import {DatePipe, DecimalPipe, NgClass, NgTemplateOutlet} from '@angular/common';
import {ImageModule} from 'primeng/image';
import {CommonEntitiesService} from '@core/services/common-entities.service';
import {Checkbox, CheckboxModule} from 'primeng/checkbox';
import {FormsModule} from '@angular/forms';
import {FormControlWrapComponent} from '@components/form-control-wrap/form-control-wrap.component';
import {InputText} from 'primeng/inputtext';
import {InputNumberModule} from 'primeng/inputnumber';
import {DatePickerModule} from 'primeng/datepicker';

@Component({
  selector: 'app-results-table',
  imports: [
    TableModule,
    PaginatorModule,
    TranslatePipe,
    ButtonModule,
    LinkComponent,
    NgClass,
    DatePipe,
    NgTemplateOutlet,
    ImageModule,
    DecimalPipe,
    FormsModule,
    FormControlWrapComponent,
    InputText,
    DatePickerModule,
    InputNumberModule,
    Checkbox,
  ],
  template: `
    <p-table
      #td
      [value]="tableItems()"
      [totalRecords]="totalRecords()"
      [rows]="resultsPerPage()"
      [first]="first()"
      [columns]="colTitles()"
      [lazy]="lazy()"
      [lazyLoadOnInit]="false"
      [(selection)]="selectedItems"
      [loading]="loading()"
      [rowTrackBy]="trackById"
      [rowHover]="true"
      [paginator]="showTablePaginator"
      [rowsPerPageOptions]="rowsPerPageOptions"
      [showCurrentPageReport]="showTablePaginator"
      (onLazyLoad)="handleLazyLoad($event)"
      (onRowSelect)="handleRowSelectionChange()"
      (onRowUnselect)="handleRowSelectionChange()"
      [selectAll]="selectAll.checked"
      (selectAllChange)="handleSelectAllChange($event)"
      currentPageReportTemplate="Showing {first} to {last} of {totalRecords} entries">
      <ng-template pTemplate="caption" >
        <div class="flex justify-between">
          @if(showTableFilter){
            <div class="relative w-[30vw]">
              <div class="search">
                <input
                  #searchInput type="search"
                  class="search__input"
                  aria-label="search"
                  placeholder="search in results....."
                  (input)="applyFilterGlobal($event, 'contains',td)">
                <button class="search__submit" aria-label="submit search">
                  <i id="icon" class="pi pi-search"></i>
                </button>
              </div>
            </div>
          }
          @if(!showTablePaginator){
            <p-paginator
              [rows]="resultsPerPage()"
              [first]="first()"
              [totalRecords]="totalRecords()"
              [rowsPerPageOptions]="rowsPerPageOptions"
              [showCurrentPageReport]="true"
              (onPageChange)="handleLazyLoad($event)">
            </p-paginator>
            {{ 'GLOBAL.TABLES.result-summary'  | translate: {totalRecords} }}
          }
        </div>
        @if(showTableToolBar){
          <button
            pButton
            type="button"
            (click)="tableToolBarAction()">
          {{ 'GLOBAL.TABLES.ACTIONS.add' | translate }}
          </button>
        }
      </ng-template>
      <ng-template
        pTemplate="header"
        let-columns>
        <tr >
          @for(colTitle of columns; track colTitle.field){
            <th [pSortableColumn]="colTitle.enableSorting ? colTitle.field : null"
                [pSortableColumnDisabled]="!colTitle.enableSorting"
                [style]="colTitle.style"
                class="bg-blueGray-100">
              @if (!colTitle.isCheckbox && !colTitle.headerIsIcon){
                <span >{{ colTitle.title }}</span>
              }
              @if(!colTitle.isCheckbox && colTitle.headerIsIcon){
                <span><span class="{{ colTitle.headerIcon }}"></span></span>
              }
              @if(colTitle.isCheckbox){
                <span><p-tableHeaderCheckbox [disabled]="isCheckboxColumnDisabled"></p-tableHeaderCheckbox></span>
              }
              @if (colTitle.enableSorting){
                <p-sortIcon field="{{ colTitle.field }}"></p-sortIcon>
              }
            </th>
          }
        </tr>
      </ng-template>
      <ng-template
        pTemplate="emptymessage"
        let-columns>
        <tr>
          <td [attr.colspan]="columns.length">
            {{ 'GLOBAL.TABLES.no-results' | translate }}
          </td>
        </tr>
      </ng-template>
      <ng-template
        pTemplate="body"
        let-tableItem>
        <tr>
          @for(col of colTitles(); track col.field;){
            <td [style]="col.style">
              @if(!col.isCheckbox && !col.isStaticCheckbox && !col.isCurrencyValue){
                <span>
                  @if(col.isLink){
                    <app-link [config]="col" [tableItem]="tableItem">
                          @if(col.onlyIcon){
                            <span class="{{ col.icon }}"></span>
                          }
                      @if(!col.onlyIcon){
                        <span>
                          @if(col.icon){
                            <span class="{{ col.icon }} mr-2"></span>
                          }
                          {{ col.field ? tableItem[col.field] : '' }}
                        </span>
                      }
                      </app-link>
                  }
                  @if(!col.isStatus &&!col.isLink && !col.isDate && !col.isImage  && col.isTranslatable){
                    <span>{{ col.field ? (tableItem[col.field] | translate) : '' }}</span>
                  }
                  @if(!col.isLink && !col.isDate && !col.isImage  && !col.isTranslatable ){
                    <span>{{ col.field ? tableItem[col.field] : '' }}</span>
                  }
                  @if(!col.isLink && col.isDate && !col.isImage){
                    <span>{{ (col.field ? tableItem[col.field] : '') | date : 'dd/MM/yyyy' }}</span>
                  }
                  @if(!col.isLink && !col.isDate && !col.isImage  && !col.isTranslatable && col.isStatus && statusClasses()){
                    <span class="badge" [ngClass]="getClass(col.field ? tableItem[col.field] : '')">
                        {{col.field ? tableItem[col.field] : '' }}
                    </span>
                  }
                  @if(col.isImage){
                    @let field= col.field ? tableItem[col.field] : '';
                    @if(field){
                      <p-image
                        src="{{ field }}"
                        width="50"
                        [preview]="true"
                      ></p-image>
                      <span
                        class="pi pi-image text-3xl"
                      ></span>
                    }
                  }
                </span>
              }
              @if(col.isCurrencyValue){
                <span>
                    {{ col.field ? (tableItem[col.field] | number: currencyDecimalsFormat) : '' }}
                </span>
              }
              @if(col.isCheckbox){
                <span>
                    <p-tableCheckbox
                      [value]="tableItem"
                      [disabled]="isCheckboxColumnDisabled || (col.dataFieldForCheckboxDisabled && tableItem[col.dataFieldForCheckboxDisabled])">
                    </p-tableCheckbox>
                </span>
              }
              @if(col.isRadioButton){
                <p-tableRadioButton
                  [value]="tableItem"
                  [disabled]="col.dataFieldForRadioButtonDisabled && tableItem[col.dataFieldForRadioButtonDisabled]"
                ></p-tableRadioButton>
              }
              @if(col.isStaticCheckbox){
                <p-checkbox
                  [binary]="true"
                  [ngModel]="col.field && tableItem[col.field]"
                  [readonly]="true"
                ></p-checkbox>
              }
              @if(col.isInputText && col.inputTextModelField){
                <app-form-control-wrap
                  [editMode]="!(col.dataFieldForInputDisabled && tableItem[col.dataFieldForInputDisabled])"
                  [displayValue]="tableItem[col.inputTextModelField]"
                >
                  <input
                    type="text"
                    pInputText
                    [disabled]="col.dataFieldForInputDisabled && tableItem[col.dataFieldForInputDisabled]"
                    [(ngModel)]="tableItem[col.inputTextModelField]"
                  />
                </app-form-control-wrap>
              }
              @if(col.isInputNumber && col.inputNumberModelField){
                <app-form-control-wrap
                  [editMode]="!(col.dataFieldForInputDisabled && tableItem[col.dataFieldForInputDisabled])"
                  [displayValue]="tableItem[col.inputNumberModelField]"
                >
                  <p-inputNumber
                    [disabled]="col.dataFieldForInputDisabled && tableItem[col.dataFieldForInputDisabled]"
                    [max]="col.inputNumberMaxModelField ? tableItem[col.inputNumberMaxModelField] : null"
                    [min]="col.inputNumberMinModelField ? tableItem[col.inputNumberMinModelField] : 0"
                    [(ngModel)]="tableItem[col.inputNumberModelField]"
                  >
                  </p-inputNumber>
                </app-form-control-wrap>
              }
              @if(col.isInputDate && col.inputDateModelField){
                <app-form-control-wrap
                  [editMode]="!(col.dataFieldForInputDateDisabled && tableItem[col.dataFieldForInputDateDisabled])"
                  [displayValue]="getDateAsString(tableItem[col.inputDateModelField])"
                >
                  <p-datepicker
                    [(ngModel)]="tableItem[col.inputDateModelField]"
                    dateFormat="dd/mm/yy"
                    [showTime]="false"
                    [readonlyInput]="true"
                    appendTo="body"
                  ></p-datepicker>
                </app-form-control-wrap>
              }
              @if(col.isButton && (col.fieldForButtonVisibility !== undefined ? !!tableItem[col.fieldForButtonVisibility] : true)){
                <button
                  pButton
                  type="button"
                  icon="{{ col.icon }}"
                  class="p-button-rounded p-button-outlined"
                  (click)="col.buttonAction(col.dataFieldForButtonAction ? tableItem[col.dataFieldForButtonAction] : null)"
                ></button>
              }
              @if(col.isButtonGroup){
                @for(groupButton of  col.buttonGroup; track groupButton.icon){
                  <button
                    pButton
                    type="button"
                    icon="{{ col.icon }}"
                    class="p-button-rounded p-button-outlined"
                    [disabled]="groupButton.dataFieldForButtonDisabled && tableItem[groupButton.dataFieldForButtonDisabled]"
                    (click)="groupButton.action(groupButton.dataFieldForButtonAction ? tableItem[groupButton.dataFieldForButtonAction] : null)"
                  ></button>
                }
              }
              @if(col.isTableActions){
                <span class="text-dark fw-bolder mb-1 fs-6">
                  @for(action of col.actions; track action.type;){
                    @switch(action.type){
                      @case ('VIEW'){
                        <ng-container
                          [ngTemplateOutlet]="viewBlock"
                          [ngTemplateOutletContext]="{tableItem:tableItem, action:action }">
                        </ng-container>
                      }
                      @case ('EDIT'){
                        <ng-container
                          [ngTemplateOutlet]="editBlock"
                          [ngTemplateOutletContext]="{ tableItem:tableItem,action:action }">
                        </ng-container>
                      }
                      @case ('DELETE'){
                        <ng-container
                          [ngTemplateOutlet]="deleteBlock"
                          [ngTemplateOutletContext]="{uuid:tableItem['uuid'] ,action:action }">
                        </ng-container>
                      }
                      @default{

                      }
                    }
                  }
                </span>
              }
            </td>
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
              pButton
              data-tool-tip="Edit"
              type="button"
              (click)="action.callbackFn(tableItem)">
              <span class="svg-icon svg-icon-3"></span>
            </button>
          </ng-template>
          <ng-template let-uuid="uuid" let-action="action" #deleteBlock>
            @if(action.isButton){
              <button
                pButton
                data-tool-tip="Delete"
                type="button"
                (click)="action.callbackFn(uuid)">
                <span class="svg-icon svg-icon-3"></span>
              </button>
            }
            @if(action.isLink){
              <app-link
                [config]="action"
                [tableItem]="tableItem" >
                        <span class="svg-icon svg-icon-3"></span>
              </app-link>
            }
          </ng-template>
        </tr>
      </ng-template>
      <ng-template
        pTemplate="summary">
        @if(mode() !== 'no-buttons'){
          <div class="flex gap-5 mt-4">
            <button
              pButton
              type="button"
              pButtonIcon="pi pi-file-export"
              severity="info"
              (click)="overrideDefaultExport() ? exportParentFunction() : td.exportCSV()"
              [disabled]="totalRecords() >= maxResultsCsvExport || loading() || !tableItems() || tableItems().length === 0"
            >
              {{ (overrideDefaultExport() ? exportLabel() : exportButtonLabel) | translate }}
            </button>
            @if(selectionEnabled){
              <button
                pButton
                type="button"
                pButtonIcon="pi pi-check"
                (click)="handleSelectItemsClicked()"
                [disabled]="!selectedItems || selectedItems.length === 0"
              >
                {{ selectButtonLabelKey() | translate }}
              </button>
            }
          </div>
        }
      </ng-template>
    </p-table>
  `,
  styleUrl: './results-table.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ResultsTableComponent {
  private utilService= inject(UtilService);
  private commonEntitiesService= inject(CommonEntitiesService);

  protected selectedItems!:BaseModel[];
  protected arrayObj = Array;
  protected rowsPerPageOptions:number[]=[10,20,50,100];

  public showTablePaginator = false;
  public showTableFilter = false;
  public showTableToolBar=false;
  public selectionEnabled=false;
  public isCheckboxColumnDisabled = false;
  public enablePaging = false;

  protected exportButtonLabel: string = 'GLOBAL.BUTTONS.export-to-csv';
  protected maxResultsCsvExport = 100;
  protected selectAll: any = this.getInitialSelectAll();

  mode = input<SearchModes>("normal");
  tableItems = input.required<BaseModel[]>();
  colTitles = input.required<SearchTableColumn[]>();
  totalRecords = input.required<number>();
  first = input(0);
  resultsPerPage = input(10);
  loading = input(false);
  lazy = input(false);
  exportLabel = input(this.exportButtonLabel);
  statusClasses = input<Map<string,string>>();
  exportFunction = input<Function>();
  callbackFunctionToolBar = input<Function>();
  selectButtonLabelKey = input('GLOBAL.BUTTONS.select');
  overrideDefaultExport = input(false);
  selectionMode= input<PrimeNGTableSelectionMode>('multiple');

  tableStateChanged = output<TableLazyLoadEvent>();
  itemsSelected = output<BaseModel[]>();
  rowSelectionChanged = output<BaseModel[]>();
  rowSingleSelectionChanged = output<BaseModel[]>();

  protected trackById( li: BaseModel): string| number | undefined {
    return li ? li.publicId : undefined;
  }

  protected applyFilterGlobal($event:Event, stringVal:string,td:any) {
    td.filterGlobal(($event.target as HTMLInputElement).value, 'contains');
  }

  protected handleLazyLoad(event: TableLazyLoadEvent): void {
    this.tableStateChanged.emit(event);
  }

  protected handleSelectItemsClicked(): void {
    this.itemsSelected.emit(this.selectedItems);
  }

  protected handleSelectAllChange(event: any): void {
    this.selectAll = event;
    this.selectedItems = event.checked ? this.tableItems() : [];

    this.handleRowSelectionChange();
  }

  protected handleRowSelectionChange(): void {
    if (this.selectionMode() === 'single') {
      this.rowSingleSelectionChanged.emit(this.selectedItems);
      return;
    }

    if (this.selectAll.checked === true && this.selectedItems.length < this.tableItems.length) {
      this.selectAll = this.getInitialSelectAll();
    }

    if (this.selectAll.checked === false && this.selectedItems.length === this.tableItems.length) {
      this.selectAll = this.getInitialSelectAll(true);
    }

    this.rowSelectionChanged.emit(this.selectedItems);
  }

  protected getClass(field:string):string|undefined{
    return this.statusClasses()?.get(field);
  }

  protected exportParentFunction(): void {
    const suppliedFunction = this.exportFunction();
    if(suppliedFunction){
      suppliedFunction();
    }
  }

  protected getFormattedDate(dateStr: string | null): string {
    let retVal: string | null = '';
    if (dateStr !== null) {
      retVal = this.utilService.convertDateStringToCalendarFormat(dateStr);
    }
    return retVal !== null ? retVal : '';
  }

  protected getDateAsString(date: Date): string {
    // return this.getFormattedDate(this.utilService.convertDateObjectsToCcmFormat(date));
    return ''
  }

  protected tableToolBarAction(uuid?:string){
    // const fun = this.callbackFunctionToolBar(uuid);
    // console.log(fun);
  }

  private getInitialSelectAll(checked?: boolean): any {
    return { originalEvent: null, checked: !!checked };
  }

  get currencyDecimalsFormat(): string {
    return this.commonEntitiesService.getBigDecimalScale();
  }

}

type Function = (args?: any) => void;

type PrimeNGTableSelectionMode = 'single' | 'multiple' | null | undefined;
