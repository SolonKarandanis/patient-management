import { ChangeDetectionStrategy, Component } from '@angular/core';
import {UserDropdownComponent} from '../user-dropdown/user-dropdown.component';

@Component({
  selector: 'app-header',
  imports: [
    UserDropdownComponent
  ],
  template: `
    <!-- Navbar -->
    <nav class="relative bg-red-600 md:pt-10 pb-10 pt-10  top-0 left-0 w-full z-10  md:flex-row
        md:flex-nowrap md:justify-start flex items-center p-4">
      <div class=" w-full mx-auto items-center flex justify-between md:flex-nowrap flex-wrap ">
        <!-- Brand -->
        <a class="text-white text-sm uppercase hidden lg:inline-block font-semibold">
          Dashboard
        </a>
        <!-- Form -->
        <div class="flex justify-between gap-5">
          <form class="md:flex hidden flex-row flex-wrap items-center lg:ml-auto mr-3" role="search">
            <div class="relative flex w-full flex-wrap items-stretch">
              <span class="z-10 h-full leading-snug font-normal text-center text-blueGray-300
                  absolute bg-transparent rounded text-base items-center justify-center w-8 pl-3 py-3">
                <i class="pi pi-search"></i>
              </span>
              <input type="text"
                     placeholder="Search here..."
                     class="border-0 px-3 py-3 placeholder-blueGray-300 text-blueGray-600 relative
              bg-white rounded text-sm shadow outline-none focus:outline-none focus:ring w-full pl-10"
              />
            </div>
          </form>
          <!-- User -->
          <ul class="flex-col md:flex-row list-none items-center hidden md:flex">
            <app-user-dropdown ></app-user-dropdown>
          </ul>
        </div>
      </div>
    </nav>
  `,
  styleUrl: './header.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HeaderComponent {

}
