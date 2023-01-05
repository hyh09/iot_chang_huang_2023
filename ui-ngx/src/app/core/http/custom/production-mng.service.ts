import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ProdSchedual, ProdReport, ProdMonitor } from "@app/shared/models/custom/production-mng.models";
import { PageLink, PageData } from "@app/shared/public-api";
import { Observable } from "rxjs";
import { RequestConfig, defaultHttpOptionsFromConfig } from "../http-utils";

// 查询接口请求参数接口
interface FilterParams {
  startTime?: number | '';
  endTime?: number | '';
  // TODO 班组名称 string【非必填】
  // TODO 当前工序 string 【非必填】
  // TODO 生产单号 string 【非必填】
}

@Injectable({
  providedIn: 'root'
})
export class ProductionMngService {

  constructor(
    private http: HttpClient
  ) { }

  // 获取生产排班列表
  public getProdSchedualList(pageLink: PageLink, filterParams: FilterParams, config?: RequestConfig): Observable<PageData<ProdSchedual>> {
    let queryStr: string[] = [];
    Object.keys(filterParams).forEach(key => {
      queryStr.push(`${key}=${filterParams[key]}`);
    });
    return this.http.get<PageData<ProdSchedual>>(`${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取生产报工列表
  public getProdReportList(pageLink: PageLink, filterParams: FilterParams, config?: RequestConfig): Observable<PageData<ProdReport>> {
    let queryStr: string[] = [];
    Object.keys(filterParams).forEach(key => {
      queryStr.push(`${key}=${filterParams[key]}`);
    });
    return this.http.get<PageData<ProdReport>>(`${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取生产监控列表
  public getProdMonitorList(pageLink: PageLink, filterParams: FilterParams, config?: RequestConfig): Observable<PageData<ProdMonitor>> {
    let queryStr: string[] = [];
    Object.keys(filterParams).forEach(key => {
      queryStr.push(`${key}=${filterParams[key]}`);
    });
    return this.http.get<PageData<ProdMonitor>>(`${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

}