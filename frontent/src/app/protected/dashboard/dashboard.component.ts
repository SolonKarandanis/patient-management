import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  imports: [],
  template: `
    <div class="flex flex-wrap">
      <div class="w-full xl:w-8/12 mb-12 xl:mb-0 px-4 text-black">
        dashboard
      </div>
    </div>
  `,
  styleUrl: './dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent {

}
