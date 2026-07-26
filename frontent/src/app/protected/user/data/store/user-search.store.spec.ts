import {UserSearchStore} from './user-search.store';
import {UserRepository} from '../repositories/user.repository';
import {UtilService} from '@core/services/util.service';
import {HttpUtil} from '@core/services/http-util.service';
import {SearchResult} from '@models/search.model';
import {TestBed} from '@angular/core/testing';
import {mockUser, mockUserSearchRequest} from '@testing/mockData';
import {of} from 'rxjs';
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from '@ngx-translate/core';

type UserSearchStore = InstanceType<typeof UserSearchStore>;


describe('UserSearchStore', () =>{
  let store: UserSearchStore;
  let userRepoSpy: jasmine.SpyObj<UserRepository>;
  let utilServiceSpy: jasmine.SpyObj<UtilService>;
  let httpUtilSpy: jasmine.SpyObj<HttpUtil>;
  let searchResult: SearchResult<any>;

  beforeEach(()=>{
    userRepoSpy = jasmine.createSpyObj('UserRepository',[
      'searchUsers',
      'exportUsersToCsv',
    ]);
    utilServiceSpy = jasmine.createSpyObj('UtilService',[
      'showMessage',
      'triggerFileDownLoad',
    ]);

    httpUtilSpy = jasmine.createSpyObj('HttpUtil',[
      'getFileNameForContentDisposition',
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
        {
          provide: HttpUtil,
          useValue: httpUtilSpy,
        },
      ]
    });

    store = TestBed.inject(UserSearchStore);

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

  it('should set search results ', () =>{
    searchResult.list = [mockUser];
    store.setSearchResults(searchResult.list,searchResult.countRows);

    expect(store.searchResults()).toBe(searchResult.list);
    expect(store.totalCount()).toBe(searchResult.countRows);
    expect(store.error()).toBe(null);
    expect(store.loading()).toBe(false);
  });
});
