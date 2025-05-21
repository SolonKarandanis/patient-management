import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {PageHeaderComponent} from '@components/page-header/page-header.component';
import {BaseComponent} from '@shared/abstract/BaseComponent';

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
        <div role="search">
          <form>
            <div class="grid gap-6 mb-6 md:grid-cols-2 mt-4">
              <div>
                <label for="first_name" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">First name</label>
                <input type="text" id="first_name" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="John" required />
              </div>
              <div>
                <label for="last_name" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Last name</label>
                <input type="text" id="last_name" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="Doe" required />
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styleUrl: './search-users.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchUsersComponent extends BaseComponent implements OnInit{


    ngOnInit(): void {
      this.initForm();
    }

  private initForm():void{

  }

}
