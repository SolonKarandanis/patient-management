import { Component } from '@angular/core';

@Component({
  selector: 'app-auth',
  standalone:false,
  template: `
    <div class="auth-container">
      <main>
        <section class="relative w-full h-full py-40 min-h-screen">
          <div
            class="absolute top-0 w-full h-full bg-blueGray-800 bg-no-repeat bg-full"
            style="background-image: url('../../assets/img/register_bg_2.png')"
          ></div>
          <router-outlet></router-outlet>
          <app-footer-small [absolute]="true"></app-footer-small>
        </section>
      </main>
    </div>
  `,
  styleUrl: './auth.component.css'
})
export class AuthComponent {

}
