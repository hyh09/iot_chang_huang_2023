///
/// Copyright © 2016-2021 The Thingsboard Authors
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { MenuType, SysMenus, TenantMenus } from '@shared/models/tenant.model';
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';

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
