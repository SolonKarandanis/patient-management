import {ChangeDetectionStrategy, Component, inject, OnInit} from '@angular/core';
import {PageHeaderComponent} from '@components/page-header/page-header.component';
import {TranslatePipe} from '@ngx-translate/core';
import {UserService} from '../data/services/user.service';
import {USER_DETAILS_PROVIDERS, USERS_DETAILS} from './user-details.provider';
import {Skeleton} from 'primeng/skeleton';

@Component({
  selector: 'app-user-details',
  imports: [
    PageHeaderComponent,
    TranslatePipe,
    Skeleton
  ],
  template: `
    <div class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-blueGray-100 border-0">
      <app-page-header>
        {{ 'USER.DETAILS.title' | translate }}
      </app-page-header>
      @if(vm(); as vm){
        @defer(when vm.loading){
          {{ vm.user?.email }}
        } @placeholder {
          <p-skeleton
            width="10rem"
            height="2rem"/>
        }
      }
    </div>
  `,
  styleUrl: './user-details.component.css',
  providers:[
    USER_DETAILS_PROVIDERS
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailsComponent implements OnInit{
  private userService = inject(UserService);
  protected vm = inject(USERS_DETAILS);


  ngOnInit(): void {

  }

}
