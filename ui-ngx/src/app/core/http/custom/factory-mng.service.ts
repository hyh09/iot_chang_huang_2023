import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';
import { Factory, FactoryMngList, FactoryTreeList, ProdDevice, ProdLine, WorkShop } from "@app/shared/models/custom/factory-mng.models";
import { BaseData, HasId, PageData, PageLink } from "@app/shared/public-api";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { DeviceAuthProp, DeviceDataAuth } from "@app/shared/models/custom/device-mng.models";

interface FetchListFilter {
  name?: string,
  workshopName?: string,
  productionLineName?: string,
  deviceName?: string
}

export enum FactoryEntityType {
  FACTORY = 'FACTORY',
  WORKSHOP = 'WORKSHOP',
  PRODUCTION_LINE = 'PRODUCTION_LINE'
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

  // 根据当前登录人获取工厂层级数据
  public getFactoryTreeList(config?: RequestConfig): Observable<FactoryTreeList> {
    return this.http.get<FactoryTreeList>('/api/deviceMonitor/rtMonitor/factory/hierarchy', defaultHttpOptionsFromConfig(config));
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
    delete params['type'];
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
  public getAllFactories(factoryName?: string, config?: RequestConfig): Observable<Factory[]> {
    return this.http.get<Factory[]>(`/api/factory/findFactoryListByLoginRole`, defaultHttpOptionsFromConfig(config)).pipe(map(res => {
      if (factoryName) {
        return res.filter(item => (item.name && item.name.indexOf(factoryName) >= 0));
      } else {
        return res;
      }
    }));
  }

  // 获取所有车间
  public getAllWorkShops(factoryId?: string, tenantId?: string, workshopName?: string, config?: RequestConfig): Observable<WorkShop[]> {
    return this.http.get<WorkShop[]>(`/api/workshop/findWorkshopListByTenant?factoryId=${factoryId || ''}&tenantId=${tenantId || ''}`, defaultHttpOptionsFromConfig(config)).pipe(map(res => {
      if (workshopName) {
        return res.filter(item => (item.name && item.name.indexOf(workshopName) >= 0));
      } else {
        return res;
      }
    }));
  }

  // 获取所有产线
  public getAllProdLines(factoryId?: string, workshopId?: string, tenantId?: string, prodLineName?: string, config?: RequestConfig): Observable<ProdLine[]> {
    return this.http.get<ProdLine[]>(`/api/productionLine/findProductionLineList?factoryId=${factoryId || ''}&workshopId=${workshopId || ''}&tenantId=${tenantId || ''}`, defaultHttpOptionsFromConfig(config)).pipe(map(res => {
      if (prodLineName) {
        return res.filter(item => (item.name && item.name.indexOf(prodLineName) >= 0));
      } else {
        return res;
      }
    }));
  }

  // 自定义查询设备列表
  public getDevices(params: {
    name?: string; factoryId?: string; dictDeviceId?: string; workshopId?: string; productionLineId?: string;
    factoryName?: string; workshopName?: string; productionLineName?: string; filterGatewayFlag?: boolean;
  }, filterImg = false, config?: RequestConfig): Observable<ProdDevice[]> {
    let queryStr: string[] = [];
    if (params) {
      Object.keys(params).forEach(key => {
        queryStr.push(`${key}=${params[key]}`);
      });
    }
    return this.http.get<ProdDevice[]>(`/api/findDeviceListByCdn?${queryStr.join('&')}&filterPictureFlag=${filterImg}&filterIconFlag=${filterImg}`,
      defaultHttpOptionsFromConfig(config));
  }

  // 分配设备
  public distributeDevice(params: { deviceIdList: string[]; factoryId: string; workshopId: string; productionLineId: string; }, config?: RequestConfig) {
    return this.http.post(`/api/distributionDevice`, params, defaultHttpOptionsFromConfig(config));
  }

  // 获取工厂、车间、产线的实体属性
  public getEntityProps(entity: FactoryEntityType, config?: RequestConfig): Observable<string[]> {
    return this.http.get<string[]>(`/api/factory/getEntityAttributeList?entity=${entity}`, defaultHttpOptionsFromConfig(config));
  }

  // 根据当前登录人获取全部设备（不含网关）的在线状态
  public getOnlineStatus(config?: RequestConfig): Observable<{[id: string]: boolean}> {
    return this.http.get<{[id: string]: boolean}>(`/api/deviceMonitor/rtMonitor/device/onlineStatus/all`, defaultHttpOptionsFromConfig(config));
  }

  // 根据当前登录人获取工厂网关的整体在线状态
  public getFactoryOnlineStatus(factoryId: string = '', config?: RequestConfig): Observable<{[id: string]: boolean}> {
    return this.http.get<{ id: string; factoryStatus: boolean }[]>(
      `/api/factory/findFactoryStatusByLoginRole?factoryId=${factoryId}`, defaultHttpOptionsFromConfig(config)
    ).pipe(map(res => {
      const map = {};
      (res || []).forEach(item => {
        map[item.id] = item.factoryStatus;
      });
      return map;
    }));
  }

  // 根据当前登录人获取所有工厂下的网关id
  public getFactoryGatewayIds(config?: RequestConfig): Observable<{factoryId: string; gatewayDeviceIds: string[]}[]> {
    return this.http.get<{factoryId: string; gatewayDeviceIds: string[]}[]>(
      `/api/deviceMonitor/rtMonitor/factory/gateway/devices`, defaultHttpOptionsFromConfig(config));
  }

  // 数据权限-查询设备列表
  public getDataAuthDevices(pageLink: PageLink, filterParams: { factoryId: string; deviceName: string; }, config?: RequestConfig): Observable<PageData<DeviceDataAuth>> {
    return this.http.get<PageData<DeviceDataAuth>>(
      `/api/dict/device/switch/devices${pageLink.toQuery()}&factoryId=${filterParams.factoryId}&deviceName=${encodeURIComponent(filterParams.deviceName)}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 数据权限-查询设备属性列表
  public getDeviceProperties(pageLink: PageLink, filterParams: { deviceId: string; propertyTitle: string; }, config?: RequestConfig): Observable<PageData<DeviceAuthProp>> {
    return this.http.get<PageData<DeviceAuthProp>>(
      `/api/dict/device/switches${pageLink.toQuery()}&deviceId=${filterParams.deviceId}&q=${encodeURIComponent(filterParams.propertyTitle)}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 数据权限-设置设备属性对工厂是否可见
  public switchDevicePropFactoryVisible(props: DeviceAuthProp[], config?: RequestConfig): Observable<any> {
    return this.http.post(`/api/dict/device/switches`, props, defaultHttpOptionsFromConfig(config));
  }

}