import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { TenantMenuService } from "@app/core/http/custom/tenant-menu.service";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { guid, HomeSection, HomeSectionPlace, MenuSection } from "@app/core/public-api";
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
      const menuSections: MenuSection[] = [{
        id: guid(),
        name: 'home.home',
        type: 'link',
        path: '/home',
        icon: 'home',
        notExact: true
      }];
      const homeSections: HomeSection[] = [];
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
          const homeSection: HomeSectionPlace = {
            id: menu.id,
            name: menu.langKey || '',
            path: menu.path,
            icon: menu.tenantMenuIcon,
            isMdiIcon: (menu.tenantMenuIcon || '').startsWith('mdi:'),
            parentId: menu.parentId
          };
          if (menu.isButton) {
            menuMap[menu.parentId] && menuBtnMap[menuMap[menu.parentId].path].push(menu.langKey);
          } else {
            menuSections.push(menuSection);
            homeSections.push({ ...homeSection, places: [] })
          }
        });
      }
      return {
        firstPath,
        menuSections: this.formatTree(menuSections, 'pages'),
        homeSections: this.formatTree(homeSections, 'places'),
        menuBtnMap
      };
    }));
  }

  private formatTree(data: { id?: string; parentId?: string; [key: string]: any }[], childrenKey = 'children') {
    const arr: any[] = [];
    const map = {};
    if (data) {
      data.forEach(item => {
        map[item.id] = item;
        item.isLeaf = true;
      });
      data.forEach(item => {
        if (item.parentId && map[item.parentId]) {
          const parent = map[item.parentId];
          if (!parent[childrenKey]) {
            parent[childrenKey] = [];
          }
          parent[childrenKey].push(item);
          parent.isLeaf = false;
        } else {
          arr.push(item);
        }
      })
    }
    return arr;
  }

}
