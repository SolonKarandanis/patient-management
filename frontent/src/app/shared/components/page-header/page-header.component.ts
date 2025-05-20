import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-page-header',
  imports: [],
  template: `
    <div class="rounded-t bg-white mb-0 px-6 py-6">
      <div class="text-center flex justify-between">
        <h6 class="text-blueGray-700 text-xl font-bold">
          <ng-content></ng-content>
        </h6>
      </div>
    </div>
  `,
  styleUrl: './page-header.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageHeaderComponent {

}
