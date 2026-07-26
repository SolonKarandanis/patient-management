import {Language} from '@models/i18n-resource.model';

export type I18nLookupState = {
  readonly languages: Language[];
  readonly modules: Record<number,string> | null;
}

export const initialI18nLookupState: I18nLookupState = {
  languages: [],
  modules: null,
};
