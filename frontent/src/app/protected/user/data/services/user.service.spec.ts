import {UserStore} from '../store/user.store';
import {UserService} from './user.service';
import {SearchService} from '@core/services/search.service';
import {TranslateService} from '@ngx-translate/core';
import {UtilService} from '@core/services/util.service';
import {TestBed} from '@angular/core/testing';
import {
  mockChangePasswordForm,
  mockChangePasswordRequest,
  mockCreateUserForm,
  mockCreateUserRequest, mockSearchUserForm,
  mockUpdateUserForm,
  mockUpdateUserRequest, mockUser,
  mockUserSearchRequest
} from '@testing/mockData';
import {signal} from '@angular/core';
import {RolesConstants} from '@core/guards/SecurityConstants';
import {SearchTableColumn} from '@models/search.model';

type UserStore = InstanceType<typeof UserStore>;

describe('UserService', () =>{
  let service: UserService;
  let userStoreSpy: jasmine.SpyObj<UserStore>;
  let searchServiceSpy: jasmine.SpyObj<SearchService>;
  let translateSpy: jasmine.SpyObj<TranslateService>;
  let utilServiceSpy: jasmine.SpyObj<UtilService>;

  beforeEach(() => {
    userStoreSpy = jasmine.createSpyObj('UserStore',[
      'getUserById',
      'registerUser',
      'updateUser',
      'deleteUser',
      'activateUser',
      'deactivateUser',
      'searchUsers',
      'changeUserPassword',
      'getUser',
      'getUserId',
      'loading',
      'searchResults',
      'totalCount',
      'createdUserId',
      'setCreatedUserId'
    ]);

    searchServiceSpy= jasmine.createSpyObj('SearchService',[
      'toUpdateUserRequest',
      'toUserSearchRequest',
      'toCreateUserRequest',
      'toChangePasswordRequest'
    ]);

    utilServiceSpy = jasmine.createSpyObj('UtilService', [], {
      strongPasswordRegex: '^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*"\'()+,-./:;<=>?[\\]^_`{|}~])(?=.{10,})',
    });

    translateSpy = jasmine.createSpyObj('TranslateService', ['instant']);


    TestBed.configureTestingModule({
      providers:[
        {
          provide: UserStore,
          useValue: userStoreSpy,
        },
        {
          provide: SearchService,
          useValue: searchServiceSpy,
        },
        {
          provide: TranslateService,
          useValue: translateSpy,
        },
        {
          provide: UtilService,
          useValue: utilServiceSpy,
        },
      ]
    });
    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute get user by Id', () =>{
    const userId: string = '1';
    service.executeGetUserById(userId);

    expect(userStoreSpy.getUserById).toHaveBeenCalledWith(userId);
    expect(userStoreSpy.getUserById).toHaveBeenCalledTimes(1);
  });

  it('should execute register user ', () =>{
    searchServiceSpy.toCreateUserRequest.and.returnValue(mockCreateUserRequest);

    service.executeRegisterUser(mockCreateUserForm);

    expect(searchServiceSpy.toCreateUserRequest).toHaveBeenCalledWith(mockCreateUserForm);
    expect(searchServiceSpy.toCreateUserRequest).toHaveBeenCalledTimes(1);
    expect(userStoreSpy.registerUser).toHaveBeenCalledWith(mockCreateUserRequest);
    expect(userStoreSpy.registerUser).toHaveBeenCalledTimes(1);
  });

  it('should execute update user ', () =>{
    const userId: string = '1';
    service.userId=signal(userId);
    searchServiceSpy.toUpdateUserRequest.and.returnValue(mockUpdateUserRequest);

    service.executeUpdateUser(mockUpdateUserForm);

    expect(searchServiceSpy.toUpdateUserRequest).toHaveBeenCalledWith(mockUpdateUserForm);
    expect(searchServiceSpy.toUpdateUserRequest).toHaveBeenCalledTimes(1);
    expect(userStoreSpy.updateUser).toHaveBeenCalledWith({id:userId,request:mockUpdateUserRequest});
    expect(userStoreSpy.updateUser).toHaveBeenCalledTimes(1);
  });

  it('should change user password', () => {
    const userId: string = '1';
    service.userId=signal(userId);
    searchServiceSpy.toChangePasswordRequest.and.returnValue(mockChangePasswordRequest);

    service.executeChangeUserPassword(mockChangePasswordForm);

    expect(searchServiceSpy.toChangePasswordRequest).toHaveBeenCalledWith(mockChangePasswordForm);
    expect(searchServiceSpy.toChangePasswordRequest).toHaveBeenCalledTimes(1);
    expect(userStoreSpy.changeUserPassword).toHaveBeenCalledWith({id:userId,request:mockChangePasswordRequest});
    expect(userStoreSpy.changeUserPassword).toHaveBeenCalledTimes(1);
  });

  it('should execute delete user ', () =>{
    const userId: string = '1';
    service.userId=signal(userId);

    service.executeDeleteUser();

    expect(userStoreSpy.deleteUser).toHaveBeenCalledWith(userId);
    expect(userStoreSpy.deleteUser).toHaveBeenCalledTimes(1);
  });

  it('should execute activate user ', () =>{
    const userId: string = '1';
    service.userId=signal(userId);

    service.executeActivateUser();

    expect(userStoreSpy.activateUser).toHaveBeenCalledWith(userId);
    expect(userStoreSpy.activateUser).toHaveBeenCalledTimes(1);
  });

  it('should execute deactivate user ', () =>{
    const userId: string = '1';
    service.userId=signal(userId);

    service.executeDeactivateUser();

    expect(userStoreSpy.deactivateUser).toHaveBeenCalledWith(userId);
    expect(userStoreSpy.deactivateUser).toHaveBeenCalledTimes(1);
  });

  it('should execute search users ', () =>{
    searchServiceSpy.toUserSearchRequest.and.returnValue(mockUserSearchRequest);

    service.executeSearchUsers(mockSearchUserForm);

    expect(searchServiceSpy.toUserSearchRequest).toHaveBeenCalledWith(mockSearchUserForm);
    expect(searchServiceSpy.toUserSearchRequest).toHaveBeenCalledTimes(1);
    expect(userStoreSpy.searchUsers).toHaveBeenCalledWith(mockUserSearchRequest);
    expect(userStoreSpy.searchUsers).toHaveBeenCalledTimes(1);
  });

  it('should reset created user id ', () =>{
    service.resetCreatedUserId();

    expect(userStoreSpy.setCreatedUserId).toHaveBeenCalledWith(null);
    expect(userStoreSpy.setCreatedUserId).toHaveBeenCalledTimes(1);
  });

    it('should initialize a search users form', () => {

      const form = service.initSearchUserForm();

      const formValues = form.value();

      expect(form).toBeTruthy();
      expect(formValues).toBeTruthy();
      expect(formValues.email).toBeDefined();
      expect(formValues.email).toEqual(null);
      expect(formValues.username).toBeDefined();
      expect(formValues.username).toEqual(null);
      expect(formValues.name).toBeDefined();
      expect(formValues.name).toEqual(null);
      expect(formValues.status).toBeDefined();
      expect(formValues.status).toEqual("account.active");
      expect(formValues.rows).toBeDefined();
      expect(formValues.rows).toEqual(10);
      expect(formValues.first).toBeDefined();
      expect(formValues.first).toEqual(0);
      expect(form.valid()).toBeTrue();
    });

  it('should initialize an update users form', () => {
    service.user = signal(mockUser);
    const form = service.initUpdateUserForm();
    const formValues = form.value();

    expect(form).toBeTruthy();
    expect(formValues).toBeTruthy();

    expect(formValues.email).toBeDefined();
    expect(formValues.email).toBeNull();

    expect(formValues.username).toBeDefined();
    expect(formValues.username).toBeNull();

    expect(formValues.firstName).toBeDefined();
    expect(formValues.firstName).toBeNull();

    expect(formValues.lastName).toBeDefined();
    expect(formValues.lastName).toBeNull();

    expect(formValues.role).toBeDefined();
    expect(formValues.role).toEqual(RolesConstants.ROLE_NO_ROLE);

    expect(form.valid()).toBeTrue();
  });

  it('should initialize a change password form', () => {
    const form = service.initChangePasswordForm();
    const formValues = form.value();

    expect(form).toBeTruthy();
    expect(formValues).toBeTruthy();

    expect(formValues.password).toBeDefined();
    expect(formValues.password).toBeNull();

    expect(formValues.confirmPassword).toBeDefined();
    expect(formValues.confirmPassword).toBeNull();

    expect(form.valid()).toBeFalse();
  });

  it('should get Users Search Table Columns', () =>{
    const translationPrefix: string = 'USER.SEARCH-USERS.RESULTS-TABLE.COLS';
    const expectedFields: string[] = ['username', 'firstName', 'lastName', 'email','statusLabel'];

    const expectedTitles: string[] = [
      `${translationPrefix}.username`,
      `${translationPrefix}.firstName`,
      `${translationPrefix}.lastName`,
      `${translationPrefix}.email`,
      `${translationPrefix}.status`,
    ];

    const cols: SearchTableColumn[] = service.getSearchUserTableColumns();

    expect(cols.length).toBe(5);
    cols.forEach((col: SearchTableColumn) => {
      expect(expectedFields.includes(col.field!));
      expect(expectedTitles.includes(col.title!));
      if (col.field === 'username') {
        expect(col.isLink).toBeTrue();
      }
      else{
        expect(col.enableSorting).toBeTrue();
      }
    });
  });
});
