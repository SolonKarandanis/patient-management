import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-protected',
  standalone:false,
  template: `
    <div>
      <app-sidebar></app-sidebar>
      <div class="relative md:ml-64 bg-blueGray-100">
        <div class="px-4 md:px-10 mx-auto w-full -m-24">
          <router-outlet></router-outlet>
        </div>
      </div>
    </div>
  `,
  styleUrl: './protected.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProtectedComponent {

}
