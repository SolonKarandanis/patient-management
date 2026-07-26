import {inject, Injectable} from '@angular/core';
import {GenericService} from '@core/services/generic.service';
import {CommonEntitiesLookupStore} from '@core/store/common-entities/common-entities-lookup.store';

@Injectable({
  providedIn: 'root'
})
export class CommonEntitiesService extends GenericService{
  private commonEntitiesLookupStore = inject(CommonEntitiesLookupStore);

  public roles = this.commonEntitiesLookupStore.roles;
  public rolesAsSelectItems = this.commonEntitiesLookupStore.getRolesAsSelectItems;
  public isManagementOfI18nResourcesEnabled = this.commonEntitiesLookupStore.isManagementOfI18nResourcesEnabled;
  public isWebSocketsEnabled = this.commonEntitiesLookupStore.isWebSocketsEnabled;
  public authMode = this.commonEntitiesLookupStore.authMode;

  /**
   * Initialize All Common Entities
   * @returns nothing
   */
  public initializeCommonEntities():void{
    this.commonEntitiesLookupStore.initializeCommonEntities();
  }

  public initializePublicApplicationConfig():void{
    this.commonEntitiesLookupStore.initializePublicApplicationConfig();
  }

  getBigDecimalScale(): string {
    // const bigDecimalPlaces = this.applicationConfig ? this.applicationConfig.BIG_DECIMAL_SCALE : this.publicApplicationConfig.BIG_DECIMAL_SCALE;
    // return `1.${bigDecimalPlaces}-${bigDecimalPlaces}`;
    return `1.`;
  }
}
