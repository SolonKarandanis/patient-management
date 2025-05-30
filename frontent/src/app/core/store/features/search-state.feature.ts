import {signalStoreFeature, withState} from '@ngrx/signals';


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

  );
}

export function setTableLoading(): SearchState {
  return { tableLoading: true,criteriaCollapsed:true};
}

export function setTableLoaded(): SearchState {
  return { tableLoading: false,criteriaCollapsed:true};
}
