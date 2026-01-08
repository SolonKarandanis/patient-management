import { HttpHeaders, HttpResponse } from '@angular/common/http';

import {
  ChangePasswordRequest,
  CreateUserRequest,
  Operation,
  Role,
  UpdateUserRequest,
  User,
} from '@models/user.model';
import {RolesConstants} from '@core/guards/SecurityConstants';
import {
  Paging,
  SavedSearch,
  SearchConfiguration, SortDirectionEnum,
  UserSearchRequest
} from '@models/search.model';
import {FormControl, FormGroup} from '@angular/forms';
import {
  ChangePasswordForm,
  CreateUserFormModel,
  createUserFormSchema,
  UpdateUserForm,
  UserSearchForm, UserSearchFormModel
} from '../src/app/protected/user/forms';
import {JwtDTO, SubmitCredentialsDTO} from '@models/auth.model';
import {GenericFile} from '@models/file.model';
import {FieldTree, form} from '@angular/forms/signals';
import {signal} from '@angular/core';


export const mockRole:Role={
  id:1,
  name:RolesConstants.ROLE_ADMIN
}


export const mockOperation:Operation={
  id:1,
  name:RolesConstants.ROLE_ADMIN
}

export const mockUser: User ={
  username:"skaran",
  status:"account.active",
  publicId:"test",
  email:"skarandanis@email.com",
  firstName:"solon",
  lastName:"karandanis",
  operations:[mockOperation],
  roles:[mockRole],
  statusLabel:"Active",
  createdDate:"",
  lastModifiedDate:"",
  isEnabled:true,
  isVerified:true,
}

export const mockBlob: Blob = new Blob([JSON.stringify({})], {
  type: 'application/json',
});

export const mockArrayBuffer: ArrayBuffer = new ArrayBuffer(16);

export const mockHeaders = new HttpHeaders().append('Content-Type', 'application/octet-stream');

export const mockArrayBufferResponse = new HttpResponse({ body: mockArrayBuffer, headers: mockHeaders });


export const mockPaging: Paging = {
  page: 1,
  limit: 10,
};

export const mockUserSearchRequest:UserSearchRequest={
  paging: {
    page: 1,
    limit: 10,
    sortField: 'name',
    sortDirection: "ASC",
  },
  username: 'org123',
  name: 'John',
  status:"account.active",
  email:'skarandanis@gmail.com',
  roleName:mockRole.name
}

export const mockSearchUserForm: FieldTree<UserSearchFormModel, string | number> = form<UserSearchFormModel>(signal({
  email: '',
  first: 0,
  rows: 10,
  name: '',
  status:"account.active",
  role: null,
  username: '',
  sortOrder: SortDirectionEnum.ASC,
  sortField: 'id'
}));

export const mockSavedSearch: SavedSearch = {
  id: 123,
  userId: '456',
  searchType: "search.type.users",
  savedSearchName: 'My Saved Item Search',
  criteria: {
    paging: {
      page: 1,
      limit: 10,
      sortField: 'name',
      sortDirection: "ASC",
    },
    username: 'org123',
    name: 'John',
    status:"account.active",
    email:'skarandanis@gmail.com',
    roleName:mockRole.name
  },
};

export const mockSearchConfiguration: SearchConfiguration = {
  id: 789,
  userId: '456',
  searchType: "search.type.users",
  resultCount: 50,
  sortColumnKey: 'name',
  sortDirection: "ASC",
  criteria: [
    {
      canFieldBeDisplayed: true,
      canFieldBeSearched: true,
      customizable: true,
      displayable: true,
      fieldName: 'test',
      searchable: true,
    },
  ],
};

export const mockLoginCredentials: SubmitCredentialsDTO = {
  password: 'test',
  email: 'test',
};

export const mockJwt: JwtDTO = {
  token: 'test',
  expires: 'test',
};

export const mockFile = new File([], 'test');

export const mockGenericFile: GenericFile = {
  filename: 'filename',
  id: 0,
  arrayBuffer: mockArrayBuffer,
  mimeType: 'application/octet-stream',
};

export const mockUserSearchConfiguration: SearchConfiguration = {
  id: 1,
  userId: '5',
  sortDirection: "ASC",
  sortColumnKey: 'test',
  resultCount: 10,
  searchType: "search.type.users",
  criteria: [],
}

export const mockCreateUserRequest:CreateUserRequest={
  email:'test',
  firstName:'test',
  lastName:'test',
  password:'test',
  confirmPassword:'test',
  role:RolesConstants.ROLE_PATIENT,
  username:'test'
};

export const mockCreateUserForm: FieldTree<CreateUserFormModel, string | number> = form<CreateUserFormModel>(signal({
  email: 'test@test.com',
  firstName: 'test',
  username: 'test',
  lastName: 'test',
  role: RolesConstants.ROLE_PATIENT,
  password: 'password',
  confirmPassword: 'password',
}),createUserFormSchema);



export const mockUpdateUserRequest:UpdateUserRequest={
  email:'test',
  firstName:'test',
  lastName:'test',
  role:RolesConstants.ROLE_DOCTOR,
  username:'test'
};

export const mockUpdateUserForm:FormGroup= new FormGroup<UpdateUserForm>({
  email: new FormControl(''),
  firstName: new FormControl(''),
  username: new FormControl(''),
  lastName: new FormControl(''),
  role:new FormControl(RolesConstants.ROLE_DOCTOR,{nonNullable: true}),
});

export const mockChangePasswordRequest:ChangePasswordRequest={
  password:'7ujm&UJM',
  confirmPassword:'7ujm&UJM'
};

export const mockChangePasswordForm:FormGroup = new FormGroup<ChangePasswordForm>({
  password:new FormControl('7ujm&UJM'),
  confirmPassword:new FormControl('7ujm&UJM')
});
