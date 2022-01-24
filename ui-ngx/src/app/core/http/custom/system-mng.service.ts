import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';
import { FactoryVersion } from "@app/shared/models/custom/system-mng.models";
import { PageLink, PageData } from "@app/shared/public-api";
import { Observable } from "rxjs";

export interface SystemVersion {
  version: string;
  publishTime: number;
}

@Injectable({
  providedIn: 'root'
})
export class SystemMngService {

  constructor(
    private http: HttpClient
  ) { }

  // 获取工厂软件版本列表
  public getFactoryVersions(pageLink: PageLink, params: { factoryName: string; gatewayName: string }, config?: RequestConfig): Observable<PageData<FactoryVersion>> {
    return this.http.get<PageData<FactoryVersion>>(
      `/api/factory/findFactoryVersionList${pageLink.toQuery()}&factoryName=${params.factoryName}&gatewayName=${params.gatewayName}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取系统版本
  public getSystemVersion(config?: RequestConfig): Observable<SystemVersion> {
    return this.http.get<SystemVersion>(`/api/system/getSystemVersion`, defaultHttpOptionsFromConfig(config));
  }
  
}