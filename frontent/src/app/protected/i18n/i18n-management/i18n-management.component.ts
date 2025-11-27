import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-i18n-management',
  imports: [],
  template: `
    <p>
      i18n-management works!
    </p>
  `,
  styleUrl: './i18n-management.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class I18nManagementComponent {

}
