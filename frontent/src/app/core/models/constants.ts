export const ApplicationLanguageStorageKey: string = "appLang";
export const DefaultApplicationLanguage:string = "en";

export const UserRolesEnum = {
  ROLE_SYSTEM_ADMIN: "user.role.sa",
  ROLE_DOCTOR: "user.role.doctor",
  ROLE_PATIENT: "user.role.patient",
  ROLE_GUEST: "user.no.role",
} as const satisfies Record<string, string>;

export type UserRoles = (typeof UserRolesEnum)[keyof typeof UserRolesEnum];
