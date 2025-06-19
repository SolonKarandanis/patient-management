import {ChangeDetectionStrategy, Component, output} from '@angular/core';
import {ButtonDirective, ButtonIcon} from 'primeng/button';
import {TranslatePipe} from '@ngx-translate/core';
import {Ripple} from 'primeng/ripple';
import {Tooltip} from 'primeng/tooltip';

@Component({
  selector: 'app-fieldset-edit-buttons',
  imports: [
    TranslatePipe,
    Ripple,
    Tooltip,
    ButtonDirective
  ],
  template: `
    @if(!isEditMode){
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
    @if(isEditMode){
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
