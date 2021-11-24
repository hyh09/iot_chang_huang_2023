import { map } from 'rxjs/operators';
import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { defaultHttpOptionsFromConfig, RequestConfig } from "@app/core/public-api";
import { Menu, MenuType } from "@app/shared/models/custom/menu-mng.models";
import { PageLink, PageData, HasUUID } from "@app/shared/public-api";
import { Observable } from "rxjs";

export interface FetchListFilter {
  name: string,
  menuType: MenuType
}

@Injectable({
  providedIn: 'root'
})

export class MenuMngService {

  constructor(
    private http: HttpClient
  ) { }

  // 获取除按钮外的所有菜单
  public getSuperMenus(menuType: MenuType, currMenuId?: string, config?: RequestConfig): Observable<Array<Menu>> {
    return this.http.get<Array<Menu>>(`/api/menu/getMenuListByCdn?menuType=${menuType}&isButton=false`, defaultHttpOptionsFromConfig(config)).pipe(map(menus => {
      return menus.filter(menu => (menu.id + '' !== currMenuId));
    }));
  }

  // 获取菜单列表
  public getMenus(pageLink: PageLink, filterParams: FetchListFilter, config?: RequestConfig): Observable<PageData<Menu>> {
    return this.http.get<PageData<Menu>>(`/api/menu/getMenuPage${pageLink.toQuery()}&name=${filterParams.name}&menuType=${filterParams.menuType}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取菜单详情
  public getMenu(menuId: HasUUID, config?: RequestConfig): Observable<Menu> {
    return this.http.get<Menu>(`/api/menu/${menuId}`, defaultHttpOptionsFromConfig(config));
  }

  // 新增或修改菜单
  public saveMenu(menu: Menu, config?: RequestConfig): Observable<Menu> {
    return this.http.post<Menu>('/api/menu/save', menu, defaultHttpOptionsFromConfig(config));
  }

  // 删除菜单
  public deleteMenu(menuId: HasUUID, config?: RequestConfig) {
    return this.http.delete(`/api/menu/delMenu/${menuId}`, defaultHttpOptionsFromConfig(config));
  }

}