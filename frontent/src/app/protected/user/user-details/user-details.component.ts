import {ChangeDetectionStrategy, Component, inject, OnInit} from '@angular/core';
import {PageHeaderComponent} from '@components/page-header/page-header.component';
import {TranslatePipe} from '@ngx-translate/core';
import {ActivatedRoute} from '@angular/router';
import {UserService} from '../data/services/user.service';

@Component({
  selector: 'app-user-details',
  imports: [
    PageHeaderComponent,
    TranslatePipe
  ],
  template: `
    <div class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-blueGray-100 border-0">
      <app-page-header>
        User Details
<!--        {{ 'USER.SEARCH.title' | translate }}-->
      </app-page-header>
    </div>
  `,
  styleUrl: './user-details.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailsComponent implements OnInit{
  private activatedRoute = inject(ActivatedRoute);
  private userService = inject(UserService);


  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }

}
