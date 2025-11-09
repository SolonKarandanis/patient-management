import {Inject, Injectable} from '@angular/core';
import {HttpErrorResponse, HttpHeaders, HttpStatusCode} from '@angular/common/http';
import {API_BASE_URL} from '../token';
import {ApiRepositories} from '@core/repositories/ApiRepositories';

@Injectable({
  providedIn: 'root',
})
export class HttpUtil{

  constructor(@Inject(API_BASE_URL) private readonly baseUrl:string) {}

  private readonly HttpErrorMessageExceptions: HttpErrorMessageException[] = [
    {
      urlRegExp: ApiRepositories.USERS,
      status: HttpStatusCode.Ok,
    },
    {
      urlRegExp: ApiRepositories.SEARCH,
      status: HttpStatusCode.NotFound,
    },
  ];

  isHttpErrorMessageException(errorResponse: HttpErrorResponse): boolean {
    if (!errorResponse || !errorResponse.url) {
      return false;
    }

    const urlSuffix: string = errorResponse.url.replace(`${this.baseUrl}/`, '');
    let check = false;
    let i = 0;

    while (!check && i < this.HttpErrorMessageExceptions.length) {
      const messageEx: HttpErrorMessageException = this.HttpErrorMessageExceptions[i];
      const statusMatch: boolean = messageEx.status === errorResponse.status;
      if (statusMatch) {
        const regexp = new RegExp(messageEx.urlRegExp);
        check = regexp.test(urlSuffix);
      }
      i++;
    }

    return check;
  }

  /**
   * Get a files name from the Content-Disposition response header
   * @param headers The headers of the response
   * @returns The file's name if found, otherwise an empty string
   */
  getFileNameForContentDisposition(headers: HttpHeaders): string {
    const header: string | null = headers.get('Content-Disposition');

    if (header) {
      const hasFilename: boolean = header.includes('filename');
      return hasFilename ? header.split('"')[1] : '';
    } else {
      return '';
    }
  }
}

export interface HttpErrorMessageException {
  urlRegExp: string;
  status: HttpStatusCode;
}
