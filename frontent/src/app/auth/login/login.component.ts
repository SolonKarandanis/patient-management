import {ChangeDetectionStrategy, Component, effect, inject, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {SignUpWithComponent} from '../../components/sign-up-with/sign-up-with.component';
import {InputText} from 'primeng/inputtext';
import {FloatLabel} from 'primeng/floatlabel';
import {Password} from 'primeng/password';
import {ButtonDirective} from 'primeng/button';
import {Ripple} from 'primeng/ripple';
import {AuthService} from '@core/services/auth.service';
import {BaseComponent} from '@shared/abstract/BaseComponent';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {SubmitCredentialsDTO} from '@models/auth.model';
import {TranslatePipe} from '@ngx-translate/core';
import {FormErrorComponent} from '@components/form-error/form-error.component';

@Component({
  selector: 'app-login',
  imports: [
    RouterLink,
    SignUpWithComponent,
    InputText,
    FloatLabel,
    Password,
    ButtonDirective,
    Ripple,
    TranslatePipe,
    ReactiveFormsModule,
    FormErrorComponent
  ],
  template: `
    <div class="container mx-auto px-4 h-full">
      <div class="flex content-center items-center justify-center h-full">
        <div class="w-full lg:w-4/12 px-4">
          <div
            class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg
            rounded-lg bg-blueGray-200 border-0">
            <app-sign-up-with></app-sign-up-with>
            <div class="flex-auto px-4 lg:px-10 py-10 pt-0">
              <div class="text-blueGray-400 text-center mb-3 font-bold">
                <small>{{ 'GLOBAL.sign-in-with-credentials' | translate }}</small>
              </div>
              <form [formGroup]="form">
                <div class="mb-6">
                  <p-float-label variant="on" class="w-full mb-3">
                    <input
                      id="email"
                      pInputText
                      type="email"
                      class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                      formControlName="email"
                      autocomplete="email"/>
                    <label for="email">{{ 'LOGIN.LABELS.email' | translate }}</label>
                  </p-float-label>
                  <app-form-error
                    [displayLabels]="isFieldValid('email')"
                    [validationErrors]="form.get('email')?.errors" />
                </div>
                <div class="mb-6">
                  <p-float-label variant="on" class="w-full mb-3">
                    <p-password
                      id="password"
                      inputStyleClass="border-0 !bg-white text-sm shadow w-full !text-black"
                      formControlName="password"
                      [feedback]="false"
                      [toggleMask]="true" />
                    <label for="password">{{ 'LOGIN.LABELS.password' | translate }}</label>
                  </p-float-label>
                  <app-form-error
                    [displayLabels]="isFieldValid('password')"
                    [validationErrors]="form.get('password')?.errors" />
                </div>
                <div class="text-center mt-6">
                  <button
                    pButton
                    pRipple
                    severity="secondary"
                    class=" font-bold uppercase px-6 py-3 rounded shadow mr-1 mb-1 w-full "
                    type="button"
                    (click)="login()"
                    [loading]="isLoading()"
                    [disabled]="isLoading()">
                    {{ "LOGIN.BUTTONS.login" | translate }}
                  </button>
                </div>
              </form>
            </div>
          </div>
          <div class="flex flex-wrap mt-6 relative">
            <div class="w-1/2">
              <a href="javascript:void(0)" class="text-blueGray-200">
                <small>{{ "LOGIN.BUTTONS.forgot-pass" | translate }}</small>
              </a>
            </div>
            <div class="w-1/2 text-right">
              <a [routerLink]="['/auth/register']" class="text-blueGray-200">
                <small>{{ "LOGIN.BUTTONS.register" | translate }}</small>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrl: './login.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginComponent extends BaseComponent implements OnInit{
  private authService = inject(AuthService);
  private fb= inject(FormBuilder);
  private router= inject(Router);

  public isLoading = this.authService.isLoading;

  constructor() {
    super();
    this.listenToSuccessfullLogin();
  }

  ngOnInit(): void {
    this.initForm();
  }

  public login():void{
    if (this.form.invalid) {
      Object.keys(this.controls).forEach(controlName =>
        this.controls[controlName].markAsTouched()
      );
      return;
    }
    const request:SubmitCredentialsDTO={
      email: this.controls['email'].value,
      password: this.controls['password'].value,
    }
    this.authService.login(request);
  }

  private initForm():void{
    this.form = this.fb.group({
      email:[
        '',
        Validators.compose([
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(320),
        ]),
      ],
      password:[
        '',
        Validators.compose([
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(100),
        ]),
      ]
    });
  }

  private listenToSuccessfullLogin():void{
    effect(() => {
      const loggedIn = this.authService.isLoggedIn();
      if (loggedIn) {
        this.navigateToHome();
      }
    });
  }

  private navigateToHome():void{
    this.router.navigate(['/dashboard'], {
      queryParams: {},
    });
  }
}
