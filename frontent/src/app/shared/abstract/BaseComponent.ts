import {Directive, inject, viewChildren} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {FormControl, FormGroup, ValidationErrors} from '@angular/forms';
import {FORM_INPUT} from '@shared/abstract/formInput.token';

@Directive({
  standalone:true
})
export class BaseComponent {
  protected translate = inject(TranslateService)

  protected form!: FormGroup;
  protected isFormSubmitted=false;

  inputChildren = viewChildren(FORM_INPUT);

  protected get controls() {
    return this.form.controls;
  }

  protected clear(){
    this.form.reset();
    this.isFormSubmitted=false;
    // this.inputChildren().forEach((input: FormInput) => {
    //   input.clear();
    // });
  }
  protected isFormValid():boolean{
    return this.isFormSubmitted || (!this.form?.dirty &&  !this.form?.invalid && this.form.errors !==null);
  }

  protected getValidationError(formError: string):ValidationErrors | null | undefined{
    return this.form.errors?.[formError];
  }

  protected isFieldValid(field: string): boolean | undefined {
    const control = this.form.get(field);
    return (!control?.valid && control?.touched) || (control?.untouched && this.isFormSubmitted);
  }

  protected validateAllFormFields() {
    Object.keys(this.form.controls).forEach(field => {
      const control = this.form.get(field);
      if (control instanceof FormControl) {
        control.markAsTouched({ onlySelf: true });
      } else if (control instanceof FormGroup) {
        this.validateAllFormFields();
      }
    });
  }
}
