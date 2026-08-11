import {ChangeDetectionStrategy, Component, effect, inject, signal} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {SignUpWithComponent} from '../../components/sign-up-with/sign-up-with.component';
import {AuthService} from '@core/services/auth.service';
import {ReactiveFormsModule} from '@angular/forms';
import {SubmitCredentialsDTO} from '@models/auth.model';
import {TranslatePipe} from '@ngx-translate/core';
import {FormErrorComponent} from '@components/form-error/form-error.component';
import {LoginFormModel} from '../forms';
import {form, Field, required, email, submit} from '@angular/forms/signals';
import {UtilService} from '@core/services/util.service';
import {HlmInputImports} from '@components/ui/input';
import {HlmButtonImports} from '@components/ui/button';
import {HlmSpinnerImports} from '@components/ui/spinner';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {lucideEye, lucideEyeOff} from '@ng-icons/lucide';

@Component({
  selector: 'app-login',
  imports: [
    RouterLink,
    SignUpWithComponent,
    TranslatePipe,
    ReactiveFormsModule,
    FormErrorComponent,
    Field,
    HlmInputImports,
    HlmButtonImports,
    HlmSpinnerImports,
    NgIcon,
  ],
  providers: [provideIcons({lucideEye, lucideEyeOff})],
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
              <form>
                <div class="mb-6">
                  <label for="email" class="block uppercase text-blueGray-600 text-xs font-bold mb-2">
                    {{ 'LOGIN.LABELS.email' | translate }}
                  </label>
                  <input
                    id="email"
                    hlmInput
                    type="email"
                    class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                    [field]="loginForm.email"
                    autocomplete="email"/>
                  <app-form-error
                    [displayLabels]="loginForm.email().invalid() && loginForm.email().touched()"
                    [validationErrors]="loginForm.email().errors()" />
                </div>
                <div class="mb-6">
                  <label for="password" class="block uppercase text-blueGray-600 text-xs font-bold mb-2">
                    {{ 'LOGIN.LABELS.password' | translate }}
                  </label>
                  <div class="relative">
                    <input
                      id="password"
                      hlmInput
                      [type]="showPassword() ? 'text' : 'password'"
                      class="border-0 px-3 py-3 pr-10 !bg-white text-sm shadow w-full !text-black"
                      [field]="loginForm.password"
                      autocomplete="current-password"/>
                    <button
                      type="button"
                      class="absolute inset-y-0 right-2 flex items-center text-blueGray-600"
                      (click)="showPassword.set(!showPassword())"
                      [attr.aria-label]="showPassword() ? 'Hide password' : 'Show password'">
                      <ng-icon [name]="showPassword() ? 'lucideEyeOff' : 'lucideEye'" />
                    </button>
                  </div>
                  <app-form-error
                    [displayLabels]="loginForm.password().invalid() && loginForm.password().touched()"
                    [validationErrors]="loginForm.password().errors()" />
                </div>
                <div class="text-center mt-6">
                  <button
                    hlmBtn
                    variant="secondary"
                    class="font-bold uppercase px-6 py-3 rounded shadow mr-1 mb-1 w-full"
                    type="button"
                    (click)="login()"
                    [disabled]="isLoading()">
                    @if (isLoading()) {
                      <hlm-spinner class="mr-2" />
                    }
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
export class LoginComponent{
  private authService = inject(AuthService);
  private utilService = inject(UtilService);
  private router= inject(Router);

  public isLoading = this.authService.isLoading;

  protected showPassword = signal(false);

  private loginModel = signal<LoginFormModel>({
    email:'',
    password:''
  })

  public loginForm = form<LoginFormModel>(this.loginModel,(rootPath)=>{
    required(rootPath.email);
    email(rootPath.email);
    required(rootPath.password);
  });

  constructor() {
    this.listenToSuccessfullLogin();
  }


  public login(): void {
    if (this.loginForm().invalid()) {
      this.utilService.markAllAsTouched(this.loginForm, this.loginModel());
      return;
    }
    submit(this.loginForm,async (form)=>{
      const request: SubmitCredentialsDTO = {
        email: form.email().value(),
        password: form.password().value(),
      };
      this.authService.login(request);
    })
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
