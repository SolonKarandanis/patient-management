import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-sign-up-with',
  imports: [],
  template: `
    <div class="rounded-t mb-0 px-6 py-6">
      <div class="text-center mb-3">
        <h6 class="text-blueGray-500 text-sm font-bold">Sign in with</h6>
      </div>
      <div class="btn-wrapper text-center">
        <button
          class="bg-white active:bg-blueGray-50 text-blueGray-700 font-normal px-4 py-2 rounded outline-none focus:outline-none mr-2 mb-1 uppercase shadow hover:shadow-md inline-flex items-center font-bold text-xs ease-linear transition-all duration-150"
          type="button"
        >
          <img alt="..." class="w-5 mr-1" src="assets/img/github.svg" />
          Github
        </button>
        <button
          class="bg-white active:bg-blueGray-50 text-blueGray-700 font-normal px-4 py-2 rounded outline-none focus:outline-none mr-1 mb-1 uppercase shadow hover:shadow-md inline-flex items-center font-bold text-xs ease-linear transition-all duration-150"
          type="button"
        >
          <img alt="..." class="w-5 mr-1" src="assets/img/google.svg" />
          Google
        </button>
      </div>
      <hr class="mt-6 border-b-1 border-blueGray-300" />
    </div>
  `,
  styleUrl: './sign-up-with.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SignUpWithComponent {

}
