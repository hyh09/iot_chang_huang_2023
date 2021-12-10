import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { TenantMenuService } from "@app/core/http/custom/tenant-menu.service";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { MenuSection } from "@app/core/public-api";
import { TenantMenu } from "@app/shared/public-api";
import { Permissions } from "@app/core/http/custom/tenant-menu.service";

@Injectable()
export class PermissionResolver implements Resolve<Permissions>  {

  constructor(
    private tenantMenuService: TenantMenuService
  ) { }

  resolve(): Observable<Permissions> {
    return this.tenantMenuService.getUserMenus().pipe(map(menus => {
      let firstPath: string = '';
      const menuSections: MenuSection[] = [];
      const menuMap: { [key: string]: TenantMenu } = {};
      const menuBtnMap: { [key: string]: string[] } = {};
      if (menus) {
        menus.filter(menu => (!menu.isButton && menu.path)).forEach(menu => {
          menuMap[menu.id] = menu;
          menuBtnMap[menu.path] = [];
          if (menu.path && !menu.hasChildren && !firstPath) {
            firstPath = menu.path;
          }
        });
        menus.forEach(menu => {
          const menuSection: MenuSection = {
            id: menu.id,
            name: menu.langKey || '',
            type: menu.hasChildren ? 'toggle' : 'link',
            path: menu.path,
            icon: menu.tenantMenuIcon,
            isMdiIcon: (menu.tenantMenuIcon || '').startsWith('mdi:'),
            parentId: menu.parentId
          };
          if (menu.isButton) {
            menuMap[menu.parentId] && menuBtnMap[menuMap[menu.parentId].path].push(menu.langKey);
          } else {
            menuSections.push(menuSection);
          }
        });
      }
      return {
        firstPath,
        menuSections: this.formatMenus(menuSections),
        menuBtnMap
      };
    }));
  }

  private formatMenus(menuSections: MenuSection[]): MenuSection[] {
    const arr: MenuSection[] = [];
    const map = {};
    if (menuSections) {
      menuSections.forEach(menu => {
        map[menu.id] = menu;
        menu.isLeaf = true;
      });
      menuSections.forEach(menu => {
        if (menu.parentId && map[menu.parentId]) {
          const parent: MenuSection = map[menu.parentId];
          if (!parent.pages) {
            parent.pages = [];
          }
          parent.pages.push(menu);
          parent.isLeaf = false;
        } else {
          arr.push(menu);
        }
      })
    }
    return arr;
  }

}
