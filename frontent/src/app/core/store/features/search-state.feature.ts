import {signalStoreFeature, withState} from '@ngrx/signals';


export type SearchState ={
  criteriaCollapsed:boolean,
  hasSearched:boolean,
  tableLoading:boolean;
};

export const initialSearchState: SearchState={
  criteriaCollapsed:false,
  hasSearched:false,
  tableLoading:false,
}

export function withSearchState() {
  return signalStoreFeature(
    withState<SearchState>(initialSearchState),
  );
}

export function setTableLoading(): SearchState {
  return { tableLoading: true,criteriaCollapsed:true,hasSearched:true };
}

export function setTableLoaded(): SearchState {
  return { tableLoading: false,criteriaCollapsed:true,hasSearched:true};
}

export function resetSearchState() {
  return initialSearchState
}
