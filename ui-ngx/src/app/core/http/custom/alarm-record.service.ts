import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AlarmLevelType, AlarmRecord, AlarmStatusType } from "@app/shared/models/custom/device-monitor.models";
import { PageLink, PageData } from "@app/shared/public-api";
import { Observable } from "rxjs";
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';

interface FetchListFilter {
  factoryId: string;
  workshopId: string;
  productionLineId: string;
  deviceId: string;
  startTime: number;
  endTime: number;
  status: AlarmStatusType,
  level: AlarmLevelType
}

@Injectable({
  providedIn: 'root'
})
export class AlarmRecordService {

  constructor(
    private http: HttpClient
  ) { }

  // 获取报警记录列表
  public getAlarmRecords(pageLink: PageLink, filterParams: FetchListFilter, config?: RequestConfig): Observable<PageData<AlarmRecord>> {
    let queryStr: string[] = [];
    Object.keys(filterParams).forEach(key => {
      queryStr.push(`${key}=${filterParams[key]}`);
    });
    return this.http.get<PageData<AlarmRecord>>(`/api/deviceMonitor/alarmRecord${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 确认报警信息
  public confirmAlarmRecord(id: string, config?: RequestConfig) {
    return this.http.post(`/api/deviceMonitor/alarmRecord/${id}/ack`, {}, defaultHttpOptionsFromConfig(config));
  }

  // 清除报警信息
  public clearAlarmRecord(id: string, config?: RequestConfig) {
    return this.http.post(`/api/deviceMonitor/alarmRecord/${id}/clear`, {}, defaultHttpOptionsFromConfig(config));
  }

}