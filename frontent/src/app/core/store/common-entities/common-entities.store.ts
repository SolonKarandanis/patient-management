import {patchState, signalStore, withComputed, withMethods, withProps, withState} from '@ngrx/signals';
import {setError, setLoaded, setLoading, withCallState} from '@core/store/features/call-state.feature';
import {computed, inject} from '@angular/core';
import {CommonEntitiesState, initialCommonEntitiesState} from '@core/store/common-entities/common-entities.state';
import {CommonEntitiesRepository} from '@core/repositories/common-entities.repository';
import {Role} from '@models/user.model';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {delay, forkJoin, pipe, switchMap, tap} from 'rxjs';
import {tapResponse} from '@ngrx/operators';
import {SelectItem} from 'primeng/api';
import {UiService} from '@core/services/ui.service';
import {ApplicationConfig} from '@models/application-config.model';

export const CommonEntitiesStore = signalStore(
  { providedIn: 'root' },
  withState<CommonEntitiesState>(initialCommonEntitiesState),
  withCallState(),
  withProps(()=>({
    commonEntitiesRepo:inject(CommonEntitiesRepository),
    uiService:inject(UiService),
  })),
  withComputed(({
    roles,
    appConfig
  })=>({
    getRolesAsSelectItems: computed(()=>{
      const roleArray = roles();
      if(roleArray && Array.isArray(roleArray) && roleArray.length > 0){
        return roleArray.map((role: Role) => {
          return {label: role.nameLabel, value: role.name} as SelectItem;
        });
      }
      return [];
    }),
    isManagementOfI18nResourcesEnabled: computed(()=>{
      const config=appConfig();
      if(!config){
        return false;
      }
      return config.MANAGE_I18N_RESOURCES_FUNCTIONALITY_ENABLED;
    }),
    isWebSocketsEnabled: computed(()=>{
      const config=appConfig();
      if(!config){
        return false;
      }
      return config.WEBSOCKETS_ENABLED;
    })
  })),
  withMethods((state)=>{
    return ({
      setRoles(roles:Role[]){
        patchState(state,{roles});
      },
      setAppConfig(appConfig:ApplicationConfig){
        patchState(state,{appConfig})
      },
      setLoadingState(){
        patchState(state, setLoading());
      },
      setLoadedState(){
        patchState(state, setLoaded());
      },
      setErrorState(error:string){
        patchState(state, setError(error));
      },
    });
  }),
  withMethods((state)=>{
    const commonEntitiesRepo = state.commonEntitiesRepo;
    const uiService = state.uiService;
    return ({
      initializePublicApplicationConfig:rxMethod<void>(
        pipe(
          tap(() => {
            state.setLoadingState();
            uiService.showScreenLoader();
          }),
          switchMap(()=>{
            return forkJoin({
              roles:commonEntitiesRepo.getAllRoles().pipe(
                tapResponse({
                  next:(result)=>{
                    state.setRoles(result);
                  },
                  error: (error:string) =>{
                    state.setErrorState(error);
                  }
                })
              ),
              appConfig:commonEntitiesRepo.getPublicApplicationConfig().pipe(
                tapResponse({
                  next:(result)=>{
                    state.setAppConfig(result);
                  },
                  error: (error:string) =>{
                    state.setErrorState(error);
                  }
                })
              )
            }).pipe(
              delay(500),
              tapResponse({
                next:(result)=>{},
                error: (error:string) =>{
                  state.setErrorState(error);
                },
                complete:()=>{
                  state.setLoadedState();
                  uiService.hideScreenLoader();
                }
              }),
            );
          })
        )
      ),
      initializeCommonEntities: rxMethod<void>(
        pipe(
          tap(() => {
            state.setLoadingState();
            uiService.showScreenLoader();
          }),
          switchMap(()=>{
            return forkJoin({
              roles:commonEntitiesRepo.getAllRoles().pipe(
                tapResponse({
                  next:(result)=>{
                    state.setRoles(result);
                  },
                  error: (error:string) =>{
                    state.setErrorState(error);
                  }
                })
              ),
              appConfig:commonEntitiesRepo.getApplicationConfig().pipe(
                tapResponse({
                  next:(result)=>{
                    state.setAppConfig(result);
                  },
                  error: (error:string) =>{
                    state.setErrorState(error);
                  }
                })
              )
            }).pipe(
              delay(500),
              tapResponse({
                next:(result)=>{},
                error: (error:string) =>{
                  state.setErrorState(error);
                },
                complete:()=>{
                  state.setLoadedState();
                  uiService.hideScreenLoader();
                }
              }),
            );
          })
        )
      )
    })
  }),
);
