import { FileService } from '@app/core/http/custom/file.service';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { DeviceDataGroup, DeviceDictionary } from '@app/shared/models/custom/device-mng.models';
import { PageLink } from '@app/shared/models/page/page-link';
import { PageData } from '@app/shared/models/page/page-data';
import { HasUUID } from '@app/shared/public-api';
import { deepClone } from '@app/core/utils';

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
    private http: HttpClient,
    private fileService: FileService
  ) { }

  // 获取设备字典列表
  public getDeviceDictionaries(pageLink: PageLink, filterParams: FetchListFilter, config?: RequestConfig): Observable<PageData<DeviceDictionary>> {
    return this.http.get<PageData<DeviceDictionary>>(
      `/api/dict/device${pageLink.toQuery()}&code=${filterParams.code}&name=${filterParams.name}&supplier=${filterParams.supplier}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取设备字典详情
  public getDeviceDictionary(dictionaryId: HasUUID | string, config?: RequestConfig): Observable<DeviceDictionary> {
    return this.http.get<DeviceDictionary>(`/api/dict/device/${dictionaryId}`, defaultHttpOptionsFromConfig(config));
  }

  // 新增或修改设备字典
  public saveDeviceDictionary(deviceDictionary: DeviceDictionary, config?: RequestConfig): Observable<DeviceDictionary> {
    if (deviceDictionary.deviceModel) {
      const subject = new ReplaySubject<DeviceDictionary>();
      this.fileService.uploadFile(deviceDictionary.deviceModel).subscribe(fileId => {
        delete deviceDictionary.deviceModel;
        const params = deepClone(deviceDictionary);
        params.fileId = fileId;
        this.saveDeviceDictionaryInfo(params, config).subscribe(res => {
          subject.next(res);
          subject.complete();
        }, (err) => {
          subject.error(err);
        });
      });
      return subject;
    } else {
      return this.saveDeviceDictionaryInfo(deviceDictionary, config);
    }
  }

  private saveDeviceDictionaryInfo(deviceDictionary: DeviceDictionary, config?: RequestConfig): Observable<DeviceDictionary> {
    return this.http.post<DeviceDictionary>('/api/dict/device', deviceDictionary, defaultHttpOptionsFromConfig(config));
  }

  // 删除设备字典
  public deleteDeviceDictionary(dictionaryId: HasUUID, config?: RequestConfig) {
    return this.http.delete(`/api/dict/device/${dictionaryId}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取当前可用的设备字典编码
  public getAvailableCode(): Observable<string> {
    return this.http.get(`/api/dict/device/availableCode`, { responseType: 'text' });
  }

  // 获取所有设备字典
  public getAllDeviceDictionaries(config?: RequestConfig): Observable<DeviceDictionary[]> {
    return this.http.get<DeviceDictionary[]>(`/api/dict/device/all`, defaultHttpOptionsFromConfig(config));
  }

  // 获取初始化的设备参数分组及分组下的属性
  public getDeviceInitDataGroup(config?: RequestConfig): Observable<DeviceDataGroup[]> {
    return this.http.get<DeviceDataGroup[]>(`/api/dict/device/group/initData`, defaultHttpOptionsFromConfig(config));
  }

}
