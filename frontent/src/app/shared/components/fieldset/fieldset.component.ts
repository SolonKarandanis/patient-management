import {ChangeDetectionStrategy, Component, input} from '@angular/core';
import {Fieldset} from 'primeng/fieldset';

@Component({
  selector: 'app-fieldset',
  imports: [
    Fieldset
  ],
  template: `
    <p-fieldset [legend]="legend()"
                [toggleable]="toggleable()"
                [collapsed]="collapsed()" >
      <ng-content></ng-content>
    </p-fieldset>
  `,
  styleUrl: './fieldset.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FieldsetComponent {
  legend = input('');
  toggleable = input(true);
  collapsed = input(false);
}
