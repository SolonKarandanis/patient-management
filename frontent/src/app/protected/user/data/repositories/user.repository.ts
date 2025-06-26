import {Injectable} from '@angular/core';
import {BaseRepository} from '@core/repositories/BaseRepository';
import {SearchResult, UserSearchRequest} from '@models/search.model';
import {Observable} from 'rxjs';
import {ChangePasswordRequest, CreateUserRequest, UpdateUserRequest, User} from '@models/user.model';
import {ApiControllers} from '@core/repositories/ApiControllers';
import {HttpContext, HttpResponse} from '@angular/common/http';
import {AUTHENTICATE_REQUEST} from '@core/guards/SecurityConstants';

@Injectable({
  providedIn: 'root',
})
export class UserRepository extends BaseRepository{

  /**
   * Search for users
   * @param request The search criteria
   * @returns An observable with a list of users found
   */
  public searchUsers(request:UserSearchRequest):Observable<SearchResult<User>>{
    return this.http
      .post<SearchResult<User>>(`${ApiControllers.USERS}/search`,request);
  }

  /**
   * Search for users
   * @param request The search criteria
   * @returns An observable with the ArrayBuffer of the csv
   */
  public exportUsersToCsv(request:UserSearchRequest):Observable<HttpResponse<ArrayBuffer>>{
    return this.http.post(`${ApiControllers.USERS}/export/csv`, request, {
      responseType: 'arraybuffer',
      observe: 'response',
    });
  }

  /**
   * Get the details of a specific user
   * @param id the id of the user
   * @returns An Observable with the details of the user
   */
  public getUserById(id:string):Observable<User>{
    return this.http.get<User>(`${ApiControllers.USERS}/${id}`);
  }

  /**
   * Register a new user
   * @param request the request for creating a new user
   * @returns An Observable with the created user
   */
  public registerUser(request:CreateUserRequest):Observable<User>{
    return this.http.post<User>(`${ApiControllers.USERS}`,request,{
      context: new HttpContext().set(AUTHENTICATE_REQUEST, false),
    });
  }

  /**
   * Update a selected user
   * @param id the id of the user
   * @param request the request for updating user
   * @returns An Observable with the updated user
   */
  public updateUser(id:string,request:UpdateUserRequest):Observable<User>{
    return this.http.put<User>(`${ApiControllers.USERS}/${id}`,request);
  }

  /**
   * Update a selected user
   * @param id the id of the user
   * @param request the request for updating user
   * @returns An Observable with the updated user
   */
  public changeUserPassword(id:string,request:ChangePasswordRequest):Observable<User>{
    return this.http.put<User>(`${ApiControllers.USERS}/${id}/password-change`,request);
  }

  /**
   * Delete a  user
   * @param id the id of the user
   * @returns An observable that resolves to no data
   */
  public deleteUser(id:string):Observable<void>{
    return this.http.delete<void>(`${ApiControllers.USERS}/${id}`);
  }

  /**
   * Activate a  user
   * @param id the id of the user
   * @returns An observable with the activated user
   */
  public activateUser(id:string):Observable<User>{
    return this.http.put<User>(`${ApiControllers.USERS}/${id}/activate`,null);
  }

  /**
   * Deactivate a  user
   * @param id the id of the user
   * @returns An observable with the de-activated user
   */
  public deactivateUser(id:string):Observable<User>{
    return this.http.put<User>(`${ApiControllers.USERS}/${id}/deactivate`,null);
  }
}
