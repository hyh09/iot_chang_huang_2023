import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';
import { PageData, TimePageLink } from '@app/shared/public-api';
import { Observable } from 'rxjs';
import { DeviceCapacityList, DeviceEnergyConsumption, DeviceEnergyConsumptionList, PotencyInterval, PotencyTop10, RunningState } from '@app/shared/models/custom/potency.models';
import { DeviceProp } from '@app/shared/models/custom/device-monitor.models';
import { map, tap } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';

interface FilterParams {
  factoryId?: string;
  workshopId?: string;
  productionLineId?: string;
  deviceId?: string;
}

@Injectable({
  providedIn: 'root'
})

export class PotencyService {

  constructor(
    private http: HttpClient,
    private translate: TranslateService
  ) { }

  // 查询设备产量列表
  public getDeviceCapacityList(pageLink: TimePageLink, params: FilterParams, config?: RequestConfig): Observable<DeviceCapacityList> {
    let queryStr: string[] = [];
    if (params) {
      Object.keys(params).forEach(key => {
        queryStr.push(`${key}=${params[key]}`);
      });
    }
    const { startTime, endTime } = pageLink;
    const timeDiff = (endTime - startTime) / 1000 / 60 / 60 / 24;
    return this.http.get<DeviceCapacityList>(`/api/pc/efficiency/queryCapacity${timeDiff < 5 ? 'OnSecondLeve' : ''}${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 查询设备产量历史列表
  public getDeviceCapacityHistoryList(pageLink: TimePageLink, deviceId: string, config?: RequestConfig): Observable<DeviceCapacityList> {
    return this.http.get<DeviceCapacityList>(
      `/api/pc/efficiency/queryCapacityHistory${pageLink.toQuery()}&deviceId=${deviceId}`,
      defaultHttpOptionsFromConfig(config)).pipe(map(res => {
        if (res && res.data && res.data.length > 0) {
          res.data.forEach((item, index) => {
            const nextItem = res.data[index + 1] || res.nextData;
            let value: number;
            if (nextItem) {
              const nextVal = parseFloat(nextItem.value || '0')
              value = nextVal <= 0 ? 0 : (parseFloat(item.value || '0') * 100 - nextVal * 100) / 100;
              if (value < 0) {
                value = 0;
              }
            } else {
              value = 0;
            }
            item.value = value + '';
          });
        }
        return res;
      }));
  }

  // 导出设备产量历史列表
  public exportDeviceCapacityHistoryList(pageLink: TimePageLink, deviceId: string) {
    return this.http.get(
      `/api/pc/efficiency/excelCapacityHistory${pageLink.toQuery()}&deviceId=${deviceId}`, { responseType: 'arraybuffer' }).pipe(tap(res => {
        var blob = new Blob([res], {type: 'application/vnd.ms-excel;'});
        var link = document.createElement('a');
        var href = window.URL.createObjectURL(blob);
        link.href = href;
        link.download = this.translate.instant('potency.device-capacity-history');
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(href);
      }));
  }

  // 获取能耗分析数据列表
  public getEnergyConsumptionDatas(pageLink: TimePageLink, params: FilterParams, config?: RequestConfig): Observable<DeviceEnergyConsumptionList> {
    let queryStr: string[] = [];
    if (params) {
      Object.keys(params).forEach(key => {
        queryStr.push(`${key}=${params[key]}`);
      });
    }
    const { startTime, endTime } = pageLink;
    const timeDiff = (endTime - startTime) / 1000 / 60 / 60 / 24;
    return this.http.get<DeviceEnergyConsumptionList>(
      `/api/pc/efficiency/queryEntityByKeysNew${timeDiff < 5 ? 'OnSecondLeve' : ''}${pageLink.toQuery()}&${queryStr.join('&')}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取设备能耗历史数据列表
  public getEnergyHistoryDatas(pageLink: TimePageLink, deviceId: string, config?: RequestConfig): Observable<PageData<DeviceEnergyConsumption>> {
    return this.http.get<PageData<DeviceEnergyConsumption>>(
      `/api/pc/efficiency/queryEnergyHistoryNew${pageLink.toQuery()}&deviceId=${deviceId}`,
      defaultHttpOptionsFromConfig(config)).pipe(map(res => {
        if (res && res.data && res.data.length > 0) {
          const othersKeys = ['createdTime', 'rename', 'deviceName'];
          const valueKeys = Object.keys(res.data[0]).filter(key => !othersKeys.includes(key));
          res.data.forEach((item, index) => {
            const nextItem = res.data[index + 1] || res.nextData;
            let value: { [name: string]: number | string } = {};
            if (nextItem) {
              valueKeys.forEach(key => {
                const nextVal = parseFloat(nextItem[key] || '0')
                value[key] = nextVal <= 0 ? 0 : (parseFloat(item[key] || '0') * 100 - nextVal * 100) / 100;
                if (value[key] < 0) {
                  value[key] = 0;
                }
                value[key] += '';
              });
            } else {
              valueKeys.forEach(key => {
                value[key] = '0';
              });
            }
            Object.assign(item, value);
          });
        }
        return res;
      }));
  }

  // 导出设备能耗历史数据列表
  public exportEnergyHistoryDatas(pageLink: TimePageLink, deviceId: string) {
    return this.http.get(
      `/api/pc/efficiency/excelEnergyHistoryNew${pageLink.toQuery()}&deviceId=${deviceId}`, { responseType: 'arraybuffer' }).pipe(tap(res => {
        var blob = new Blob([res], {type: 'application/vnd.ms-excel;'});
        var link = document.createElement('a');
        var href = window.URL.createObjectURL(blob);
        link.href = href;
        link.download = this.translate.instant('potency.energy-consumption-history');
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(href);
      }));
  }

  // 获取设备的参数
  public getDeviceProps(deviceId: string, config?: RequestConfig): Observable<DeviceProp[]> {
    return this.http.get<DeviceProp[]>(`/api/pc/efficiency/queryDictName?deviceId=${deviceId}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取设备运行状态
  public getDeviceRunningState(params: { deviceId: string; attributeParameterList: DeviceProp[], startTime: number; endTime: number; }, config?: RequestConfig): Observable<RunningState[]> {
    return this.http.post<RunningState[]>(`/api/pc/efficiency/queryTheRunningStatusByDevice`, params, defaultHttpOptionsFromConfig(config));
  }

  // 查询产量或能耗Top10（keyNum(产量传空字符串)：1【水】，2【电】，3【气】；type：0【产量】，1【能耗】）
  public getTop10(params: {factoryId: string; keyNum: '1' | '2' | '3' | ''; type: '0' | '1';}, config?: RequestConfig): Observable<PotencyTop10> {
    return this.http.post<PotencyTop10>(`/api/pc/efficiency/queryTodayEffceency`, params, defaultHttpOptionsFromConfig(config)).pipe(map(res => {
      const maxVal = Math.max.apply(Math, (res || []).map(item => { return Number(item.value) })) || 0;
      (res || []).forEach(item => {
        item.percent = Number(item.value) / maxVal * 100;
      })
      return (res || []);
    }));
  }

  // 查询区间产量或能耗（keyNum(产量传空字符串)：1【水】，2【电】，3【气】；type：0【产量】，1【能耗】）
  public getIntervalData(params: {deviceId: string; startTime: number; endTime: number; keyNum: '1' | '2' | '3' | ''; type: '0' | '1';}, config?: RequestConfig): Observable<PotencyInterval> {
    const { deviceId, startTime, endTime, keyNum, type } = params;
    return this.http.get<PotencyInterval>(`/api/capacityDevice/getDeviceCapacity?deviceId=${deviceId}&startTime=${startTime}&endTime=${endTime}&keyNum=${keyNum}&type=${type}`, defaultHttpOptionsFromConfig(config));
  }

}