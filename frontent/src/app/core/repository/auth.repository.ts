import {Injectable} from '@angular/core';
import {BaseRepository} from './BaseRepository';
import {JwtDTO, SubmitCredentialsDTO} from '../models/auth.model';
import { Observable } from 'rxjs';
import {ApiControllers} from './ApiControllers';
import {AUTHENTICATE_REQUEST} from '../guards/SecurityConstants';
import { HttpContext } from '@angular/common/http';
import {User} from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class AuthRepository extends BaseRepository{

  /**
   * Submit credentials for login
   * @param credentials the credentials submitted
   * @returns An observable with the JwtDTO
   */
  login(credentials:SubmitCredentialsDTO):Observable<JwtDTO>{
    return this.http.post<JwtDTO>(`${ApiControllers.AUTH}/login`,credentials,{
      context: new HttpContext().set(AUTHENTICATE_REQUEST, false),
    })
  }

  /**
   * Requests the logged in users info
   * @returns An observable with the UserModel
   */
  getUserByToken(): Observable<User> {
    return this.http.get<User>(`${ApiControllers.USERS}/account`);
  }

  // getUserOperations(): Observable<OperationModel[]> {
  //     return this.http.get<Operations[]>(`${this.dutEndpoint}/${ApiControllers.USERS}/operations`)
  //         .pipe(
  //             map(response =>{
  //                 const models =response.map(this.operationMapper.mapToModel)
  //                 return models;
  //             })
  //         )
  // }
}
