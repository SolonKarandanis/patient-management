import { ChangeDetectionStrategy, Component } from '@angular/core';
import {TranslatePipe} from '@ngx-translate/core';
import {Divider} from 'primeng/divider';

@Component({
  selector: 'app-required-fields-label',
  imports: [
    TranslatePipe,
    Divider
  ],
  template: `
    <p-divider></p-divider>
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
    <p-divider></p-divider>
  `,
  styleUrl: './required-fields-label.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RequiredFieldsLabelComponent {

}
