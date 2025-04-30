import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-errors500',
  imports: [],
  template: `
    <h1 class="fw-bolder fs-4x text-gray-700 mb-10">System Error</h1>

    <div class="fw-bold fs-3 text-gray-400 mb-15">
      Something went wrong! <br />
      Please try again later.
    </div>
  `,
  styleUrl: './errors500.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Errors500Component {

}
