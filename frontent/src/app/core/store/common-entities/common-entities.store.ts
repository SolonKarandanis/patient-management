import {patchState, signalStore, withComputed, withMethods, withProps, withState} from '@ngrx/signals';
import {setError, setLoaded, setLoading, withCallState} from '@core/store/features/call-state.feature';
import {inject} from '@angular/core';
import {CommonEntitiesState, initialCommonEntitiesState} from '@core/store/common-entities/common-entities.state';
import {CommonEntitiesRepository} from '@core/repositories/common-entities.repository';
import {Role} from '@models/user.model';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {pipe, switchMap, tap} from 'rxjs';
import {tapResponse} from '@ngrx/operators';

export const CommonEntitiesStore = signalStore(
  { providedIn: 'root' },
  withState<CommonEntitiesState>(initialCommonEntitiesState),
  withCallState(),
  withProps(()=>({
    commonEntitiesRepo:inject(CommonEntitiesRepository),
  })),
  withComputed(({
    roles
  })=>({

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
      )
    })
  }),
);
