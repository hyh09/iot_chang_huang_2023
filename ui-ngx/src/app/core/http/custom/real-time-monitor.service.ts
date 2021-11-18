import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { RequestConfig, defaultHttpOptionsFromConfig } from "@app/core/public-api";
import { AlarmRuleInfo, RealTimeData } from "@app/shared/models/custom/device-monitor.models";
import { FactoryTreeNodeIds } from "@app/shared/models/custom/factory-mng.models";
import { PageLink, PageData } from "@app/shared/public-api";
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class RealTimeMonitorService {

  constructor(
    private http: HttpClient
  ) { }

  // 获取实时监控数据
  public getRealTimeData(pageLink: PageLink, params: FactoryTreeNodeIds, config?: RequestConfig): Observable<RealTimeData> {
    let queryStr: string[] = [];
    if (params) {
      Object.keys(params).forEach(key => {
        queryStr.push(`${key}=${params[key]}`);
      });
    }
    return this.http.get<RealTimeData>(`/api/deviceMonitor/rtMonitor/device${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

}