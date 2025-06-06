import { ChangeDetectionStrategy, Component } from '@angular/core';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {NgClass} from '@angular/common';
import {MenuItem} from 'primeng/api';

@Component({
  selector: 'app-sidebar',
  imports: [
    RouterLink,
    NgClass,
    RouterLinkActive,
  ],
  template: `
    <nav class="md:left-0 md:block md:fixed md:top-0 md:bottom-0 md:overflow-y-auto md:flex-row
        md:flex-nowrap md:overflow-hidden shadow-xl bg-white flex flex-wrap items-center justify-between relative md:w-64 z-10 py-4 px-6">
      <div class="md:flex-col md:items-stretch md:min-h-full md:flex-nowrap px-0 flex flex-wrap items-center justify-between w-full mx-auto">
        <!-- Toggler -->
        <button
          class="cursor-pointer text-black opacity-50 md:hidden px-3 py-1 text-xl leading-none bg-transparent
            rounded border border-solid border-transparent"
          type="button"
          (click)="toggleCollapseShow('bg-white m-2 py-3 px-6')">
          <i class="pi pi-bars"></i>
        </button>
        <!-- Brand -->
        <a [routerLink]="['/']"
          class="md:block text-left md:pb-2 text-blueGray-600 mr-0 inline-block whitespace-nowrap text-sm uppercase font-bold p-4 px-0">
          <span class="block sm:hidden"> Tailwind Angular </span>
          <span class="hidden sm:block"> Notus Angular </span>
        </a>
        <ul class="md:hidden items-center flex flex-wrap list-none">
          <li class="inline-block relative">
<!--            <p-splitbutton icon="pi pi-bell" [model]="menuItems" />-->
          </li>
          <li class="inline-block relative">
<!--            <app-user-dropdown class="block"></app-user-dropdown>-->
          </li>
        </ul>
        <!-- Collapse -->
        <div
          class="md:flex md:flex-col md:items-stretch md:opacity-100 md:relative md:mt-4 md:shadow-none shadow absolute top-0 left-0 right-0
          z-40 overflow-y-auto overflow-x-hidden h-auto items-center flex-1 rounded"
          [ngClass]="collapseShow">
          <!-- Collapse header -->
          <div
            class="md:min-w-full md:hidden block pb-4 mb-4 border-b border-solid border-blueGray-200">
            <div class="flex flex-wrap">
              <div class="w-6/12">
                <a [routerLink]="['/']"
                  class="md:block text-left md:pb-2 text-blueGray-600 mr-0 inline-block whitespace-nowrap text-sm uppercase font-bold p-4 px-0">
                  Notus Angular
                </a>
              </div>
              <div class="w-6/12 flex justify-end">
                <button
                  type="button"
                  class="cursor-pointer text-black opacity-50 md:hidden px-3 py-1 text-xl leading-none bg-transparent rounded border
                  border-solid border-transparent"
                  (click)="toggleCollapseShow('hidden')">
                  <i class="pi pi-times"></i>
                </button>
              </div>
            </div>
          </div>
          <!-- Form -->
          <form class="mt-6 mb-4 md:hidden">
            <div class="mb-3 pt-0">
              <input
                type="text"
                placeholder="Search"
                class="px-3 py-2 h-12 border border-solid border-blueGray-500 placeholder-blueGray-300 text-blueGray-600
                bg-white rounded text-base leading-snug shadow-none outline-none focus:outline-none w-full font-normal"
              />
            </div>
          </form>
          <!-- Divider -->
          <hr class="my-4 md:min-w-full" />
          <!-- Heading -->
          <h6 class="md:min-w-full text-blueGray-500 text-xs uppercase font-bold block pt-1 pb-4 no-underline">
            Admin Layout Pages
          </h6>
          <!-- Navigation -->
          <ul class="md:flex-col md:min-w-full flex flex-col list-none">
            <li class="items-center">
              <a [routerLink]="['/dashboard']"
                class="text-xs uppercase py-3 font-bold block"
                routerLinkActive
                #adminDashboard="routerLinkActive"
                [ngClass]="adminDashboard.isActive ? 'text-red-600 hover:text-red-700' : 'text-blueGray-700 hover:text-blueGray-500'">
                <i class="pi pi-desktop mr-2 text-sm"
                  [ngClass]="adminDashboard.isActive ? 'opacity-75' : 'text-blueGray-300'">
                </i>
                Dashboard
              </a>
            </li>
            <li class="items-center">
              <a [routerLink]="['/users']"
                 class="text-xs uppercase py-3 font-bold block"
                 routerLinkActive
                 #userRoute="routerLinkActive"
                 [ngClass]="userRoute.isActive ? 'text-red-600 hover:text-red-700' : 'text-blueGray-700 hover:text-blueGray-500'">
                <i class="pi pi-desktop mr-2 text-sm"
                   [ngClass]="userRoute.isActive ? 'opacity-75' : 'text-blueGray-300'">
                </i>
                Users
              </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  `,
  styleUrl: './sidebar.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SidebarComponent {
  collapseShow = "hidden";

  menuItems!:MenuItem[];

  constructor() {
    this.menuItems=[
      {
        label: 'Update',
        icon: 'pi pi-refresh',
        // command: () => {
        //   this.messageService.add({ severity: 'success', summary: 'Updated', detail: 'Data Updated', life: 3000 });
        // },
      },
      {
        label: 'Delete',
        icon: 'pi pi-times',
        // command: () => {
        //   this.messageService.add({ severity: 'warn', summary: 'Delete', detail: 'Data Deleted', life: 3000 });
        // },
      },
      {
        separator: true,
      },
      {
        label: 'Quit',
        icon: 'pi pi-power-off',
        // command: () => {
        //   window.open('https://angular.io/', '_blank');
        // },
      },
    ];
  }

  protected toggleCollapseShow(classes:string):void {
    this.collapseShow = classes;
  }
}
