import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-errors',
  standalone:false,
  template: `
    <router-outlet></router-outlet>
  `,
  styleUrl: './errors.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ErrorsComponent {

}
