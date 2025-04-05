import {Injectable} from '@angular/core';
import {BaseRepository} from './BaseRepository';
import {Observable} from 'rxjs';
import {HttpResponse} from '@angular/common/http';
import {ApiControllers} from './ApiControllers';

@Injectable({
  providedIn: 'root'
})
export class FilesRepository extends BaseRepository{

  /**
   * Get the data of a file from the backend for downloading
   * @param fileid The file's ID
   * @returns An observable with the file's data in ArrayBuffer
   */


  downloadFile(fileid: number): Observable<HttpResponse<ArrayBuffer>> {
    return this.http.get(`${ApiControllers.FILES}/${fileid}`, {
      responseType: 'arraybuffer',
      observe: 'response',
    });
  }

  // downloadFile(fileid: number): Observable<GenericFile> {
  //     return this.filesController.downloadFile(fileid).pipe(
  //         map((responseData: HttpResponse<ArrayBuffer>) => ({
  //             filename: this.httpUtil.getFileNameForContentDisposition(responseData.headers),
  //             id: fileid,
  //             mimeType: responseData.headers.get('Content-Type')!,
  //             arrayBuffer: responseData.body!,
  //         }))
  //     );
  // }

}
