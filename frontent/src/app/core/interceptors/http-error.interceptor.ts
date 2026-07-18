import {
  HttpErrorResponse,
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest,
} from '@angular/common/http';
import {inject} from '@angular/core';
import {Router} from '@angular/router';
import { ErrorService } from '@core/services/error.service';
import {HttpUtil} from '@core/services/http-util.service';
import {catchError, retry, throwError, timer} from 'rxjs';

interface ProblemDetailBody {
  detail?: string;
  errors?: Record<string, string>;
}

/**
 * Backend error bodies come in two shapes: a legacy JSON array of translated
 * message strings, or an RFC 7807 ProblemDetail object (`detail`, and for
 * validation errors a field -> message `errors` map).
 */
function extractErrorMessages(error: HttpErrorResponse): string[] {
  const body: unknown = error.error;

  if (Array.isArray(body)) {
    return body;
  }

  if (body && typeof body === 'object') {
    const problemDetail = body as ProblemDetailBody;

    if (problemDetail.errors) {
      return Object.values(problemDetail.errors);
    }

    if (problemDetail.detail) {
      return [problemDetail.detail];
    }
  }

  return [];
}

export const httpError: HttpInterceptorFn = (
  request: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const router = inject(Router);
  const errorService = inject(ErrorService);
  const httpUtil = inject(HttpUtil);

  let retriesCount = 0;
  let maxRetries = 0;
  return next(request).pipe(
    retry({
      delay: (err) => {
        let status;

        if (err instanceof HttpErrorResponse && (err).status !== undefined) {
          status = (err).status.toString();
        }

        // retry for unknown or 5XX errors
        if (retriesCount < maxRetries && status !== undefined && (status.startsWith('5') || status.startsWith('0'))) {
          retriesCount++;
          return timer(1000);
        } else {
          // else throw error and continue with error handling
          throw err;
        }
      },
    }),
    catchError((error: HttpErrorResponse) => {
      const isMessageException: boolean = httpUtil.isHttpErrorMessageException(error);
      const errorMessages: string[] = extractErrorMessages(error);

      if (!isMessageException) {
        if (errorMessages.length > 0) {
          errorMessages.forEach((errorMessage: string) => {
            errorService.showErrorMessage(error, errorMessage);
          });
        } else {
          errorService.showErrorMessage(error);
        }

        if (error.status === 403) {
          router.navigate(['/']);
        }
      }
      return throwError(() => error);
    })
  );
}
