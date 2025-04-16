import { Component } from '@angular/core';

@Component({
  selector: 'app-auth',
  standalone:false,
  template: `
    <div class="auth-container">
      <main>
        <section class="relative w-full h-full py-40 min-h-screen">
          <router-outlet></router-outlet>
        </section>
      </main>
    </div>
  `,
  styleUrl: './auth.component.css'
})
export class AuthComponent {

}
