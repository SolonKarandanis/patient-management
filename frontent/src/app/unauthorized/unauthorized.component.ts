import { ChangeDetectionStrategy, Component } from '@angular/core';
import {TranslatePipe} from '@ngx-translate/core';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {lucideBan} from '@ng-icons/lucide';

@Component({
  selector: 'app-unauthorized',
  imports: [
    TranslatePipe,
    NgIcon
  ],
  providers: [provideIcons({lucideBan})],
  template: `
    <div class="text-center">
      <ng-icon name="lucideBan" class="text-8xl" />
      <p class="text-3xl">{{ 'GLOBAL.UNAUTHORIZED' | translate }}</p>
    </div>
  `,
  styleUrl: './unauthorized.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnauthorizedComponent {

}
