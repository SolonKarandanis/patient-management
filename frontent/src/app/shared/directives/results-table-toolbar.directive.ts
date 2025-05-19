import {Directive, OnInit} from '@angular/core';
import {ResultsTableComponent} from '@components/results-table/results-table.component';

@Directive({
  selector: '[app-results-table[tableToolbar]]',
  standalone:true
})
export class ResultsTableToolbarDirective implements OnInit{

  constructor(private host:ResultsTableComponent) { }

  ngOnInit(): void {
    this.host.showTableToolBar=true;
  }
}
