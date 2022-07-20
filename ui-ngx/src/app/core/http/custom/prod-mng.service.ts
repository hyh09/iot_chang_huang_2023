import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { RequestConfig, defaultHttpOptionsFromConfig } from "@app/core/public-api";
import { ProdCalendar, ProdMng } from "@app/shared/models/custom/device-mng.models";
import { PageLink, PageData, HasUUID } from "@app/shared/public-api";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { environment } from '@env/environment';

@Injectable({
  providedIn: 'root'
})
export class ProdMngService {

  constructor(
    private http: HttpClient
  ) { }

  // 获取生产管理列表
  public getProdMngList(pageLink: PageLink, filterParams: { factoryName: string; deviceName: string; }, config?: RequestConfig): Observable<PageData<ProdMng>> {
    return this.http.get<PageData<ProdMng>>(
      `/api/productionCalender/getPageList${pageLink.toQuery()}&factoryName=${filterParams.factoryName}&deviceName=${filterParams.deviceName}&factoryId=${environment.factoryId || ''}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 分页获取设备生产日历列表
  public getProdCalendarList(pageLink: PageLink, deviceId: string, config?: RequestConfig): Observable<PageData<ProdCalendar>> {
    return this.http.get<PageData<ProdCalendar>>(`/api/productionCalender/getHistoryPageByDeviceId${pageLink.toQuery()}&deviceId=${deviceId}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取设备所有的生产日历
  public getAllProdCalendars(deviceId: string, config?: RequestConfig): Observable<ProdCalendar[]> {
    return this.http.get<ProdCalendar[]>(`/api/productionCalender/getHistoryByDeviceId?deviceId=${deviceId}`, defaultHttpOptionsFromConfig(config));
  }

  // 新增或编辑生产日历
  public saveProdCalendar(prodCalendar: ProdCalendar, config?: RequestConfig): Observable<ProdCalendar> {
    return this.http.post<ProdCalendar>('/api/productionCalender/save', prodCalendar, defaultHttpOptionsFromConfig(config)).pipe(map(() => {
      return prodCalendar;
    }));
  }

  // 查询生产日历详情
  public getProdCalendar(id: HasUUID, config?: RequestConfig): Observable<ProdCalendar> {
    return this.http.get<ProdCalendar>(`/api/productionCalender/${id}`, defaultHttpOptionsFromConfig(config));
  }

  // 删除生产日历
  public deleteProdCalendar(id: HasUUID, config?: RequestConfig) {
    return this.http.delete(`/api/productionCalender/deleteById/${id}`, defaultHttpOptionsFromConfig(config));
  }
  
}