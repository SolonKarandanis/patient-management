import {patchState, signalStore, withComputed, withMethods, withProps, withState} from '@ngrx/signals';
import {setError, setLoaded, setLoading, withCallState} from '@core/store/features/call-state.feature';
import {computed, inject} from '@angular/core';
import {CommonEntitiesState, initialCommonEntitiesState} from '@core/store/common-entities/common-entities.state';
import {CommonEntitiesRepository} from '@core/repositories/common-entities.repository';
import {Role} from '@models/user.model';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {forkJoin, pipe, switchMap, tap} from 'rxjs';
import {tapResponse} from '@ngrx/operators';
import {SelectItem} from 'primeng/api';
import {UiService} from '@core/services/ui.service';

export const CommonEntitiesStore = signalStore(
  { providedIn: 'root' },
  withState<CommonEntitiesState>(initialCommonEntitiesState),
  withCallState(),
  withProps(()=>({
    commonEntitiesRepo:inject(CommonEntitiesRepository),
    uiService:inject(UiService),
  })),
  withComputed(({
    roles
  })=>({
    getRolesAsSelectItems: computed(()=>{
      const roleArray = roles();
      if(roleArray && Array.isArray(roleArray) && roleArray.length > 0){
        return roleArray.map((role: Role) => {
          return {label: role.nameLabel, value: role.name} as SelectItem;
        });
      }
      return [];
    })
  })),
  withMethods((state)=>{
    return ({
      setRoles(roles:Role[]){
        patchState(state,{roles});
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
      getAllRoles: rxMethod<void>(
        pipe(
          tap(() => {
            state.setLoadingState();
          }),
          switchMap(()=>
            commonEntitiesRepo.getAllRoles().pipe(
              tapResponse({
                next:(result)=>{
                  state.setRoles(result);
                  state.setLoadedState();
                },
                error: (error:string) =>{
                  state.setErrorState(error);
                }
              })
            )
          ),
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
              roles:commonEntitiesRepo.getAllRoles()
            });
          })
        )
      )
    })
  }),
);
