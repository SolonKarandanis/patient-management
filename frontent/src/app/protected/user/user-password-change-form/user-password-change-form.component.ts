import {ChangeDetectionStrategy, Component, input} from '@angular/core';
import {FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {TranslatePipe} from '@ngx-translate/core';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-user-password-change-form',
  imports: [
    TranslatePipe,
    ReactiveFormsModule,
    NgClass
  ],
  template: `
    @let form = formGroup();
    <form [formGroup]="form">
      <div class="flex-auto px-4 lg:px-10 py-10 pt-0 ">
        <div class="grid gap-6 mb-6 md:grid-cols-2">
          <div class="mt-4">
            <label for="password"
                   [ngClass]="{'app-required-label': form.enabled}">
              {{ 'USER.DETAILS.LABELS.password' | translate }}
            </label>
          </div>
        </div>
      </div>
    </form>
  `,
  styleUrl: './user-password-change-form.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserPasswordChangeFormComponent {

  formGroup = input.required<FormGroup>();

  protected isFieldValid(field: string): boolean | undefined {
    const control = this.formGroup().get(field);
    return (!control?.valid && control?.touched) ;
  }

}
