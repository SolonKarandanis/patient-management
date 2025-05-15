import {ChangeDetectionStrategy, Component, computed, inject, signal} from '@angular/core';
import {AuthService} from '@core/services/auth.service';

@Component({
  selector: 'app-user-dropdown',
  imports: [],
  template: `
    <button
      id="dropdownUserAvatarButton"
      data-dropdown-toggle="dropdownAvatar"
      class="flex text-sm bg-gray-800 rounded-full md:me-0 focus:ring-4 focus:ring-gray-300 dark:focus:ring-gray-600"
      type="button"
      (click)="toggleDropdown()">
      <span class="sr-only">Open user menu</span>
      <img class="w-8 h-8 rounded-full" src="assets/img/team-1-800x800.jpg" alt="user photo">
    </button>

    <!-- Dropdown menu -->
    <div id="dropdownAvatar"
         class="z-10 absolute top-16 right-3 bg-white divide-y divide-gray-100 rounded-lg shadow-sm w-44 dark:bg-gray-700 dark:divide-gray-600"
         [class.hidden]="dropDownHidden()">
      <div class="px-4 py-3 text-sm text-gray-900 dark:text-white">
        <div>{{ fullName() }}</div>
        <div class="font-medium truncate">{{ loggedInUser()?.email }}</div>
      </div>
      <ul class="py-2 text-sm text-gray-700 dark:text-gray-200" aria-labelledby="dropdownUserAvatarButton">
        <li>
          <a href="#"
             class="block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:text-white">Dashboard</a>
        </li>
        <li>
          <a href="#"
             class="block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:text-white">Settings</a>
        </li>
        <li>
          <a href="#"
             class="block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:text-white">Earnings</a>
        </li>
      </ul>
      <div class="py-2">
        <button
           class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 dark:hover:bg-gray-600 dark:text-gray-200
           dark:hover:text-white"
            (click)="logout()">
          Sign out
        </button>
      </div>
    </div>
  `,
  styleUrl: './user-dropdown.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDropdownComponent {
  private authService = inject(AuthService);

  protected loggedInUser =this.authService.loggedUser;

  protected fullName = computed(()=> `${this.loggedInUser()?.firstName} ${this.loggedInUser()?.lastName}`);

  dropDownHidden = signal(true);

  public toggleDropdown():void{
    this.dropDownHidden.set(!this.dropDownHidden());
  }

  public logout():void{
    this.authService.logout();
  }
}
