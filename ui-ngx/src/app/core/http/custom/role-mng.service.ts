import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { RequestConfig, defaultHttpOptionsFromConfig } from "@app/core/public-api";
import { Role } from "@app/shared/models/custom/auth-mng.models";
import { Observable } from "rxjs";


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

}