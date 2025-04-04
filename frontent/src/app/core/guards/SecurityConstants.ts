import { HttpContextToken } from "@angular/common/http";

export enum RolesConstants{
  ROLE_ADMIN = "user.role.sa",
  ROLE_DOCTOR = "user.role.doctor",
  ROLE_PATIENT = "user.role.patient",
  ROLE_NO_ROLE = "user.no.role",
}

export const userRoles =[
  RolesConstants.ROLE_ADMIN,
  RolesConstants.ROLE_DOCTOR,
  RolesConstants.ROLE_PATIENT,
  RolesConstants.ROLE_NO_ROLE,
];

export const AUTHENTICATE_REQUEST = new HttpContextToken(() => true);
