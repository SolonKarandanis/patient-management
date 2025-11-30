export interface I18nResource {
  id: number;
  key: string;
  mod: string;
  translations: { lang: string; value: string }[];
  _translations?: { lang: string; value: string }[];
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
