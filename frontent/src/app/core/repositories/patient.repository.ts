import {computed, Injectable} from '@angular/core';
import {BaseRepository} from '@core/repositories/BaseRepository';
import {rxResource} from '@angular/core/rxjs-interop';

@Injectable({
  providedIn: 'root',
})
export class PatientRepository extends BaseRepository{
  readonly weatherforecastResource = rxResource({
    loader: () => this.http.get<string[]>(`/api/weatherforecast`),
  });

  readonly loading = computed(() => this.weatherforecastResource.isLoading() || this.weatherforecastResource.isLoading());

  readonly desserts = computed(() => this.weatherforecastResource.value() ?? []);

  protected refresh() {
    this.weatherforecastResource.reload();
  }
}
