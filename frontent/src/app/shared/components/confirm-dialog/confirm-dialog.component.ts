import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {BrnDialogRef, injectBrnDialogContext} from '@spartan-ng/brain/dialog';
import {HlmAlertDialogImports} from '@components/ui/alert-dialog';
import {HlmButtonImports} from '@components/ui/button';
import {ConfirmContext} from '@core/services/confirm.service';

@Component({
  selector: 'app-confirm-dialog',
  imports: [
    HlmAlertDialogImports,
    HlmButtonImports
  ],
  template: `
    <div hlmAlertDialogContent>
      <div hlmAlertDialogHeader>
        @if(context.header){
          <h2 hlmAlertDialogTitle>{{context.header}}</h2>
        }
        <p hlmAlertDialogDescription>{{context.message}}</p>
      </div>
      <div hlmAlertDialogFooter>
        <button hlmBtn variant="outline" type="button" (click)="reject()">{{context.rejectLabel}}</button>
        <button hlmBtn type="button" (click)="accept()">{{context.acceptLabel}}</button>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfirmDialogComponent {
  protected readonly context = injectBrnDialogContext<ConfirmContext>();
  private readonly dialogRef = inject(BrnDialogRef<boolean>);

  protected accept(): void {
    this.dialogRef.close(true);
  }

  protected reject(): void {
    this.dialogRef.close(false);
  }
}
