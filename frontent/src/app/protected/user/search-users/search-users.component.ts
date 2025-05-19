import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-search-users',
  imports: [],
  template: `
    <p>
      search-users works!
    </p>
  `,
  styleUrl: './search-users.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchUsersComponent {

}
