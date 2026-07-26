import {Role} from '@models/user.model';
import {ApplicationConfig} from '@models/application-config.model';


export interface CommonEntitiesLookupState{
  readonly roles:Role[] | undefined;
  readonly appConfig: ApplicationConfig | undefined
}

export const initialCommonEntitiesLookupState: CommonEntitiesLookupState = {
  roles: undefined,
  appConfig: undefined
}
