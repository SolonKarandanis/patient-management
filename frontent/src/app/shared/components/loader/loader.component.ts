import { ChangeDetectionStrategy, Component } from '@angular/core';
import {UiService} from '@core/services/ui.service';
import {BlockUI} from 'primeng/blockui';
import {ProgressSpinner} from 'primeng/progressspinner';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-loader',
  imports: [
    BlockUI,
    ProgressSpinner,
    TranslatePipe
  ],
  template: `
    <div class="on-top">
        <p-block-ui [blocked]="uiService.screenLoaderVisible()" >
          <div class="flex flex-row place-content-center  absolute left-[50%] top-[50%]">
            <p-progressSpinner [style]="{width:'80px',height:'50px'}"></p-progressSpinner>
            <div class="text-3xl text-white txt-shadow">
              {{uiService.loaderMessage() ?? 'SCREEN-LOADER.loading'| translate}}
            </div>
          </div>
        </p-block-ui>
    </div>
  `,
  styleUrl: './loader.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoaderComponent {
  constructor(protected uiService:UiService) {
  }
}
