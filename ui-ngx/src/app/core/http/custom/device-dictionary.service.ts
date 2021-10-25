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

  // 获取数据字典列表
  public getDeviceDictionaries(pageLink: PageLink, filterParams: FetchListFilter, config?: RequestConfig): Observable<PageData<DeviceDictionary>> {
    return this.http.get<PageData<DeviceDictionary>>(
      `/api/dict/device${pageLink.toQuery()}&code=${filterParams.code}&name=${filterParams.name}&supplier=${filterParams.supplier}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  public getDeviceDictionary(dictionaryId: HasUUID, config?: RequestConfig): Observable<DeviceDictionary> {
    return this.http.get<DeviceDictionary>(`/api/asset/info/${dictionaryId}`, defaultHttpOptionsFromConfig(config));
  }

  public saveDeviceDictionary(dataDictionary: DeviceDictionary, config?: RequestConfig): Observable<DeviceDictionary> {
    return this.http.post<DeviceDictionary>('/api/asset', dataDictionary, defaultHttpOptionsFromConfig(config));
  }

  public deleteDeviceDictionary(dictionaryId: HasUUID, config?: RequestConfig) {
    return this.http.delete(`/api/asset/${dictionaryId}`, defaultHttpOptionsFromConfig(config));
  }

}
