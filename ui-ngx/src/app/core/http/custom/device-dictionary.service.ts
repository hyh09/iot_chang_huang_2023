import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';
import { Observable } from 'rxjs';
import { DeviceDictionary } from '@app/shared/models/custom/device-mng.models';
import { PageLink } from '@app/shared/models/page/page-link';
import { PageData } from '@app/shared/models/page/page-data';
import { HasUUID } from '@app/shared/public-api';

export interface FetchListFilter {
  code: string,
  name: string,
  supplier: string
}

@Injectable({
  providedIn: 'root'
})
export class DeviceDictionaryService {

  constructor(
    private http: HttpClient
  ) { }

  // 获取设备字典列表
  public getDeviceDictionaries(pageLink: PageLink, filterParams: FetchListFilter, config?: RequestConfig): Observable<PageData<DeviceDictionary>> {
    return this.http.get<PageData<DeviceDictionary>>(
      `/api/dict/device${pageLink.toQuery()}&code=${filterParams.code}&name=${filterParams.name}&supplier=${filterParams.supplier}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取设备字典详情
  public getDeviceDictionary(dictionaryId: HasUUID, config?: RequestConfig): Observable<DeviceDictionary> {
    return this.http.get<DeviceDictionary>(`/api/dict/device/${dictionaryId}`, defaultHttpOptionsFromConfig(config));
  }

  // 新增或修改设备字典
  public saveDeviceDictionary(dataDictionary: DeviceDictionary, config?: RequestConfig): Observable<DeviceDictionary> {
    return this.http.post<DeviceDictionary>('/api/dict/device', dataDictionary, defaultHttpOptionsFromConfig(config));
  }

  // 删除设备字典
  public deleteDeviceDictionary(dictionaryId: HasUUID, config?: RequestConfig) {
    return this.http.delete(`/api/dict/device/${dictionaryId}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取当前可用的设备字典编码
  public getAvailableCode(): Observable<string> {
    return this.http.get(`/api/dict/device/availableCode`, { responseType: 'text' });
  }

}
