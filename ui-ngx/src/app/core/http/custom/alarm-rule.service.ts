import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';
import { AlarmRuleInfo } from "@app/shared/models/custom/device-monitor.models";
import { PageLink, PageData } from "@app/shared/public-api";
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AlarmRuleService {

  constructor(
    private http: HttpClient
  ) { }

  // 获取报警规则列表
  public getAlarmRules(pageLink: PageLink, name: string, config?: RequestConfig): Observable<PageData<AlarmRuleInfo>> {
    return this.http.get<PageData<AlarmRuleInfo>>(`/api/deviceMonitor/alarmRule/device/profile${pageLink.toQuery()}&name=${name}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取报警规则详情
  public getAlarmRule(id: string, config?: RequestConfig): Observable<AlarmRuleInfo> {
    return this.http.get<AlarmRuleInfo>(`/api/deviceMonitor/alarmRule/device/profile/${id}`, defaultHttpOptionsFromConfig(config));
  }

  // 保存报警规则
  public saveAlarmRule(alarmRuleInfo: AlarmRuleInfo, config?: RequestConfig): Observable<AlarmRuleInfo> {
    return this.http.post<AlarmRuleInfo>(`/api/deviceMonitor/alarmRule/device/profile`, alarmRuleInfo, defaultHttpOptionsFromConfig(config));
  }

  // 删除报警规则
  public deleteAlarmRule(id: string, config?: RequestConfig) {
    return this.http.delete(`/api/deviceMonitor/alarmRule/device/profile/${id}`, defaultHttpOptionsFromConfig(config));
  }

}