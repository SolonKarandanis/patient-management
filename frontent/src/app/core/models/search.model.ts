import {UserAccountStatus} from '@models/user.model';

export type SearchResult<T>={
  list:T[],
  countRows:number
}

export interface SearchTableColumn {
  title: string;
  field: string;
  isLink?: boolean;
  routerLinkConfig?: SearchTableColumnLinkRouterConf;
  dataFieldForRoute?: string;
  isCheckbox?: boolean;
  isButton?: boolean;
  icon?: string;
  style?:string;
  enableSorting?: boolean;
  isDate?: boolean;
  isImage?: boolean;
  isTranslatable?:boolean;
  onlyIcon?:boolean;
  isStatus?:boolean;
  isTableActions?:boolean;
  toolTip?:string;
  dataForToolTip?:string;
  actions?:SearchTableColumnAction[];
}

export interface SearchTableColumnAction{
  type:ActionTypes;
  toolTip:string;
  dataForToolTip?:string;
  routerLinkConfig?: SearchTableColumnLinkRouterConf;
  dataFieldForRoute?: string;
  isLink?: boolean;
  isButton?:boolean;
  callbackFn?:(args?: any) => void;
}

const ActionTypesEnum = {
  VIEW: "VIEW",
  EDIT: "EDIT",
  DELETE: "DELETE",
} as const satisfies Record<string, string>;

export type ActionTypes = (typeof ActionTypesEnum)[keyof typeof ActionTypesEnum];


export interface SearchTableColumnLinkRouterConf {
  preRoutes?: string[];
  postRoutes?: string[];
}

const SortDirectionEnum = {
  ASC: "ASC",
  DESC: "DESC",
  DELETE: "DELETE",
} as const satisfies Record<string, string>;

export type SortDirection = (typeof SortDirectionEnum)[keyof typeof SortDirectionEnum];

export interface Paging {
  page: number;
  limit: number;
  sortField?: string;
  sortDirection?: SortDirection;
}

export interface SearchRequest {
  paging: Paging;
}

export interface UserSearchRequest extends SearchRequest {
  username: string|null|undefined;
  name: string|null|undefined;
  email: string|null|undefined;
  status: UserAccountStatus;
  roleName: string;
}

export type SearchRequestCriteria = UserSearchRequest


export interface SavedSearch {
  id?: number;
  userId: string;
  searchType: SearchType;
  savedSearchName: string;
  criteria: SearchRequestCriteria;
}

export interface SearchConfigurationCriteria {
  fieldName: string;
  searchable: boolean;
  displayable: boolean;
  customizable: boolean;
  sortable?: boolean;
  canFieldBeSearched: boolean;
  canFieldBeDisplayed: boolean;
}

export interface SearchConfiguration {
  id?: number;
  userId: string;
  searchType: SearchType;
  resultCount: number;
  sortColumnKey?: string;
  sortDirection?: SortDirection;
  criteria: SearchConfigurationCriteria[];
}

const SearchTypeEnum = {
  USERS: "search.type.users",
  PATIENTS: "search.type.patients",
} as const satisfies Record<string, string>;

export type SearchType = (typeof SearchTypeEnum)[keyof typeof SearchTypeEnum];


