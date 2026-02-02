import {Injectable} from '@angular/core';
import {BaseRepository} from '@core/repositories/BaseRepository';
import {Observable} from 'rxjs';
import {Role} from '@models/user.model';
import {ApiRepositories} from '@core/repositories/ApiRepositories';
import {HttpContext} from '@angular/common/http';
import {AUTHENTICATE_REQUEST} from '@core/guards/SecurityConstants';
import {ApplicationConfig} from '@models/application-config.model';

@Injectable({
  providedIn: 'root',
})
export class CommonEntitiesRepository extends BaseRepository{

  /**
   * Get all Roles
   * @returns An observable with a list of all roles found
   */
  public getAllRoles():Observable<Role[]> {
    return this.http.get<Role[]>(`${ApiRepositories.AUTH}/${ApiRepositories.ROLES}`,{
      context: new HttpContext().set(AUTHENTICATE_REQUEST, false),
    });
  }

  public getApplicationConfig():Observable<ApplicationConfig>{
    return this.http.get<ApplicationConfig>(`${ApiRepositories.AUTH}/${ApiRepositories.COMMON}/config`)
  }

  public getPublicApplicationConfig():Observable<ApplicationConfig>{
    return this.http.get<ApplicationConfig>(`${ApiRepositories.AUTH}/${ApiRepositories.PUBLIC}/config`,{
      context: new HttpContext().set(AUTHENTICATE_REQUEST, false),
    })
  }
}
