import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { defaultHttpOptionsFromConfig, RequestConfig } from "@app/core/public-api";
import { Factory, FactoryMngList, ProdDevice, ProdLine, WorkShop } from "@app/shared/models/custom/factory-mng.models";
import { BaseData, HasId } from "@app/shared/public-api";
import { Observable } from "rxjs";

export interface FetchListFilter {
  name?: string,
  workshopName?: string,
  productionlineName?: string,
  deviceName?: string
}

@Injectable({
  providedIn: 'root'
})

export class FactoryMngService {

  constructor(
    private http: HttpClient
  ) { }

  // 条件查询工厂列表（含车间、产线、设备）
  public getFactoryList(params?: FetchListFilter, config?: RequestConfig): Observable<FactoryMngList> {
    let queryStr: string[] = [];
    if (params) {
      Object.keys(params).forEach(key => {
        queryStr.push(`${key}=${params[key]}`);
      });
    }
    return this.http.get<FactoryMngList>(`/api/factory/findFactoryListByCdn?${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 新增/更新工厂信息
  public saveFactory(params: BaseData<HasId>, config?: RequestConfig) {
    return this.http.post(`/api/factory/save`, params, defaultHttpOptionsFromConfig(config));
  }

  // 获取工厂信息
  public getFactory(id: string, config?: RequestConfig): Observable<Factory> {
    return this.http.get<Factory>(`/api/factory/${id}`, defaultHttpOptionsFromConfig(config));
  }

  // 删除工厂
  public deleteFactory(id: string, config?: RequestConfig) {
    return this.http.delete(`/api/factory/delete/${id}`, defaultHttpOptionsFromConfig(config));
  }

  // 新增/更新车间信息
  public saveWorkShop(params: BaseData<HasId>, config?: RequestConfig) {
    return this.http.post(`/api/workshop/save`, params, defaultHttpOptionsFromConfig(config));
  }

  // 获取车间信息
  public getWorkShop(id: string, config?: RequestConfig): Observable<WorkShop> {
    return this.http.get<WorkShop>(`/api/workshop/${id}`, defaultHttpOptionsFromConfig(config));
  }

  // 删除车间
  public deleteWorkShop(id: string, config?: RequestConfig) {
    return this.http.delete(`/api/workshop/delete/${id}`, defaultHttpOptionsFromConfig(config));
  }

  // 新增/更新产线信息
  public saveProdLine(params: BaseData<HasId>, config?: RequestConfig) {
    return this.http.post(`/api/productionLine/save`, params, defaultHttpOptionsFromConfig(config));
  }

  // 获取产线信息
  public getProdLine(id: string, config?: RequestConfig): Observable<ProdLine> {
    return this.http.get<ProdLine>(`/api/productionLine/${id}`, defaultHttpOptionsFromConfig(config));
  }

  // 删除产线
  public deleteProdLine(id: string, config?: RequestConfig) {
    return this.http.delete(`/api/productionLine/delete/${id}`, defaultHttpOptionsFromConfig(config));
  }

  // 新增/更新设备信息
  public saveDevice(params: BaseData<HasId>, config?: RequestConfig) {
    return this.http.post(`/api/saveOrUpdDevice`, params, defaultHttpOptionsFromConfig(config));
  }

  // 获取设备信息
  public getDevice(id: string, config?: RequestConfig): Observable<ProdDevice> {
    return this.http.get<ProdDevice>(`/api/device/getDeviceInfo/${id}`, defaultHttpOptionsFromConfig(config));
  }

  // 移除设备
  public removeDevice(params: { deviceIdList: string[]; productionLineId: string; }, config?: RequestConfig) {
    return this.http.post(`/api/removeDevice`, params, defaultHttpOptionsFromConfig(config));
  }

  // 获取所有工厂
  public getAllFactories(config?: RequestConfig): Observable<Factory[]> {
    return this.http.get<Factory[]>(`/api/factory/findFactoryListByLoginRole`, defaultHttpOptionsFromConfig(config));
  }

  // 获取所有车间
  public getAllWorkShops(factoryId?: string, tenantId?: string, config?: RequestConfig): Observable<WorkShop[]> {
    return this.http.get<WorkShop[]>(`/api/workshop/findWorkshopListByTenant?factoryId=${factoryId || ''}&tenantId=${tenantId || ''}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取所有产线
  public getAllProdLines(factoryId?: string, workshopId?: string, tenantId?: string, config?: RequestConfig): Observable<ProdLine[]> {
    return this.http.get<ProdLine[]>(`/api/productionLine/findProductionLineList?factoryId=${factoryId || ''}&workshopId=${workshopId || ''}&tenantId=${tenantId || ''}`, defaultHttpOptionsFromConfig(config));
  }

  // 分配设备
  public distributeDevice(params: { deviceIdList: string[]; factoryId: string; workshopId: string; productionLineId: string; }, config?: RequestConfig) {
    return this.http.post(`/api/distributionDevice`, params, defaultHttpOptionsFromConfig(config));
  }

}