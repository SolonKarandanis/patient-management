import {Role} from '@models/user.model';
import {ApplicationConfig} from '@models/application-config.model';


export interface CommonEntitiesState{
  readonly roles:Role[] | undefined;
  readonly appConfig: ApplicationConfig | undefined
}

export const initialCommonEntitiesState: CommonEntitiesState = {
  roles: undefined,
  appConfig: undefined
}
