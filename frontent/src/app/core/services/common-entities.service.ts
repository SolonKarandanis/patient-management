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
  public isManagementOfI18nResourcesEnabled = this.commonEntitiesStore.isManagementOfI18nResourcesEnabled;
  public isWebSocketsEnabled = this.commonEntitiesStore.isWebSocketsEnabled;

  /**
   * Initialize All Common Entities
   * @returns nothing
   */
  public initializeCommonEntities():void{
    this.commonEntitiesStore.initializeCommonEntities();
  }

  public executeGetPublicApplicationConfig():void{
    this.commonEntitiesStore.getPublicApplicationConfig();
  }

  getBigDecimalScale(): string {
    // const bigDecimalPlaces = this.applicationConfig ? this.applicationConfig.BIG_DECIMAL_SCALE : this.publicApplicationConfig.BIG_DECIMAL_SCALE;
    // return `1.${bigDecimalPlaces}-${bigDecimalPlaces}`;
    return `1.`;
  }
}
