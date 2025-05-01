import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-protected',
  standalone:false,
  template: `
    <div>
      <app-sidebar></app-sidebar>
      <div class="relative md:ml-64 bg-blueGray-100">
        <app-header></app-header>
        <div class="px-4 md:px-10 mx-auto w-full -m-24">
          <router-outlet></router-outlet>
          <app-footer-small [absolute]="false"></app-footer-small>
        </div>
      </div>
    </div>
  `,
  styleUrl: './protected.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProtectedComponent {

}
