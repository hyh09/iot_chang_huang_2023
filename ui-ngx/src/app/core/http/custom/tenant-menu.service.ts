import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { SysMenus, TenantMenus } from '@shared/models/tenant.model';
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';
import { MenuType } from '@app/shared/models/custom/menu-mng.models';

@Injectable({
  providedIn: 'root'
})
export class TenantMenuService {

  constructor(
    private http: HttpClient
  ) { }

  // 获取系统菜单（已配置的菜单会被标记）
  public getSysMenuList(menuType: MenuType, tenantId: string, name?: string, config?: RequestConfig): Observable<SysMenus> {
    return this.http.get<SysMenus>(
      `/api/menu/getTenantMenuListByTenantId?menuType=${menuType}&tenantId=${tenantId}&name=${name}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取租户菜单
  public getTenantMenuList(menuType: MenuType, tenantId: string, name?: string, config?: RequestConfig): Observable<TenantMenus> {
    return this.http.get<TenantMenus>(
      `/api/tenantMenu/getTenantMenuList?menuType=${menuType}&tenantId=${tenantId}&name=${name}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // public getTenantInfo(tenantId: string, config?: RequestConfig): Observable<TenantInfo> {
  //   return this.http.get<TenantInfo>(`/api/tenant/info/${tenantId}`, defaultHttpOptionsFromConfig(config));
  // }

  // public saveTenant(tenant: Tenant, config?: RequestConfig): Observable<Tenant> {
  //   return this.http.post<Tenant>('/api/tenant', tenant, defaultHttpOptionsFromConfig(config));
  // }

  // public deleteTenant(tenantId: string, config?: RequestConfig) {
  //   return this.http.delete(`/api/tenant/${tenantId}`, defaultHttpOptionsFromConfig(config));
  // }

}
