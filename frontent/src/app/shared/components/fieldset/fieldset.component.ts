import {ChangeDetectionStrategy, Component, input, linkedSignal, output} from '@angular/core';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {lucideChevronDown} from '@ng-icons/lucide';
import {HlmCollapsibleImports} from '@components/ui/collapsible';
import {
  FieldsetHeaderWithButtonsComponent
} from '@components/fieldset-header-with-buttons/fieldset-header-with-buttons.component';
import {FieldsetEditButtonsComponent} from '@components/fieldset-edit-buttons/fieldset-edit-buttons.component';

@Component({
  selector: 'app-fieldset',
  imports: [
    HlmCollapsibleImports,
    NgIcon,
    FieldsetHeaderWithButtonsComponent,
    FieldsetEditButtonsComponent
  ],
  providers: [provideIcons({lucideChevronDown})],
  template: `
    <div hlmCollapsible [(expanded)]="expanded" class="bg-slate-100 text-black rounded-md p-2">
      <app-fieldset-header-with-buttons>
        <span titleText class="font-semibold">{{legend()}}</span>
        @if(allowEdit()){
          <app-fieldset-edit-buttons [allowSave]="allowSave()" />
        }
        @if(toggleable()){
          <button hlmCollapsibleTrigger type="button" class="ml-auto" [attr.aria-label]="legend()">
            <ng-icon name="lucideChevronDown" class="transition-transform" [class.rotate-180]="expanded()" />
          </button>
        }
      </app-fieldset-header-with-buttons>
      <div hlmCollapsibleContent>
        <ng-content></ng-content>
      </div>
    </div>
  `,
  styleUrl: './fieldset.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FieldsetComponent {
  allowEdit = input(false);
  allowSave = input(false);
  legend = input('');
  toggleable = input(true);
  collapsed = input(false);

  protected readonly expanded = linkedSignal(() => !this.collapsed());

  editModeChanged = output<boolean>();
  saveClicked = output<boolean>();
  validateFormClicked = output<boolean>();
  resetFormValidityClicked = output<boolean>();
}
