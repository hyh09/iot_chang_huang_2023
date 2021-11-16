import { MenuTreeNodeOptions, MenuType } from '@app/shared/models/custom/menu-mng.models';
import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { RequestConfig, defaultHttpOptionsFromConfig } from "@app/core/public-api";
import { Role, UserInfo } from "@app/shared/models/custom/auth-mng.models";
import { PageLink, PageData, HasUUID } from "@app/shared/public-api";
import { Observable } from "rxjs";

interface FetchListFilter {
  roleCode: string,
  roleName: string
}

interface FetchRoleUserListFilter {
  userCode: string,
  userName: string,
  roleId: string
}

@Injectable({
  providedIn: 'root'
})
export class RoleMngService {

  constructor(
    private http: HttpClient
  ) { }

  // 获取所有角色
  public getAllRoles(config?: RequestConfig): Observable<Array<Role>> {
    return this.http.get<Array<Role>>(`/api/role/findAll`, defaultHttpOptionsFromConfig(config));
  }

  // 获取角色列表
  public getRoles(pageLink: PageLink, filterParams: FetchListFilter, config?: RequestConfig): Observable<PageData<Role>> {
    return this.http.get<PageData<Role>>(
      `/api/role/pageQuery${pageLink.toQuery()}&roleCode=${filterParams.roleCode}&roleName=${filterParams.roleName}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取角色详情
  public getRole(roleId: HasUUID, config?: RequestConfig): Observable<Role> {
    return this.http.get<Role>(`/api/role/getRoleById/${roleId}`, defaultHttpOptionsFromConfig(config));
  }

  // 新增或更新角色信息
  public saveRole(role: Role, config?: RequestConfig): Observable<Role> {
    return this.http.post<Role>('/api/role/save', role, defaultHttpOptionsFromConfig(config));
  }

  // 删除角色
  public deleteRole(roleId: HasUUID) {
    return this.http.delete(`/api/role/delete/${roleId}`, { responseType: 'text' });
  }

  // 获取当前可用的角色编码
  public getAvailableCode(): Observable<string> {
    return this.http.post(`/api/user/getCode`, { key: "2" }, { responseType: 'text' });
  }

  // 角色关联用户
  public bindUsers(bindParams: { userIds: HasUUID[], tenantSysRoleId: string }, config?: RequestConfig) {
    return this.http.post(`/api/role/relationUser`, bindParams, defaultHttpOptionsFromConfig(config));
  }

  // 角色取消关联用户
  public unbindUsers(unbindParams: { userIds: HasUUID[], tenantSysRoleId: string }, config?: RequestConfig) {
    return this.http.post(`/api/role/unboundUser`, unbindParams, defaultHttpOptionsFromConfig(config));
  }

  // 获取角色下的用户列表
  public getBindingUsers(pageLink: PageLink, params: FetchRoleUserListFilter, config?: RequestConfig): Observable<PageData<UserInfo>> {
    return this.http.get<PageData<UserInfo>>(
      `/api/role/getUserByInRole/${params.roleId}/users${pageLink.toQuery()}&userCode=${params.userCode}&userName=${params.userName}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取不在角色下的用户列表
  public getNotBindingUsers(pageLink: PageLink, params: FetchRoleUserListFilter, config?: RequestConfig): Observable<PageData<UserInfo>> {
    return this.http.get<PageData<UserInfo>>(
      `/api/role/getUserByNotInRole/${params.roleId}/users${pageLink.toQuery()}&userCode=${params.userCode}&userName=${params.userName}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 根据菜单类型和角色获取菜单列表
  public getMenusByRole(menuType: MenuType, roleId?: string, config?: RequestConfig): Observable<MenuTreeNodeOptions[]> {
    return this.http.post<MenuTreeNodeOptions[]>(`/api/roleMenu/queryAll`, { menuType, roleId }, defaultHttpOptionsFromConfig(config));
  }

  // 配置角色下的菜单及按钮权限
  public setRolePermissions(menuVoList: string[], semiSelectList: string[], roleId: string) {
    return this.http.post(`/api/roleMenu/binding`, { menuVoList, semiSelectList, roleId }, { responseType: 'text' });
  }

}