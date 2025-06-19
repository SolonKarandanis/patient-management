import {ChangeDetectionStrategy, Component, input, output, signal} from '@angular/core';
import {Fieldset} from 'primeng/fieldset';
import {PrimeTemplate} from 'primeng/api';
import {
  FieldsetHeaderWithButtonsComponent
} from '@components/fieldset-header-with-buttons/fieldset-header-with-buttons.component';
import {FieldsetEditButtonsComponent} from '@components/fieldset-edit-buttons/fieldset-edit-buttons.component';

@Component({
  selector: 'app-fieldset',
  imports: [
    Fieldset,
    PrimeTemplate,
    FieldsetHeaderWithButtonsComponent,
    FieldsetEditButtonsComponent
  ],
  template: `
    <p-fieldset [toggleable]="toggleable()"
                [collapsed]="collapsed()">
      <ng-template pTemplate="header">
        <app-fieldset-header-with-buttons>
          <span titleText>{{legend()}}</span>
          @if(allowEdit()){
            <app-fieldset-edit-buttons />
          }
        </app-fieldset-header-with-buttons>
      </ng-template>
      <ng-content></ng-content>
    </p-fieldset>
  `,
  styleUrl: './fieldset.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FieldsetComponent {
  allowEdit = input(false);
  legend = input('');
  toggleable = input(true);
  collapsed = input(false);

  editModeChanged =output<boolean>();
  saveClicked =output<boolean>();
}
