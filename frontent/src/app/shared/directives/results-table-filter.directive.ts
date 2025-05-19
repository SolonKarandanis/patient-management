import {Directive, OnInit} from '@angular/core';
import {ResultsTableComponent} from '@components/results-table/results-table.component';

@Directive({
  selector: '[app-results-table[tableFilter]]',
  standalone:true
})
export class ResultsTableFilterDirective implements OnInit{

  constructor(private host:ResultsTableComponent) { }

  ngOnInit(): void {
    this.host.showTableFilter=true;
  }

}
