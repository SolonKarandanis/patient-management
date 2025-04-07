import {inject, Injectable} from '@angular/core';
import {MessageService} from 'primeng/api';
import {TranslateService} from '@ngx-translate/core';
import {HttpErrorResponse} from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class ErrorService{

  private messageService = inject(MessageService);
  private translate = inject(TranslateService);

  /**
   * Show an error message
   * @param error The HttpErrorResponse received by the API
   * @param errorMessage The message to display
   */
  public showErrorMessage(error: HttpErrorResponse, errorMessage?: string): void {
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: errorMessage ?? this.getErrorMessage(error),
      sticky: this.isSticky(error),
      closable: true,
      life: 10000,
      // key: 'requestError'
    });
  }

  /**
   * Check if an error message should be sticky and not hide automatically
   * @param error The HttpErrorResponse received by the API
   * @returns If an error message should be sticky
   */
  private isSticky(error: HttpErrorResponse): boolean {
    const code: number = error.status;
    return code === 400;
  }

  /**
   * Get the error message to display for a specific HttpErrorResponse
   * @param error The HttpErrorResponse received by the API
   * @returns The error message to display
   */
  private getErrorMessage(error: HttpErrorResponse): string {
    let errorMessage: string;

    switch (error.status) {
      case 401:
        errorMessage = this.translate.instant('GLOBAL.ERRORS.unauthorized');
        break;
      case 403:
        errorMessage = this.translate.instant('GLOBAL.ERRORS.forbidden');
        break;
      case 404:
        errorMessage = this.translate.instant('GLOBAL.ERRORS.not-found');
        break;
      case 0:
      case 500:
      case 501:
      case 502:
      case 503:
      case 504:
      case 505:
      case 506:
      case 507:
      case 508:
      case 509:
      case 510:
      case 511:
        errorMessage = `${this.translate.instant('GLOBAL.ERRORS.server')}: ${error.status}`;
        break;

      default:
        errorMessage = error.message;
        break;
    }

    return errorMessage;
  }
}
