import {Directive, effect, input, TemplateRef, ViewContainerRef} from '@angular/core';
import {Role} from '@models/user.model';
import {AuthService} from '@core/services/auth.service';

@Directive({
  selector: '[appAllowedRoles]'
})
export class AllowedRolesDirective {
  appAllowedRoles = input<number[]>([]);

  constructor(
    private readonly authService: AuthService,
    private readonly view: ViewContainerRef,
    private readonly template: TemplateRef<any>
  ) {
    effect(() => {
      this.hideShowTemplate();
    });
  }



  private hideShowTemplate():void{
    this.hideTemplate();
    const currentUserRolesIds: number[] =this.authService.loggedInUserRoleIds();
    currentUserRolesIds.every((roleId: number) => {
      if (this.appAllowedRoles().includes(roleId)) {
        this.showTemplate();
        return false;
      }
      return true;
    });
  }

  private hideTemplate(): void {
    this.view.clear();
  }

  private showTemplate(): void {
    this.view.createEmbeddedView(this.template);
  }

}
