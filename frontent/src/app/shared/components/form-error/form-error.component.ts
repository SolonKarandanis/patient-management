import {ChangeDetectionStrategy, Component, computed, input} from '@angular/core';
import {ValidationErrors} from '@angular/forms';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-form-error',
  imports: [
    TranslatePipe
  ],
  template: `
   @if(displayLabels()){
     @for(errorName of errorKeys(); track errorName){
       <p class="mt-2 text-sm text-red-600 dark:text-red-500">
         @if(getValidationErrorsObject(); as errors){
           {{
             ((validationErrorsTranslationPrefix() ?? 'GLOBAL.FORMS.ERRORS.') + (errors[errorName]?.message ?? errorName))
               | translate :{requiredLength: errors[errorName]?.minLength?.requiredLength ?? errors[errorName]?.maxLength?.requiredLength}
           }}
         }
       </p>
     }
   }
  `,
  styleUrl: './form-error.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FormErrorComponent {
  displayLabels = input<boolean | undefined>(false);
  validationErrors = input<ValidationErrors| null| undefined>();
  validationErrorsTranslationPrefix = input<string|undefined>(undefined);

  errorKeys = computed(() => {
    const errors = this.validationErrors();
    if (Array.isArray(errors)) {
      return errors.map(errObj => errObj.kind).filter(Boolean);
    }
    if (errors && typeof errors === 'object') {
      return Object.keys(errors).filter(key => !key.startsWith('__'));
    }
    return [];
  });

  getValidationErrorsObject(): ValidationErrors | null {
    const errors = this.validationErrors();
    if (Array.isArray(errors)) {
      return errors.reduce((acc, errObj) => ({
        ...acc, [errObj.kind]: errObj
      }), {});
    }
    return errors ?? null;
  }

}
