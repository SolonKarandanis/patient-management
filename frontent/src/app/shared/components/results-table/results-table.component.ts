import {ChangeDetectionStrategy, Component, inject, input, output} from '@angular/core';
import {Table, TableLazyLoadEvent, TableModule} from 'primeng/table';
import {BaseModel} from '@models/base.model';
import {SearchTableColumn} from '@models/search.model';
import {UtilService} from '@core/services/util.service';
import {Paginator} from 'primeng/paginator';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-results-table',
  imports: [
    TableModule,
    Paginator,
    TranslatePipe
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
      [loading]="loading"
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
  // protected maxResultsCsvExport = MAX_RESULTS_CSV_EXPORT;


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

  tableStateChanged = output<TableLazyLoadEvent>();
  itemsSelected = output<BaseModel[]>();
  rowSelectionChanged = output<BaseModel[]>();
  rowSingleSelectionChanged = output<BaseModel[]>();

  protected trackById( li: BaseModel): string| number | undefined {
    return li ? li.publicId : undefined;
  }

  protected applyFilterGlobal($event:Event, stringVal:string,td:Table) {
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

}

type Function = (args?: any) => void;
