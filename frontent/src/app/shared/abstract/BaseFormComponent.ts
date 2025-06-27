import {Directive} from '@angular/core';
import {FormControl, FormGroup, ValidationErrors} from '@angular/forms';

@Directive({
  standalone:true
})
export class BaseFormComponent{

  protected isFormSubmitted=false;

  protected isFormValid(form:FormGroup):boolean{
    return this.isFormSubmitted || (!form?.dirty &&  !form?.invalid && form.errors !==null);
  }

  protected getValidationError(formError: string,form:FormGroup):ValidationErrors | null | undefined{
    return form.errors?.[formError];
  }

  protected isFieldValidBase(field: string, form:FormGroup): boolean | undefined {
    const control = form.get(field);
    return (!control?.valid && control?.touched) || (control?.untouched && this.isFormSubmitted);
  }

  protected validateAllFormFields(form:FormGroup) {
    Object.keys(form.controls).forEach(field => {
      const control = form.get(field);
      if (control instanceof FormControl) {
        control.markAsTouched({ onlySelf: true });
      } else if (control instanceof FormGroup) {
        this.validateAllFormFields(form);
      }
    });
  }
}
