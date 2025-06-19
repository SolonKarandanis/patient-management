import {ChangeDetectionStrategy, Component, output} from '@angular/core';
import {ButtonDirective, ButtonIcon} from 'primeng/button';
import {TranslatePipe} from '@ngx-translate/core';
import {Ripple} from 'primeng/ripple';
import {Tooltip} from 'primeng/tooltip';

@Component({
  selector: 'app-fieldset-edit-buttons',
  imports: [
    ButtonIcon,
    TranslatePipe,
    Ripple,
    Tooltip,
    ButtonDirective
  ],
  template: `
    @if(!isEditMode){
      <button
        pRipple
        type="button"
        pButtonIcon="pi pi-file-edit"
        class="p-button-rounded p-button-text"
        pTooltip="{{ 'GLOBAL.BUTTONS.edit' | translate }}"
        (click)="enterEditMode()"
      ></button>
    }
    @if(isEditMode){
      <button
        pButton
        pRipple
        type="button"
        pButtonIcon="pi pi-save"
        class="p-button-rounded p-button-text p-button-success"
        pTooltip="{{ 'GLOBAL.BUTTONS.save' | translate }}"
        (click)="saveClickHandler()"
      ></button>
      <button
        pButton
        pRipple
        type="button"
        pButtonIcon="pi pi-times-circle"
        class="p-button-rounded p-button-text"
        pTooltip="{{ 'GLOBAL.BUTTONS.cancel' | translate }}"
        (click)="exitEditMode()"
      ></button>
    }
  `,
  styleUrl: './fieldset-edit-buttons.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FieldsetEditButtonsComponent {
  protected isEditMode: boolean = false;

  editModeChanged =output<boolean>();
  saveClicked =output<boolean>();

  protected enterEditMode(): void {
    this.isEditMode = true;
    this.emitEditModeValue();
  }

  protected exitEditMode(): void {
    this.isEditMode = false;
    this.emitEditModeValue();
  }

  protected saveClickHandler(): void {
    this.saveClicked.emit(true);
    this.exitEditMode();
  }

  private emitEditModeValue(): void {
    this.editModeChanged.emit(this.isEditMode);
  }

}
