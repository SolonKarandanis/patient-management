import {Attribute, ChangeDetectionStrategy, Component, input} from '@angular/core';
import {SearchTableColumn, SearchTableColumnAction} from '@models/search.model';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-link',
  imports: [
    RouterLink
  ],
  template: `
    @if(config(); as config){
      <a [attr.data-tool-tip]="config.toolTip"
         [routerLink]="
        arrayObj.prototype.concat(
            config.routerLinkConfig?.preRoutes ? config.routerLinkConfig?.preRoutes : [],
            [config.dataFieldForRoute ? tableItem()[config.dataFieldForRoute] : ''],
            config.routerLinkConfig?.postRoutes ? config.routerLinkConfig?.postRoutes : []
        )"
         [class]="cssClasses">
        <ng-content ></ng-content>
      </a>
    }
  `,
  styleUrl: './link.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LinkComponent {

  constructor(
    @Attribute('cssClasses') public cssClasses:string=''
  ) {
  }

  config = input.required<SearchTableColumn | SearchTableColumnAction>();
  tableItem = input.required<Record<string,unknown>>();

  protected arrayObj = Array;

}
