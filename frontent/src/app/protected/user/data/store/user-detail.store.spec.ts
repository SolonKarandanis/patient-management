import {UserDetailStore} from './user-detail.store';
import {UserRepository} from '../repositories/user.repository';
import {UtilService} from '@core/services/util.service';
import {TestBed} from '@angular/core/testing';
import {
  mockChangePasswordRequest,
  mockCreateUserRequest,
  mockUpdateUserRequest,
  mockUser,
} from '@testing/mockData';
import {of} from 'rxjs';
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from '@ngx-translate/core';

type UserDetailStore = InstanceType<typeof UserDetailStore>;


describe('UserDetailStore', () =>{
  let store: UserDetailStore;
  let userRepoSpy: jasmine.SpyObj<UserRepository>;
  let utilServiceSpy: jasmine.SpyObj<UtilService>;

  beforeEach(()=>{
    userRepoSpy = jasmine.createSpyObj('UserRepository',[
      'getUserById',
      'registerUser',
      'updateUser',
      'deleteUser',
      'activateUser',
      'deactivateUser',
      'changeUserPassword'
    ]);
    utilServiceSpy = jasmine.createSpyObj('UtilService',[
      'showMessage',
    ]);

    TestBed.configureTestingModule({
      imports:[
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader }
        })
      ],
      providers:[
        {
          provide: UserRepository,
          useValue: userRepoSpy,
        },
        {
          provide: UtilService,
          useValue: utilServiceSpy,
        },
      ]
    });

    store = TestBed.inject(UserDetailStore);
  });

  it('should be created', () => {
    expect(store).toBeTruthy();
  });

  it('should load user by id ', (done) =>{
    const userId: string = '1';
    userRepoSpy.getUserById.and.returnValue(of(mockUser));

    store.loadUserById(userId).subscribe((result) => {
      expect(result).toBe(mockUser);
      expect(userRepoSpy.getUserById).toHaveBeenCalledWith(userId);
      expect(userRepoSpy.getUserById).toHaveBeenCalledTimes(1);
      expect(store.selectedUser()).toBe(mockUser);
      done();
    });
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

  it('should change user password', () => {
    const userId: string = '1';
    userRepoSpy.changeUserPassword.and.returnValue(of(mockUser));

    store.changeUserPassword({id:userId,request:mockChangePasswordRequest});

    expect(userRepoSpy.changeUserPassword).toHaveBeenCalledWith(userId,mockChangePasswordRequest);
    expect(userRepoSpy.changeUserPassword).toHaveBeenCalledTimes(1);
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

  it('should set selected user ', () =>{
    store.setSelectedUser(mockUser);

    expect(store.selectedUser()).toBe(mockUser);
    expect(store.error()).toBe(null);
    expect(store.loading()).toBe(false);
  });

  it('should set created user id ', () =>{
    store.setCreatedUserId(mockUser.publicId);

    expect(store.createdUserId()).toBe(mockUser.publicId);
    expect(store.error()).toBe(null);
    expect(store.loading()).toBe(false);
  });
});
