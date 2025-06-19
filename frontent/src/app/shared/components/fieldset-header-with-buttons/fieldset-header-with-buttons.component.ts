import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-fieldset-header-with-buttons',
  imports: [],
  template: `
    <div class="flex items-center">
      <ng-content select="[titleText]"></ng-content>
      <ng-content></ng-content>
    </div>
  `,
  styleUrl: './fieldset-header-with-buttons.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FieldsetHeaderWithButtonsComponent {

}
