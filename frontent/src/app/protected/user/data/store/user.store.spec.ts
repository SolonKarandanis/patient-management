import {UserStore} from './user.store';
import {UserRepository} from '../repositories/user.repository';
import {UtilService} from '@core/services/util.service';
import {HttpUtil} from '@core/services/http-util.service';
import {SearchResult} from '@models/search.model';
import {TestBed} from '@angular/core/testing';
import {mockCreateUserRequest, mockUpdateUserRequest, mockUser, mockUserSearchRequest} from '@testing/mockData';
import {of} from 'rxjs';

type UserStore = InstanceType<typeof UserStore>;


describe('UserStore', () =>{
  let store: UserStore;
  let userRepoSpy: jasmine.SpyObj<UserRepository>;
  let utilServiceSpy: jasmine.SpyObj<UtilService>;
  let httpUtilSpy: jasmine.SpyObj<HttpUtil>;
  let searchResult: SearchResult<any>;

  beforeEach(()=>{
    userRepoSpy = jasmine.createSpyObj('UserRepository',[
      'searchUsers',
      'getUserById',
      'registerUser',
      'updateUser',
      'deleteUser',
      'activateUser',
      'deactivateUser',
      'exportUsersToCsv'
    ]);
    utilServiceSpy = jasmine.createSpyObj('UtilService',[
      'showMessage',
    ]);

    httpUtilSpy = jasmine.createSpyObj('HttpUtil',[
      'getFileNameForContentDisposition',
    ]);

    TestBed.configureTestingModule({
      providers:[
        {
          provide: UserRepository,
          useValue: userRepoSpy,
        },
        {
          provide: UtilService,
          useValue: utilServiceSpy,
        },
        {
          provide: HttpUtil,
          useValue: httpUtilSpy,
        },
      ]
    });

    store = TestBed.inject(UserStore);

    searchResult = {
      countRows: 1,
      list: [],
    };
  });

  it('should be created', () => {
    expect(store).toBeTruthy();
  });

  it('should search users ', () =>{
    searchResult.list = [mockUser];
    userRepoSpy.searchUsers.and.returnValue(of(searchResult));

    store.searchUsers(mockUserSearchRequest);

    expect(userRepoSpy.searchUsers).toHaveBeenCalledWith(mockUserSearchRequest);
    expect(userRepoSpy.searchUsers).toHaveBeenCalledTimes(1);
  });

  it('should export users to csv ', () =>{
    userRepoSpy.exportUsersToCsv.and.returnValue(of());

    store.exportUsersToCsv(mockUserSearchRequest);

    expect(userRepoSpy.exportUsersToCsv).toHaveBeenCalledWith(mockUserSearchRequest);
    expect(userRepoSpy.exportUsersToCsv).toHaveBeenCalledTimes(1);
  });

  it('should get user by id ', () =>{
    const userId: string = '1';
    userRepoSpy.getUserById.and.returnValue(of(mockUser));

    store.getUserById(userId);

    expect(userRepoSpy.getUserById).toHaveBeenCalledWith(userId);
    expect(userRepoSpy.getUserById).toHaveBeenCalledTimes(1);
  });

  it('should register user ', () =>{
    userRepoSpy.registerUser.and.returnValue(of(mockUser));

    store.registerUser(mockCreateUserRequest);

    expect(userRepoSpy.registerUser).toHaveBeenCalledWith(mockCreateUserRequest);
    expect(userRepoSpy.registerUser).toHaveBeenCalledTimes(1);
  });

  it('should update user ', () =>{
    const userId: string = '1';
    userRepoSpy.updateUser.and.returnValue(of(mockUser));

    store.updateUser({id:userId,request:mockUpdateUserRequest});

    expect(userRepoSpy.updateUser).toHaveBeenCalledWith(userId,mockUpdateUserRequest);
    expect(userRepoSpy.updateUser).toHaveBeenCalledTimes(1);
  });

  it('should delete user ', () =>{
    const userId: string = '1';
    userRepoSpy.deleteUser.and.returnValue(of());

    store.deleteUser(userId);

    expect(userRepoSpy.deleteUser).toHaveBeenCalledWith(userId);
    expect(userRepoSpy.deleteUser).toHaveBeenCalledTimes(1);
  });

  it('should activate user ', () =>{
    const userId: string = '1';
    userRepoSpy.activateUser.and.returnValue(of(mockUser));

    store.activateUser(userId);

    expect(userRepoSpy.activateUser).toHaveBeenCalledWith(userId);
    expect(userRepoSpy.activateUser).toHaveBeenCalledTimes(1);
  });

  it('should deactivate user ', () =>{
    const userId: string = '1';
    userRepoSpy.deactivateUser.and.returnValue(of(mockUser));

    store.deactivateUser(userId);

    expect(userRepoSpy.deactivateUser).toHaveBeenCalledWith(userId);
    expect(userRepoSpy.deactivateUser).toHaveBeenCalledTimes(1);
  });

  it('should verify that it should return computed user', () =>{
    store.setSelectedUser(mockUser);

    expect(store.getUser()).toBe(mockUser);
  });

  it('should verify that it should return computed user id', () =>{
    store.setSelectedUser(mockUser);

    expect(store.getUserId()).toBe(mockUser.publicId);
  });

  it('should verify that it should return computed username', () =>{
    store.setSelectedUser(mockUser);

    expect(store.getUsername()).toBe(mockUser.username);
  });

  it('should set search results ', () =>{
    searchResult.list = [mockUser];
    store.setSearchResults(searchResult.list,searchResult.countRows);

    expect(store.searchResults()).toBe(searchResult.list);
    expect(store.totalCount()).toBe(searchResult.countRows);
    expect(store.errorMessage()).toBe(null);
    expect(store.showError()).toBe(false);
    expect(store.loading()).toBe(false);
  });

  it('should set selected user ', () =>{
    store.setSelectedUser(mockUser);

    expect(store.selectedUser()).toBe(mockUser);
    expect(store.errorMessage()).toBe(null);
    expect(store.showError()).toBe(false);
    expect(store.loading()).toBe(false);
  });

  it('should set created user id ', () =>{
    store.setCreatedUserId(mockUser.publicId);

    expect(store.createdUserId()).toBe(mockUser.publicId);
    expect(store.errorMessage()).toBe(null);
    expect(store.showError()).toBe(false);
    expect(store.loading()).toBe(false);
  });
});
