export const ApplicationLanguageStorageKey: string = "appLang";
export const DefaultApplicationLanguage:string = "en";

export const UserRolesEnum = {
  ACTIVE: "account.active",
  INACTIVE: "account.inactive",
  DELETED: "account.deleted",
} as const satisfies Record<string, string>;
