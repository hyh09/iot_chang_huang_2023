import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';
import { Observable } from 'rxjs';
import { DataDictionary } from '@app/shared/models/custom/device-mng.models';
import { PageLink } from '@app/shared/models/page/page-link';
import { PageData } from '@app/shared/models/page/page-data';
import { HasUUID } from '@app/shared/public-api';
import { map } from 'rxjs/operators';

interface FetchListFilter {
  code: string,
  name: string,
  dictDataType: string
}

interface DataType {
  name: string,
  code: string
}

interface DataTypeResult {
  dictDataTypeList: Array<DataType>
}

@Injectable({
  providedIn: 'root'
})
export class DataDictionaryService {

  constructor(
    private http: HttpClient
  ) { }
  
  // 获取数据字典列表
  public getDataDictionaries(pageLink: PageLink, filterParams: FetchListFilter, config?: RequestConfig): Observable<PageData<DataDictionary>> {
    return this.http.get<PageData<DataDictionary>>(
      `/api/dict/data${pageLink.toQuery()}&code=${filterParams.code}&name=${filterParams.name}&dictDataType=${filterParams.dictDataType}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取数据字典详情
  public getDataDictionary(dictionaryId: HasUUID, config?: RequestConfig): Observable<DataDictionary> {
    return this.http.get<DataDictionary>(`/api/dict/data/${dictionaryId}`, defaultHttpOptionsFromConfig(config));
  }

  // 新增或更新数据字典
  public saveDataDictionary(dataDictionary: DataDictionary, config?: RequestConfig): Observable<DataDictionary> {
    return this.http.post<DataDictionary>('/api/dict/data', dataDictionary, defaultHttpOptionsFromConfig(config));
  }

  // 删除数据字典
  public deleteDataDictionary(dictionaryId: HasUUID, config?: RequestConfig) {
    return this.http.delete(`/api/dict/data/${dictionaryId}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取数据类型
  public getDataType(config?: RequestConfig): Observable<Array<DataType>> {
    return this.http.get<DataTypeResult>(`/api/dict/data/resource`, defaultHttpOptionsFromConfig(config)).pipe(map(result => {
      return result.dictDataTypeList;
    }));
  }

  // 获取当前可用的数据字典编码
  public getAvailableCode(): Observable<string> {
    return this.http.get(`/api/dict/data/availableCode`, { responseType: 'text' });
  }

  // 获取所有设备字典
  public getAllDataDictionaries(config?: RequestConfig): Observable<DataDictionary[]> {
    return this.http.get<DataDictionary[]>(`/api/dict/data/all`, defaultHttpOptionsFromConfig(config));
  }

}
