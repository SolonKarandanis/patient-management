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
     @for(errorName of errorNames(); track errorName){
       <p class="mt-2 text-sm text-red-600 dark:text-red-500">
         @if(validationErrors(); as errors){
           {{
             (validationErrorsTranslationPrefix()[errorName]?? 'GLOBAL.FORMS.ERRORS') + errorName
               | translate :{requiredLength: errors['minLength']? errors['minLength']?.requiredLength: errors['maxLength']?.requiredLength}
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
  displayLabels = input<boolean>(false);
  validationErrors = input<ValidationErrors| null| undefined>();
  validationErrorsTranslationPrefix = input<any>({});

  errorNames = computed(() => {
    const errors = this.validationErrors();
    if(errors && Object.keys(errors).length > 0){
      return Object.keys(errors);
    }
    return [];
  });

}
