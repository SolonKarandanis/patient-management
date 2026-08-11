import {ChangeDetectionStrategy, Component, inject, input, output, signal} from '@angular/core';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {lucideCircleX, lucideFileEdit, lucideSave} from '@ng-icons/lucide';
import {TranslatePipe} from '@ngx-translate/core';
import {HlmButtonImports} from '@components/ui/button';
import {HlmTooltipImports} from '@components/ui/tooltip';
import {FieldsetComponent} from '@components/fieldset/fieldset.component';

@Component({
  selector: 'app-fieldset-edit-buttons',
  imports: [
    TranslatePipe,
    HlmButtonImports,
    HlmTooltipImports,
    NgIcon
  ],
  providers: [provideIcons({lucideFileEdit, lucideSave, lucideCircleX})],
  template: `
    @if(!isEditMode()){
      @let editLabel = 'GLOBAL.BUTTONS.edit' | translate;
      <button
        hlmBtn
        variant="ghost"
        size="icon"
        type="button"
        class="rounded-4xl!"
        [hlmTooltip]="editLabel"
        [attr.aria-label]="editLabel"
        (click)="enterEditMode()"
      >
        <ng-icon name="lucideFileEdit" />
      </button>
    }
    @if(isEditMode()){
      @let saveLabel = 'GLOBAL.BUTTONS.save' | translate;
      <button
        hlmBtn
        variant="ghost"
        size="icon"
        type="button"
        class="rounded-4xl!"
        [hlmTooltip]="saveLabel"
        [attr.aria-label]="saveLabel"
        (click)="saveClickHandler()"
      >
        <ng-icon name="lucideSave" />
      </button>
      @let cancelLabel = 'GLOBAL.BUTTONS.cancel' | translate;
      <button
        hlmBtn
        variant="ghost"
        size="icon"
        type="button"
        class="rounded-4xl!"
        [hlmTooltip]="cancelLabel"
        [attr.aria-label]="cancelLabel"
        (click)="exitEditMode()"
      >
        <ng-icon name="lucideCircleX" />
      </button>
    }
  `,
  styleUrl: './fieldset-edit-buttons.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FieldsetEditButtonsComponent {

  private fieldSet = inject(FieldsetComponent);

  isEditMode = signal(false);
  allowSave = input(false);

  protected enterEditMode(): void {
    this.emitEditModeValue(true);
  }

  protected exitEditMode(): void {
    this.fieldSet.resetFormValidityClicked.emit(true);
    this.emitEditModeValue(false);
  }

  protected saveClickHandler(): void {
    this.fieldSet.validateFormClicked.emit(true);
    if(this.allowSave()){
      this.fieldSet.saveClicked.emit(true);
      this.exitEditMode();
    }
  }

  private emitEditModeValue(editMode:boolean): void {
    this.isEditMode.set(editMode);
    this.fieldSet.editModeChanged.emit(editMode);
  }

}
