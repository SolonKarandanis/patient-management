import {Directive, OnInit} from '@angular/core';
import {ResultsTableComponent} from '@components/results-table/results-table.component';

@Directive({
  selector: '[app-results-table[tablePaginator]]',
  standalone:true
})
export class ResultsTablePaginatorDirective implements OnInit{

  constructor(private host:ResultsTableComponent) { }

  ngOnInit(): void {
    this.host.showTablePaginator=true;
  }
}
