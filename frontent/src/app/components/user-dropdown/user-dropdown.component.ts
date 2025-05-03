import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-user-dropdown',
  imports: [],
  template: `
    <p>
      user-dropdown works!
    </p>
  `,
  styleUrl: './user-dropdown.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDropdownComponent {

}
