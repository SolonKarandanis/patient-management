import { ChangeDetectionStrategy, Component } from '@angular/core';
import {PageHeaderComponent} from '@components/page-header/page-header.component';

@Component({
  selector: 'app-search-users',
  imports: [
    PageHeaderComponent
  ],
  template: `
    <div class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-blueGray-100 border-0">
      <app-page-header>
        Search Users
      </app-page-header>
      <div class="flex-auto px-4 lg:px-10 py-10 pt-0">
        <form>
          <h6 class="text-blueGray-400 text-sm mt-3 mb-6 font-bold uppercase">
            User Information
          </h6>
        </form>
      </div>
    </div>
  `,
  styleUrl: './search-users.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchUsersComponent {

}
