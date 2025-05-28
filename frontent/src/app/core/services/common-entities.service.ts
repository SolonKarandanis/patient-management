import {inject, Injectable} from '@angular/core';
import {GenericService} from '@core/services/generic.service';
import {CommonEntitiesStore} from '@core/store/common-entities/common-entities.store';

@Injectable({
  providedIn: 'root'
})
export class CommonEntitiesService extends GenericService{
  private commonEntitiesStore = inject(CommonEntitiesStore);

  public roles = this.commonEntitiesStore.roles;
  public rolesAsSelectItems = this.commonEntitiesStore.getRolesAsSelectItems;

  /**
   * Initialize All Common Entities
   * @returns nothing
   */
  public initializeCommonEntities():void{
    this.commonEntitiesStore.initializeCommonEntities();
  }
}
