import {Directive, OnInit} from '@angular/core';
import {ResultsTableComponent} from '@components/results-table/results-table.component';

@Directive({
  selector: '[app-results-table[tableSelection]]'
})
export class ResultsTableSelectDirective implements OnInit{

  constructor(private host:ResultsTableComponent) { }

  ngOnInit(): void {
    this.host.selectionEnabled=true;
  }

}
