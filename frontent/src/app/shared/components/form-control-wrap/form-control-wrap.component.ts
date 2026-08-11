import {ChangeDetectionStrategy, Component, input} from '@angular/core';
import {HlmSkeletonImports} from '@components/ui/skeleton';

@Component({
  selector: 'app-form-control-wrap',
  imports: [
    HlmSkeletonImports
  ],
  template: `
    @if(!editMode() && !fetchingData()){
      <div class="min-h-9 p-2 border-2 border-solid rounded-md">
        {{displayValue()}}
      </div>
    }
    @if(fetchingData()){
      <hlm-skeleton class="w-full" [style.height]="skeletonHeightClass()" />
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
