import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { defaultHttpOptionsFromConfig, RequestConfig } from "@app/core/public-api";
import { PageData, PageLink } from "@app/shared/public-api";
import { Observable } from "rxjs";
import { DeviceCapacityList, DeviceEnergyConsumptionList } from '@app/shared/models/custom/potency.models';

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

  // 查询设备产能列表
  public getDeviceCapacityList(pageLink: PageLink, params: FilterParams, config?: RequestConfig): Observable<DeviceCapacityList> {
    let queryStr: string[] = [];
    if (params) {
      Object.keys(params).forEach(key => {
        queryStr.push(`${key}=${params[key]}`);
      });
    }
    return this.http.get<DeviceCapacityList>(`/api/pc/efficiency/queryCapacity${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取能耗分析表头
  public getEnergyConsumptionTableHeader(config?: RequestConfig): Observable<string[]> {
    return this.http.get<string[]>(`/api/pc/efficiency/queryEntityByKeysHeader`, defaultHttpOptionsFromConfig(config));
  }

  // 获取能耗分析数据列表
  public getEnergyConsumptionDatas(pageLink: PageLink, params: FilterParams, config?: RequestConfig): Observable<DeviceEnergyConsumptionList> {
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
  public getEnergyHistoryDatas(pageLink: PageLink, deviceId: string, config?: RequestConfig): Observable<PageData<object>> {
    return this.http.get<PageData<object>>(
      `/api/pc/efficiency/queryEnergyHistory${pageLink.toQuery()}&deviceId=${deviceId}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

}