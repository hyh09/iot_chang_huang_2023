import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { SysMenus, TenantMenus } from '@shared/models/tenant.model';
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';
import { MenuType } from '@app/shared/models/custom/menu-mng.models';
import { NzTreeNodeOptions } from 'ng-zorro-antd/tree';
import { MenuSection } from '@app/core/public-api';

export interface Permissions {
  firstPath: string;
  menuSections: MenuSection[];
  menuBtnMap: { [key: string]: string[] };
}

@Injectable({
  providedIn: 'root'
})
export class TenantMenuService {

  constructor(
    private http: HttpClient
  ) { }

  // 获取系统菜单（不含按钮，已配置的菜单会被标记）
  public getSysMenuList(menuType: MenuType, tenantId: string, config?: RequestConfig): Observable<SysMenus> {
    return this.http.get<SysMenus>(
      `/api/menu/getTenantMenuListByTenantId?menuType=${menuType}&tenantId=${tenantId}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取租户菜单（不含按钮）
  public getTenantMenuList(menuType: MenuType, tenantId: string, config?: RequestConfig): Observable<TenantMenus> {
    return this.http.get<TenantMenus>(
      `/api/tenantMenu/getTenantMenuList?menuType=${menuType}&tenantId=${tenantId}&isButton=false`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 新增/修改租户菜单
  public saveTenantMenus(pcList: NzTreeNodeOptions[], appList: NzTreeNodeOptions[], tenantId: string, config?: RequestConfig) {
    return this.http.post(`/api/tenantMenu/saveOrUpdTenantMenu`, { pcList, appList, tenantId }, defaultHttpOptionsFromConfig(config));
  }

  // 获取除系统管理员外登录用户的菜单（含按钮）
  public getUserMenus(config?: RequestConfig): Observable<TenantMenus> {
    return this.http.post<TenantMenus>(`/api/roleMenu/queryByUser`, { menuType: MenuType.PC }, defaultHttpOptionsFromConfig(config));
  }
  
}
