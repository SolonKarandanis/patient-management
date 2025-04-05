import { inject, Injectable, Injector } from "@angular/core";
import { FormBuilder, FormControl, FormGroup } from "@angular/forms";

@Injectable({
  providedIn: 'root',
})
export class GenericService{

  protected formBuilder = inject(FormBuilder);
  protected injector = inject(Injector);

  protected isFormSubmitted=false;

  public validateAllFormFields(form:FormGroup) {
    Object.keys(form.controls).forEach(field => {
      const control = form.get(field);
      if (control instanceof FormControl) {
        control.markAsTouched({ onlySelf: true });
      } else if (control instanceof FormGroup) {
        this.validateAllFormFields(form);
      }
    });
  }

  public isFormValid(form:FormGroup){
    return this.isFormSubmitted || !form?.dirty
  }

}
