import { ChangeDetectionStrategy, Component } from '@angular/core';
import {TranslatePipe} from '@ngx-translate/core';
import {HlmSeparatorImports} from '@components/ui/separator';

@Component({
  selector: 'app-required-fields-label',
  imports: [
    TranslatePipe,
    HlmSeparatorImports
  ],
  template: `
    <div class="p-2 m-2 text-black">
      <span>
        {{ 'REQUIRED-FIELDS-LABEL.part1' | translate }}
      </span>
      <span>
        (<label class="app-required-label mr-1"></label>)
      </span>
      <span>
        {{ 'REQUIRED-FIELDS-LABEL.part2' | translate }}
      </span>
    </div>
    <hlm-separator></hlm-separator>
  `,
  styleUrl: './required-fields-label.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RequiredFieldsLabelComponent {

}
