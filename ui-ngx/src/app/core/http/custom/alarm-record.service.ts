import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AlarmLevelType, AlarmRecord, AlarmStatusType } from '@app/shared/models/custom/device-monitor.models';
import { PageLink, PageData } from '@app/shared/public-api';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';
import { tap } from 'rxjs/operators';

interface FetchListFilter {
  factoryId?: string;
  workshopId?: string;
  productionLineId?: string;
  deviceId?: string;
  startTime?: number;
  endTime?: number;
  status?: AlarmStatusType,
  level?: AlarmLevelType
}

@Injectable({
  providedIn: 'root'
})
export class AlarmRecordService {

  constructor(
    private http: HttpClient,
    private translate: TranslateService
  ) { }

  // 获取报警记录列表
  public getAlarmRecords(pageLink: PageLink, filterParams: FetchListFilter, config?: RequestConfig): Observable<PageData<AlarmRecord>> {
    let queryStr: string[] = [];
    Object.keys(filterParams).forEach(key => {
      queryStr.push(`${key}=${filterParams[key]}`);
    });
    return this.http.get<PageData<AlarmRecord>>(`/api/deviceMonitor/alarmRecord${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 导出报警记录列表
  public exportAlarmRecords(pageLink: PageLink, filterParams: FetchListFilter) {
    let queryStr: string[] = [];
    Object.keys(filterParams).forEach(key => {
      queryStr.push(`${key}=${filterParams[key]}`);
    });
    return this.http.get(
      `/api/deviceMonitor/excelAlarmRecord${pageLink.toQuery()}&${queryStr.join('&')}`, { responseType: 'arraybuffer' }).pipe(tap(res => {
        var blob = new Blob([res], {type: 'application/vnd.ms-excel;'});
        var link = document.createElement('a');
        var href = window.URL.createObjectURL(blob);
        link.href = href;
        link.download = this.translate.instant('device-monitor.alarm-record');
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(href);
      }));
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