import { ChangeDetectionStrategy, Component } from '@angular/core';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-unauthorized',
  imports: [
    TranslatePipe
  ],
  template: `
    <div class="text-center">
      <span class="pi pi-ban text-8xl"></span>
      <p class="text-3xl">{{ 'GLOBAL.UNAUTHORIZED' | translate }}</p>
    </div>
  `,
  styleUrl: './unauthorized.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnauthorizedComponent {

}
