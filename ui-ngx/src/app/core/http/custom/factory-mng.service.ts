import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { defaultHttpOptionsFromConfig, RequestConfig } from "@app/core/public-api";
import { FactoryMngList } from "@app/shared/models/custom/factory-mng.models";
import { Observable } from "rxjs";

export interface FetchListFilter {
  name: string,
  workshopName: string,
  productionlineName: string,
  deviceName: string
}

@Injectable({
  providedIn: 'root'
})

export class FactoryMngService {

  constructor(
    private http: HttpClient
  ) { }

  // 条件查询工厂列表（含车间、产线、设备）
  public getFactoryList(params: FetchListFilter, config?: RequestConfig): Observable<FactoryMngList> {
    let queryStr: string[] = [];
    Object.keys(params).forEach(key => {
      queryStr.push(`${key}=${params[key]}`);
    });
    return this.http.get<FactoryMngList>(`/api/factory/findFactoryListBuyCdn?${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

}