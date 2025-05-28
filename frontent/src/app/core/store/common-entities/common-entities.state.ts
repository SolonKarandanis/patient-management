import {Role} from '@models/user.model';


export interface CommonEntitiesState{
  readonly roles:Role[] | undefined;
}

export const initialCommonEntitiesState: CommonEntitiesState = {
  roles: undefined,
}
