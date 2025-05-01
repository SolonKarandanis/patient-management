import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  imports: [],
  template: `
    <nav class="md:left-0 md:block md:fixed md:top-0 md:bottom-0 md:overflow-y-auto md:flex-row
        md:flex-nowrap md:overflow-hidden shadow-xl bg-white flex flex-wrap items-center justify-between relative md:w-64 z-10 py-4 px-6">
      <div class="md:flex-col md:items-stretch md:min-h-full md:flex-nowrap px-0 flex flex-wrap items-center justify-between w-full mx-auto">
        <button
          class="cursor-pointer text-black opacity-50 md:hidden px-3 py-1 text-xl leading-none bg-transparent
            rounded border border-solid border-transparent"
          type="button"
          (click)="toggleCollapseShow('bg-white m-2 py-3 px-6')">
          <i class="fas fa-bars"></i>
        </button>
      </div>
    </nav>
  `,
  styleUrl: './sidebar.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SidebarComponent {
  collapseShow = "hidden";

  protected toggleCollapseShow(classes:string):void {
    this.collapseShow = classes;
  }
}
