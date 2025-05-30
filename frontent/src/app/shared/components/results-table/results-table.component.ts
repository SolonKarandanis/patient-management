import {ChangeDetectionStrategy, Component, inject, input, output} from '@angular/core';
import {TableLazyLoadEvent, TableModule} from 'primeng/table';
import {BaseModel} from '@models/base.model';
import {SearchTableColumn} from '@models/search.model';
import {UtilService} from '@core/services/util.service';
import {Paginator} from 'primeng/paginator';
import {TranslatePipe} from '@ngx-translate/core';
import {ButtonDirective, ButtonIcon} from 'primeng/button';
import {Ripple} from 'primeng/ripple';
import {LinkComponent} from '@components/link/link.component';
import {DatePipe, NgClass, NgTemplateOutlet} from '@angular/common';
import {Tooltip} from 'primeng/tooltip';

@Component({
  selector: 'app-results-table',
  imports: [
    TableModule,
    Paginator,
    TranslatePipe,
    ButtonDirective,
    Ripple,
    LinkComponent,
    NgClass,
    DatePipe,
    NgTemplateOutlet,
    Tooltip,
    ButtonIcon
  ],
  template: `
    <p-table
      #td
      [value]="tableItems()"
      [totalRecords]="totalRecords()"
      [rows]="resultsPerPage()"
      [first]="first()"
      [columns]="colTitles()"
      (onLazyLoad)="handleLazyLoad($event)"
      [lazy]="lazy()"
      [lazyLoadOnInit]="false"
      [(selection)]="selectedItems"
      [loading]="loading()"
      [rowTrackBy]="trackById"
      [rowHover]="true"
      [paginator]="showTablePaginator"
      [rowsPerPageOptions]="rowsPerPageOptions"
      [showCurrentPageReport]="showTablePaginator"
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
            {{ 'GLOBAL.TABLES.RESULT_SUMMARY'  | translate: {totalRecords} }}
          }
        </div>
        @if(showTableToolBar){
          <button
            pButton
            pRipple
            type="button"
            (click)="tableToolBarAction()">
          {{ 'GLOBAL.TABLES.ACTIONS.ADD' | translate }}
          </button>
        }
      </ng-template>
      <ng-template
        pTemplate="header"
        let-columns>
        <tr>
          @for(colTitle of columns; track colTitle.field){
            <th [pSortableColumn]="colTitle.enableSorting ? colTitle.field : null"
                [pSortableColumnDisabled]="!colTitle.enableSorting"
                [pTooltip]="colTitle.headerTooltip"
                [tooltipDisabled]="!colTitle.headerTooltip"
                [style]="colTitle.style">
              @if (!colTitle.isCheckbox){
                <span>{{ colTitle.title }}</span>
              }
              @else{
                <span><p-tableHeaderCheckbox></p-tableHeaderCheckbox></span>
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
            {{ 'GLOBAL.TABLES.NO_RESULTS' | translate }}
          </td>
        </tr>
      </ng-template>
      <ng-template
        pTemplate="body"
        let-tableItem>
        <tr>
          @for(col of colTitles(); track col.field;){
            <td [style]="col.style">
              @if(!col.isCheckbox){
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
                    <span>{{ col.field ? (tableItem[col.field]) : '' }}</span>
                  }
                  @if(!col.isLink && !col.isDate && !col.isImage  && !col.isTranslatable ){
                    <span>{{ col.field ? tableItem[col.field] : '' }}</span>
                  }
                  @if(!col.isLink && col.isDate && !col.isImage){
                    <span>{{ (col.field ? tableItem[col.field] : '') | date : 'dd/MM/yyyy' }}</span>
                  }
                  @if(!col.isLink && !col.isDate && !col.isImage  && col.isTranslatable && col.isStatus && statusClasses()){
                    <span class="badge" [ngClass]="getClass(col.field ? tableItem[col.field] : '')">
                        {{col.field ? tableItem[col.field] : '' }}
                    </span>
                  }
                </span>
              }
              @if(col.isCheckbox){
                <span>
                    <p-tableCheckbox [value]="tableItem"></p-tableCheckbox>
                </span>
              }
              @if(col.isButton){
                <span>
                </span>
              }
              @if(col.isTableActions){
                <span class="text-dark fw-bolder mb-1 fs-6">
                  @for(action of col.actions; track action.type;){
                    @switch(action.type){
                      @case ('VIEW'){
                        <ng-container
                          *ngTemplateOutlet="viewBlock; context: {tableItem:tableItem, action:action }">
                              </ng-container>
                      }
                      @case ('EDIT'){
                        <ng-container
                          *ngTemplateOutlet="editBlock; context: { tableItem:tableItem,action:action }">
                              </ng-container>
                      }
                      @case ('DELETE'){
                        <ng-container
                          *ngTemplateOutlet="deleteBlock; context: {uuid:tableItem['uuid'] ,action:action }">
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
              pRipple
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
                pRipple
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
        <button
          pButton
          pRipple
          type="button"
          pButtonIcon="pi pi-file-export"
          (click)="overrideDefaultExport() ? exportParentFunction() : td.exportCSV()"
          [disabled]="totalRecords() >= maxResultsCsvExport || loading || !tableItems || tableItems.length === 0"
        >
          {{ (overrideDefaultExport() ? exportLabel() : exportButtonLabel) | translate }}
        </button>
        <button
          pButton
          pRipple
          type="button"
          pButtonIcon="pi pi-check"
          (click)="handleSelectItemsClicked()"
          [disabled]="!selectedItems || selectedItems.length === 0"
        >
          {{ selectButtonLabelKey() | translate }}
        </button>
      </ng-template>
    </p-table>
  `,
  styleUrl: './results-table.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ResultsTableComponent {
  private utilService= inject(UtilService);

  protected selectedItems!:BaseModel[];
  protected arrayObj = Array;
  protected rowsPerPageOptions:number[]=[10,20,50,100];
  public showTablePaginator = false;
  public showTableFilter = false;
  public showTableToolBar=false;
  protected exportButtonLabel: string = 'GLOBAL.BUTTONS.export-to-csv';
  protected maxResultsCsvExport = 100;


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

  protected getClass(field:string):string|undefined{
    return this.statusClasses()?.get(field);
  }

  protected exportParentFunction(): void {
    this.exportFunction();
  }

  protected getFormattedDate(dateStr: string | null): string {
    let retVal: string | null = '';
    if (dateStr !== null) {
      // retVal = this.utilService.convertDateStringToCalendarFormat(dateStr);
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

}

type Function = (args?: any) => void;
