import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-loader',
  imports: [],
  template: `
    <p>
      loader works!
    </p>
  `,
  styleUrl: './loader.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoaderComponent {

}
