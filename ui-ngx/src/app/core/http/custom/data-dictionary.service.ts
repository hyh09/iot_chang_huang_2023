import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';
import { Observable } from 'rxjs';
import { DataDictionary } from '@app/shared/models/custom/device-mng.models';
import { PageLink } from '@app/shared/models/page/page-link';
import { PageData } from '@app/shared/models/page/page-data';

@Injectable({
  providedIn: 'root'
})
export class DataDictionaryService {

  constructor(
    private http: HttpClient
  ) { }
  
  public getDataDictionaries(pageLink: PageLink, config?: RequestConfig): Observable<PageData<DataDictionary>> {
    return this.http.get<PageData<DataDictionary>>(`/api/tenant/assetInfos${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  public getDataDictionary(dictionaryId: string, config?: RequestConfig): Observable<DataDictionary> {
    return this.http.get<DataDictionary>(`/api/asset/info/${dictionaryId}`, defaultHttpOptionsFromConfig(config));
  }

  public saveDataDictionary(dataDictionary: DataDictionary, config?: RequestConfig): Observable<DataDictionary> {
    return this.http.post<DataDictionary>('/api/asset', dataDictionary, defaultHttpOptionsFromConfig(config));
  }

  public deleteDataDictionary(dictionaryId: string, config?: RequestConfig) {
    return this.http.delete(`/api/asset/${dictionaryId}`, defaultHttpOptionsFromConfig(config));
  }

}
