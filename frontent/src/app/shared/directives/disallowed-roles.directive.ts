import {Directive, effect, input, TemplateRef, ViewContainerRef} from '@angular/core';
import {AuthService} from '@core/services/auth.service';

@Directive({
  selector: '[appDisallowedRoles]'
})
export class DisallowedRolesDirective {
  appDisallowedRoles = input<number[]>([]);

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
    this.showTemplate();
    const currentUserRolesIds: number[] =this.authService.loggedInUserRoleIds();
    currentUserRolesIds.every((roleId: number) => {
      if (this.appDisallowedRoles().includes(roleId)) {
        this.hideTemplate();
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
