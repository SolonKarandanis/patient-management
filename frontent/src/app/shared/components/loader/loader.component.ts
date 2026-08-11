import { ChangeDetectionStrategy, Component } from '@angular/core';
import {UiService} from '@core/services/ui.service';
import {HlmSpinnerImports} from '@components/ui/spinner';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-loader',
  imports: [
    HlmSpinnerImports,
    TranslatePipe
  ],
  template: `
    <div class="on-top">
      @if(uiService.screenLoaderVisible()){
        <div class="fixed inset-0 z-50 bg-black/50 flex flex-row place-content-center items-center gap-3">
          <hlm-spinner class="text-6xl text-white" />
          <div class="text-3xl text-white txt-shadow">
            {{uiService.loaderMessage() ?? 'SCREEN-LOADER.loading' | translate}}
          </div>
        </div>
      }
    </div>
  `,
  styleUrl: './loader.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoaderComponent {
  constructor(protected uiService:UiService) {
  }
}
