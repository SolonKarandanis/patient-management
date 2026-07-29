import {UserSearchStore} from '../store/user-search.store';
import {UserDetailStore} from '../store/user-detail.store';
import {UserService} from './user.service';
import {UserSearchMapperService} from './user-search-mapper.service';
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
import {of} from 'rxjs';

type UserSearchStore = InstanceType<typeof UserSearchStore>;
type UserDetailStore = InstanceType<typeof UserDetailStore>;

describe('UserService', () =>{
  let service: UserService;
  let userSearchStoreSpy: jasmine.SpyObj<UserSearchStore>;
  let userDetailStoreSpy: jasmine.SpyObj<UserDetailStore>;
  let searchServiceSpy: jasmine.SpyObj<UserSearchMapperService>;
  let translateSpy: jasmine.SpyObj<TranslateService>;
  let utilServiceSpy: jasmine.SpyObj<UtilService>;

  beforeEach(() => {
    userSearchStoreSpy = jasmine.createSpyObj('UserSearchStore',[
      'searchUsers',
      'exportUsersToCsv',
      'resetSearchResults',
      'loading',
      'searchResults',
      'totalCount',
      'criteriaCollapsed',
      'hasSearched',
      'tableLoading',
    ]);

    userDetailStoreSpy = jasmine.createSpyObj('UserDetailStore',[
      'loadUserById',
      'registerUser',
      'updateUser',
      'deleteUser',
      'activateUser',
      'deactivateUser',
      'changeUserPassword',
      'getUser',
      'getUserId',
      'getUserRolesAsSelectItems',
      'loading',
      'createdUserId',
      'setCreatedUserId'
    ]);

    searchServiceSpy= jasmine.createSpyObj('UserSearchMapperService',[
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
          provide: UserSearchStore,
          useValue: userSearchStoreSpy,
        },
        {
          provide: UserDetailStore,
          useValue: userDetailStoreSpy,
        },
        {
          provide: UserSearchMapperService,
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

  it('should load user by Id', (done) =>{
    const userId: string = '1';
    userDetailStoreSpy.loadUserById.and.returnValue(of(mockUser));

    service.loadUserById$(userId).subscribe((result) => {
      expect(result).toBe(mockUser);
      expect(userDetailStoreSpy.loadUserById).toHaveBeenCalledWith(userId);
      expect(userDetailStoreSpy.loadUserById).toHaveBeenCalledTimes(1);
      done();
    });
  });

  it('should execute register user ', () =>{
    searchServiceSpy.toCreateUserRequest.and.returnValue(mockCreateUserRequest);

    service.executeRegisterUser(mockCreateUserForm);

    expect(searchServiceSpy.toCreateUserRequest).toHaveBeenCalledWith(mockCreateUserForm);
    expect(searchServiceSpy.toCreateUserRequest).toHaveBeenCalledTimes(1);
    expect(userDetailStoreSpy.registerUser).toHaveBeenCalledWith(mockCreateUserRequest);
    expect(userDetailStoreSpy.registerUser).toHaveBeenCalledTimes(1);
  });

  it('should execute update user ', () =>{
    const userId: string = '1';
    service.userId=signal(userId);
    searchServiceSpy.toUpdateUserRequest.and.returnValue(mockUpdateUserRequest);

    service.executeUpdateUser(mockUpdateUserForm);

    expect(searchServiceSpy.toUpdateUserRequest).toHaveBeenCalledWith(mockUpdateUserForm);
    expect(searchServiceSpy.toUpdateUserRequest).toHaveBeenCalledTimes(1);
    expect(userDetailStoreSpy.updateUser).toHaveBeenCalledWith({id:userId,request:mockUpdateUserRequest});
    expect(userDetailStoreSpy.updateUser).toHaveBeenCalledTimes(1);
  });

  it('should change user password', () => {
    const userId: string = '1';
    service.userId=signal(userId);
    searchServiceSpy.toChangePasswordRequest.and.returnValue(mockChangePasswordRequest);

    service.executeChangeUserPassword(mockChangePasswordForm);

    expect(searchServiceSpy.toChangePasswordRequest).toHaveBeenCalledWith(mockChangePasswordForm);
    expect(searchServiceSpy.toChangePasswordRequest).toHaveBeenCalledTimes(1);
    expect(userDetailStoreSpy.changeUserPassword).toHaveBeenCalledWith({id:userId,request:mockChangePasswordRequest});
    expect(userDetailStoreSpy.changeUserPassword).toHaveBeenCalledTimes(1);
  });

  it('should execute delete user ', () =>{
    const userId: string = '1';
    service.userId=signal(userId);

    service.executeDeleteUser();

    expect(userDetailStoreSpy.deleteUser).toHaveBeenCalledWith(userId);
    expect(userDetailStoreSpy.deleteUser).toHaveBeenCalledTimes(1);
  });

  it('should execute activate user ', () =>{
    const userId: string = '1';
    service.userId=signal(userId);

    service.executeActivateUser();

    expect(userDetailStoreSpy.activateUser).toHaveBeenCalledWith(userId);
    expect(userDetailStoreSpy.activateUser).toHaveBeenCalledTimes(1);
  });

  it('should execute deactivate user ', () =>{
    const userId: string = '1';
    service.userId=signal(userId);

    service.executeDeactivateUser();

    expect(userDetailStoreSpy.deactivateUser).toHaveBeenCalledWith(userId);
    expect(userDetailStoreSpy.deactivateUser).toHaveBeenCalledTimes(1);
  });

  it('should execute search users ', () =>{
    searchServiceSpy.toUserSearchRequest.and.returnValue(mockUserSearchRequest);

    service.executeSearchUsers(mockSearchUserForm);

    expect(searchServiceSpy.toUserSearchRequest).toHaveBeenCalledWith(mockSearchUserForm);
    expect(searchServiceSpy.toUserSearchRequest).toHaveBeenCalledTimes(1);
    expect(userSearchStoreSpy.searchUsers).toHaveBeenCalledWith(mockUserSearchRequest);
    expect(userSearchStoreSpy.searchUsers).toHaveBeenCalledTimes(1);
  });

  it('should reset created user id ', () =>{
    service.resetCreatedUserId();

    expect(userDetailStoreSpy.setCreatedUserId).toHaveBeenCalledWith(null);
    expect(userDetailStoreSpy.setCreatedUserId).toHaveBeenCalledTimes(1);
  });

    it('should initialize a search users form', () => {

      const form = service.initSearchUserForm();

      const formValues = form().value();

      expect(form).toBeTruthy();
      expect(formValues).toBeTruthy();
      expect(formValues.email).toBeDefined();
      expect(formValues.email).toEqual('');
      expect(formValues.username).toBeDefined();
      expect(formValues.username).toEqual('');
      expect(formValues.name).toBeDefined();
      expect(formValues.name).toEqual('');
      expect(formValues.status).toBeDefined();
      expect(formValues.status).toEqual("account.active");
      expect(formValues.rows).toBeDefined();
      expect(formValues.rows).toEqual(10);
      expect(formValues.first).toBeDefined();
      expect(formValues.first).toEqual(0);
      expect(form().valid).toBeTrue();
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
