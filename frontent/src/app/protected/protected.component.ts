import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-protected',
  standalone:false,
  template: `
    <div>
      <div class="relative md:ml-64 bg-blueGray-100">

      </div>
    </div>
  `,
  styleUrl: './protected.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProtectedComponent {

}
