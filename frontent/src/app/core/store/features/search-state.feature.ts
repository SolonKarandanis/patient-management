import {signalStoreFeature, withComputed, withState} from '@ngrx/signals';
import {computed} from '@angular/core';


export type SearchState ={
  criteriaCollapsed:boolean,
  tableLoading:boolean;
};

export const initialSearchState: SearchState={
  criteriaCollapsed:false,
  tableLoading:false,
}

export function withSearchState() {
  return signalStoreFeature(
    withState<SearchState>(initialSearchState),
    withComputed(({criteriaCollapsed,tableLoading}) => ({
      criteriaCollapsed: computed(() => criteriaCollapsed()=== true),
      tableLoading: computed(() => tableLoading() == false),
    }))
  );
}


export function setTableLoading(): SearchState {
  return { tableLoading: true,criteriaCollapsed:true};
}

export function setTableLoaded(): SearchState {
  return { tableLoading: false,criteriaCollapsed:true};
}
