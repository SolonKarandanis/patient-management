import {computed, Service} from '@angular/core';
import {BaseRepository} from '@core/repositories/BaseRepository';
import {rxResource} from '@angular/core/rxjs-interop';

@Service()
export class PatientRepository extends BaseRepository{
  // readonly weatherforecastResource = rxResource({
  //   loader: () => this.http.get<string[]>(`/api/weatherforecast`),
  // });
  //
  // readonly loading = computed(() => this.weatherforecastResource.isLoading());
  //
  // readonly desserts = computed(() => this.weatherforecastResource.value() ?? []);
  //
  // protected refresh() {
  //   this.weatherforecastResource.reload();
  // }
}
