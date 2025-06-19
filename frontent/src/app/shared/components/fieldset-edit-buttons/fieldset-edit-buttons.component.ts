import {ChangeDetectionStrategy, Component, inject, input, output, signal} from '@angular/core';
import {ButtonDirective, ButtonIcon} from 'primeng/button';
import {TranslatePipe} from '@ngx-translate/core';
import {Ripple} from 'primeng/ripple';
import {Tooltip} from 'primeng/tooltip';
import {FieldsetComponent} from '@components/fieldset/fieldset.component';

@Component({
  selector: 'app-fieldset-edit-buttons',
  imports: [
    TranslatePipe,
    Ripple,
    Tooltip,
    ButtonDirective
  ],
  template: `
    @if(!isEditMode()){
      <button
        pButton
        pRipple
        type="button"
        icon="pi pi-file-edit"
        class="rounded-4xl! bg-transparent! border-transparent!"
        pTooltip="{{ 'GLOBAL.BUTTONS.edit' | translate }}"
        (click)="enterEditMode()"
      ></button>
    }
    @if(isEditMode()){
      <button
        pButton
        pRipple
        type="button"
        icon="pi pi-save"
        class="rounded-4xl! bg-transparent! border-transparent!"
        pTooltip="{{ 'GLOBAL.BUTTONS.save' | translate }}"
        (click)="saveClickHandler()"
      ></button>
      <button
        pButton
        pRipple
        type="button"
        icon="pi pi-times-circle"
        class="rounded-4xl! bg-transparent! border-transparent!"
        pTooltip="{{ 'GLOBAL.BUTTONS.cancel' | translate }}"
        (click)="exitEditMode()"
      ></button>
    }
  `,
  styleUrl: './fieldset-edit-buttons.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FieldsetEditButtonsComponent {

  private fieldSet = inject(FieldsetComponent);

  isEditMode = signal(false);

  protected enterEditMode(): void {
    this.emitEditModeValue(true);
  }

  protected exitEditMode(): void {
    this.emitEditModeValue(false);
  }

  protected saveClickHandler(): void {
    this.fieldSet.saveClicked.emit(true);
    this.exitEditMode();
  }

  private emitEditModeValue(editMode:boolean): void {
    this.isEditMode.set(editMode);
    this.fieldSet.editModeChanged.emit(editMode);
  }

}
