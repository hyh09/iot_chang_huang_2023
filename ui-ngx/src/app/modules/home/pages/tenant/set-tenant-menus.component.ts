import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AppState } from '@app/core/core.state';
import { TenantMenuService } from '@app/core/http/custom/tenant-menu.service';
import { UtilsService } from '@app/core/services/utils.service';
import { MenuType } from '@app/shared/models/custom/menu-mng.models';
import { DialogComponent, TenantInfo, TenantMenus } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { NzTreeNodeOptions } from 'ng-zorro-antd/tree';

export interface SetMenusDialogData {
  
}

@Component({
  selector: 'tb-set-tenant-menus',
  templateUrl: './set-tenant-menus.component.html',
  styleUrls: ['./set-tenant-menus.component.scss']
})
export class SetTenantMenusComponent extends DialogComponent<SetTenantMenusComponent, TenantMenus> implements OnInit {

  pcSysMenus: NzTreeNodeOptions[];
  pcTenantMenus: NzTreeNodeOptions[];
  appSysMenus: NzTreeNodeOptions[];
  appTenantMenus: NzTreeNodeOptions[];

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<SetTenantMenusComponent, TenantMenus>,
    protected tenantMenuService: TenantMenuService,
    @Inject(MAT_DIALOG_DATA) protected tenantInfo: TenantInfo,
    protected utils: UtilsService
  ) {
    super(store, router, dialogRef);
  }

  ngOnInit() {
    this.getMenuList(MenuType.PC);
  }

  getMenuList(menuType: MenuType, name?: string) {
    this.tenantMenuService.getSysMenuList(menuType, this.tenantInfo.id.id, name).subscribe(menus => {
      if (menus) {
        menus.forEach(menu => {
          menu.title = menu.name;
          menu.key = menu.id;
        });
        if (menuType === MenuType.PC) {
          this.pcSysMenus = this.utils.formatTree(menus);
        } else if (menuType === MenuType.APP) {
          this.appSysMenus = this.utils.formatTree(menus);
        }
      }
    });
    this.tenantMenuService.getTenantMenuList(menuType, this.tenantInfo.id.id, name).subscribe(menus => {
      if (menus) {
        menus.forEach(menu => {
          menu.title = menu.tenantMenuName;
          menu.key = menu.id;
        });
        if (menuType = MenuType.PC) {
          this.pcTenantMenus = this.utils.formatTree(menus);
        } else if (menuType = MenuType.APP) {
          this.appTenantMenus = this.utils.formatTree(menus);
        }
      }
    });
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {

  }

}
