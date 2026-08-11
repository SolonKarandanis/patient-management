import {inject, Injectable} from '@angular/core';
import {BrnDialogService} from '@spartan-ng/brain/dialog';
import {TranslateService} from '@ngx-translate/core';
import {ConfirmDialogComponent} from '@components/confirm-dialog/confirm-dialog.component';

export interface ConfirmContext {
  header?: string;
  message: string;
  acceptLabel: string;
  rejectLabel: string;
}

export interface ConfirmOptions {
  header?: string;
  message: string;
  accept: () => void;
  reject?: () => void;
  acceptLabel?: string;
  rejectLabel?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ConfirmService {

  private readonly dialogService = inject(BrnDialogService);
  private readonly translate = inject(TranslateService);

  confirm(options: ConfirmOptions): void {
    const context: ConfirmContext = {
      header: options.header,
      message: options.message,
      acceptLabel: options.acceptLabel ?? this.translate.instant('GLOBAL.BUTTONS.yes'),
      rejectLabel: options.rejectLabel ?? this.translate.instant('GLOBAL.BUTTONS.no'),
    };

    const dialogRef = this.dialogService.open<ConfirmContext, boolean>(ConfirmDialogComponent, undefined, context, {
      role: 'alertdialog',
    });

    dialogRef.closed$.subscribe((accepted) => {
      if (accepted) {
        options.accept();
      } else {
        options.reject?.();
      }
    });
  }
}
