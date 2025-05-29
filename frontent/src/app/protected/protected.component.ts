import {ChangeDetectionStrategy, Component, effect, inject} from '@angular/core';
import {CommonEntitiesService} from '@core/services/common-entities.service';

@Component({
  selector: 'app-protected',
  standalone:false,
  template: `
    <div>
      <app-sidebar></app-sidebar>
      <div class="relative md:ml-64 bg-blueGray-100">
        <app-header></app-header>
        <div class="px-4 md:px-10 mx-auto w-full -m-24">
          <div class="relative md:pt-32 pb-32 pt-12">
            <div class="min-h-[76vh]">
              <router-outlet></router-outlet>
            </div>
            <app-footer></app-footer>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrl: './protected.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProtectedComponent {
  protected commonEntitiesService = inject(CommonEntitiesService);

  constructor() {
    effect(() => {
      this.commonEntitiesService.initializeCommonEntities();
    });
  }

}
