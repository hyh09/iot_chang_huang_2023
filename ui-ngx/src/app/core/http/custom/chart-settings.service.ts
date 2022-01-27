import { HasUUID } from '@app/shared/public-api';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';
import { Observable } from 'rxjs';
import { DeviceDictionary } from '@app/shared/models/custom/device-mng.models';
import { PageLink } from '@app/shared/models/page/page-link';
import { PageData } from '@app/shared/models/page/page-data';
import { Chart, ChartProp } from '@app/shared/models/custom/chart-settings.model';
import { map } from 'rxjs/operators';

interface FetchListFilter {
  code: string,
  name: string
}

@Injectable({
  providedIn: 'root'
})
export class ChartSettingsService {

  constructor(
    private http: HttpClient
  ) { }

  // 获取设备字典列表
  public getDeviceDictionaries(pageLink: PageLink, filterParams: FetchListFilter, config?: RequestConfig): Observable<PageData<DeviceDictionary>> {
    return this.http.get<PageData<DeviceDictionary>>(
      `/api/dict/device${pageLink.toQuery()}&code=${filterParams.code}&name=${filterParams.name}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取设备字典关联的图表
  public getCharts(dictDeviceId: string, config?: RequestConfig): Observable<PageData<Chart>> {
    return this.http.get<Chart[]>(`/api/dict/device/graphs?dictDeviceId=${dictDeviceId}`, defaultHttpOptionsFromConfig(config)).pipe(map(res => {
      return {
        data: res || [],
        totalPages: 1,
        totalElements: (res || []).length,
        hasNext: false
      }
    }));
  }

  // 获取图表详情
  public getChart(graphId: HasUUID, config?: RequestConfig): Observable<Chart> {
    return this.http.get<Chart>(`/api/dict/device/graph/${graphId}`, defaultHttpOptionsFromConfig(config));
  }

  // 保存图表
  public saveChart(dictDeviceId: string, chart: Chart, config?: RequestConfig): Observable<Chart> {
    return this.http.post<Chart>(`/api/dict/device/${dictDeviceId}/graph`, chart, defaultHttpOptionsFromConfig(config));
  }

  // 删除图表
  public deleteChart(graphId: HasUUID, config?: RequestConfig) {
    return this.http.delete(`/api/dict/device/graph/${graphId}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取设备字典的所有属性（不分页）
  public getDeviceDictProps(dictDeviceId: string, config?: RequestConfig): Observable<ChartProp[]> {
    return this.http.get<ChartProp[]>(`/api/dict/device/all/properties?dictDeviceId=${dictDeviceId}`, defaultHttpOptionsFromConfig(config));
  }

}