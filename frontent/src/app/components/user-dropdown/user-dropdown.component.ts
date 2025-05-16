import {ChangeDetectionStrategy, Component, computed, inject} from '@angular/core';
import {AuthService} from '@core/services/auth.service';

@Component({
  selector: 'app-user-dropdown',
  imports: [],
  template: `
    <section class="dropdown">
      <button
        id="dropdownUserAvatarButton"
        data-dropdown-toggle="dropdownAvatar"
        class="dropdown__title"
        type="button"
        aria-expanded="false"
        aria-controls="dropdownAvatar">
        <span class="sr-only">Open user menu</span>
        <img class="w-8 h-8 rounded-full" src="assets/img/team-1-800x800.jpg" alt="user photo">
      </button>

      <!-- Dropdown menu -->
      <ul class="dropdown__menu"  aria-labelledby="dropdownUserAvatarButton" id="dropdownAvatar">
        <li>
          <div class="px-4 py-3 text-sm text-gray-900 dark:text-white">
            <div>{{ fullName() }}</div>
            <div class="font-medium truncate">{{ loggedInUser()?.email }}</div>
          </div>
        </li>
        <li>
          <a href="#"
             class="block px-4 py-2 text-gray-900 hover:bg-gray-100 dark:hover:bg-gray-200 dark:hover:text-white">
            Dashboard
          </a>
        </li>
        <li>
          <a href="#"
             class="block px-4 py-2 text-gray-900 hover:bg-gray-100 dark:hover:bg-gray-200 dark:hover:text-white">
            Settings
          </a>
        </li>
        <li>
          <a href="#"
             class="block px-4 py-2 text-gray-900 hover:bg-gray-100 dark:hover:bg-gray-200 dark:hover:text-white" >
            Earnings
          </a>
        </li>
        <li>
          <button
            type="button"
            class="block px-4 py-2 text-gray-900 hover:bg-gray-100 dark:hover:!bg-gray-200 dark:hover:text-white w-full text-left"
            (click)="logout()">
            Sign out
          </button>
        </li>
      </ul>
    </section>

  `,
  styleUrl: './user-dropdown.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDropdownComponent {
  private authService = inject(AuthService);

  protected loggedInUser =this.authService.loggedUser;

  protected fullName = computed(()=> `${this.loggedInUser()?.firstName} ${this.loggedInUser()?.lastName}`);

  public logout():void{
    this.authService.logout();
  }
}
