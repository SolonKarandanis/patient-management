import {Injectable} from '@angular/core';
import {toast} from '@spartan-ng/brain/sonner';

export interface ToastOptions {
  sticky?: boolean;
  closable?: boolean;
  life?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  success(detail: string, summary: string, options?: ToastOptions): void {
    toast.success(summary, this.toToastData(detail, options));
  }

  error(detail: string, summary: string, options?: ToastOptions): void {
    toast.error(summary, this.toToastData(detail, options));
  }

  warn(detail: string, summary: string, options?: ToastOptions): void {
    toast.warning(summary, this.toToastData(detail, options));
  }

  private toToastData(detail: string, options?: ToastOptions) {
    return {
      description: detail,
      duration: options?.sticky ? Infinity : options?.life,
      closeButton: options?.closable,
    };
  }
}
