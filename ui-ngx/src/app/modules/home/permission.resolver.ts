import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { TenantMenuService } from "@app/core/http/custom/tenant-menu.service";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { MenuSection } from "@app/core/public-api";

interface Permissions {
  firstPath: string;
  menuSections: MenuSection[];
  menuBtnMap: { [key: string]: string[] };
}

@Injectable()
export class PermissionResolver implements Resolve<Permissions>  {

  constructor(
    private tenantMenuService: TenantMenuService
  ) { }

  resolve(): Observable<Permissions> {
    return this.tenantMenuService.getUserMenus().pipe(map(menus => {
      let firstPath: string = '';
      const menuSections: MenuSection[] = [];
      const menuBtnMap: { [key: string]: string[] } = {};
      console.log(menus)
      if (menus) {
        menus.filter(menu => (!menu.isButton)).forEach(menu => {
          menuBtnMap[menu.id] = [];
          if (menu.path && !firstPath) {
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
            isMdiIcon: menu.tenantMenuIcon.startsWith('mdi:'),
            parentId: menu.parentId
          };
          menuSections.push(menuSection);
          if (menu.isButton) {
            menuBtnMap[menu.parentId].push(menu.langKey);
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
        if (menu.parentId) {
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
