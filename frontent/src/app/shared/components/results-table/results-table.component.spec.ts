import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { TranslateLoader, TranslateModule, TranslateNoOpLoader } from '@ngx-translate/core';

import { ResultsTableComponent } from './results-table.component';

describe('ResultsTableComponent', () => {
  let component: ResultsTableComponent;
  let fixture: ComponentFixture<ResultsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ResultsTableComponent,
        TranslateModule.forRoot({
          loader: {provide: TranslateLoader, useClass: TranslateNoOpLoader}
        }),
      ],
      providers: [
        // ResultsTableComponent's isLink/isTableActions columns render app-link,
        // which uses RouterLink and needs a router present to resolve ActivatedRoute.
        provideRouter([]),
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(ResultsTableComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('tableItems', []);
    fixture.componentRef.setInput('colTitles', []);
    fixture.componentRef.setInput('totalRecords', 0);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
