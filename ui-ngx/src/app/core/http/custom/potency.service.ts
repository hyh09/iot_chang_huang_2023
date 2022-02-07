import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';
import { PageData, TimePageLink } from "@app/shared/public-api";
import { Observable } from "rxjs";
import { DeviceCapacityList, DeviceEnergyConsumptionList, RunningState } from '@app/shared/models/custom/potency.models';
import { DeviceProp } from "@app/shared/models/custom/device-monitor.models";

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
    private http: HttpClient
  ) { }

  // 查询设备产量列表
  public getDeviceCapacityList(pageLink: TimePageLink, params: FilterParams, config?: RequestConfig): Observable<DeviceCapacityList> {
    let queryStr: string[] = [];
    if (params) {
      Object.keys(params).forEach(key => {
        queryStr.push(`${key}=${params[key]}`);
      });
    }
    return this.http.get<DeviceCapacityList>(`/api/pc/efficiency/queryCapacity${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 查询设备产量历史列表
  public getDeviceCapacityHistoryList(pageLink: TimePageLink, deviceId: string, config?: RequestConfig): Observable<DeviceCapacityList> {
    return this.http.get<DeviceCapacityList>(`/api/pc/efficiency/queryCapacityHistory${pageLink.toQuery()}&deviceId=${deviceId}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取能耗分析表头
  public getEnergyConsumptionTableHeader(config?: RequestConfig): Observable<string[]> {
    return this.http.get<string[]>(`/api/pc/efficiency/queryEntityByKeysHeader`, defaultHttpOptionsFromConfig(config));
  }

  // 获取能耗分析数据列表
  public getEnergyConsumptionDatas(pageLink: TimePageLink, params: FilterParams, config?: RequestConfig): Observable<DeviceEnergyConsumptionList> {
    let queryStr: string[] = [];
    if (params) {
      Object.keys(params).forEach(key => {
        queryStr.push(`${key}=${params[key]}`);
      });
    }
    return this.http.get<DeviceEnergyConsumptionList>(
      `/api/pc/efficiency/queryEntityByKeys${pageLink.toQuery()}&${queryStr.join('&')}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取设备能耗历史数据表头
  public getEnergyHistoryTableHeader(config?: RequestConfig): Observable<string[]> {
    return this.http.get<string[]>(`/api/pc/efficiency/queryEnergyHistoryHeader`, defaultHttpOptionsFromConfig(config));
  }

  // 获取设备能耗历史数据列表
  public getEnergyHistoryDatas(pageLink: TimePageLink, deviceId: string, config?: RequestConfig): Observable<PageData<object>> {
    return this.http.get<PageData<object>>(
      `/api/pc/efficiency/queryEnergyHistory${pageLink.toQuery()}&deviceId=${deviceId}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取设备的参数
  public getDeviceProps(deviceId: string, config?: RequestConfig): Observable<DeviceProp[]> {
    return this.http.get<DeviceProp[]>(`/api/pc/efficiency/queryDictName?deviceId=${deviceId}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取设备运行状态
  public getDeviceRunningState(params: { deviceId: string; keyNames: string[], startTime: number; endTime: number; }, config?: RequestConfig): Observable<RunningState> {
    return this.http.post<RunningState>(`/api/pc/efficiency/queryTheRunningStatusByDevice`, params, defaultHttpOptionsFromConfig(config));
  }

}