export interface I18nResource {
  id: number;
  key: string;
  mod: string;
  translations: Record<number,string>;
  translationList:Translation[];
  _translationList?: Translation[];
  editing: boolean;
}

export interface UpdateI18nResource {
  resourceId: number;
  textValue: string;
  languageId: number;
}

export interface I18nResourceResponse {
  countRows: number;
  list: I18nResource[];
}

export interface Language{
  id: number;
  isoCode: string;
  label: string;
}

export interface Translation {
  lang:number;
  value:string;
}
