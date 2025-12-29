import {ChangeDetectionStrategy, Component, input} from '@angular/core';
import {Skeleton} from 'primeng/skeleton';

@Component({
  selector: 'app-form-control-wrap',
  imports: [
    Skeleton
  ],
  template: `
    @if(!editMode() && !fetchingData()){
      <div class="min-h-9 p-2 border-2 border-solid rounded-md">
        {{displayValue()}}
      </div>
    }
    @if(fetchingData()){
      <p-skeleton height="{{skeletonHeightClass()}}">
      </p-skeleton>
    }
    @if(editMode() && !fetchingData()){
      <ng-content></ng-content>
    }

  `,
  styleUrl: './form-control-wrap.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FormControlWrapComponent {

  editMode = input<boolean>(false);
  fetchingData = input<boolean>(false);
  displayValue = input<string|null>('');
  skeletonHeightClass = input<string>('2rem');
}
