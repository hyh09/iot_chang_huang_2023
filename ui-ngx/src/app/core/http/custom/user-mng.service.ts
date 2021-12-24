import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { RequestConfig, defaultHttpOptionsFromConfig } from "@app/core/public-api";
import { UserInfo } from "@app/shared/models/custom/auth-mng.models";
import { PageLink, PageData } from "@app/shared/public-api";
import { Observable } from "rxjs";

interface FetchListFilter {
  factoryId?: string,
  userCode: string,
  userName: string
}

interface UserPwdParams {
  userId: string,
  password: string
}

@Injectable({
  providedIn: 'root'
})
export class UserMngService {

  constructor(
    private http: HttpClient
  ) { }

  // 获取用户列表
  public getUsers(pageLink: PageLink, filterParams: FetchListFilter, config?: RequestConfig): Observable<PageData<UserInfo>> {
    return this.http.get<PageData<UserInfo>>(
      `/api/user/findAll${pageLink.toQuery()}&userCode=${filterParams.userCode}&userName=${filterParams.userName}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取工厂管理员列表
  public getFactoryManagers(pageLink: PageLink, filterParams: FetchListFilter, config?: RequestConfig): Observable<PageData<UserInfo>> {
    return this.http.get<PageData<UserInfo>>(
      `/api/user/findFactoryManagers${pageLink.toQuery()}&factoryId=${filterParams.factoryId}&userCode=${filterParams.userCode}&userName=${filterParams.userName}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取用户详情
  public getUser(userId: string, config?: RequestConfig): Observable<UserInfo> {
    return this.http.get<UserInfo>(`/api/user/${userId}`, defaultHttpOptionsFromConfig(config));
  }

  // 新增或更新用户信息
  public saveUser(userInfo: UserInfo, config?: RequestConfig): Observable<UserInfo> {
    return this.http.post<UserInfo>('/api/user/save', userInfo, defaultHttpOptionsFromConfig(config));
  }

  // 删除用户
  public deleteUser(userId: string, config?: RequestConfig) {
    return this.http.delete(`/api/user/${userId}`, defaultHttpOptionsFromConfig(config));
  }

  // 修改用户密码
  public changeUserPwd(params: UserPwdParams): Observable<string> {
    return this.http.post(`/api/user/changeOthersPassword`, params, { responseType: 'text' });
  }

  // 获取当前可用的用户编码
  public getAvailableCode(tenantId?: string): Observable<string> {
    return this.http.post(`/api/user/getCode`, { key: "1", tenantId }, { responseType: 'text' });
  }

}