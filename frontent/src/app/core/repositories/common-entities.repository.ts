import {Injectable} from '@angular/core';
import {BaseRepository} from '@core/repositories/BaseRepository';
import {Observable} from 'rxjs';
import {Role} from '@models/user.model';
import {ApiControllers} from '@core/repositories/ApiControllers';

@Injectable({
  providedIn: 'root',
})
export class CommonEntitiesRepository extends BaseRepository{

  /**
   * Get all Roles
   * @returns An observable with a list of all roles found
   */
  public getAllRoles():Observable<Role[]> {
    return this.http.get<Role[]>(`${ApiControllers.ROLES}`);
  }
}
