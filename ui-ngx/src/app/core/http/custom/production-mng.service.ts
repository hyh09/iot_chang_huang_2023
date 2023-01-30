import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ProdSchedual, ProdReport, ProdMonitor } from "@app/shared/models/custom/production-mng.models";
import { PageLink, PageData } from "@app/shared/public-api";
import { Observable } from "rxjs";
import { RequestConfig, defaultHttpOptionsFromConfig } from "../http-utils";
import { map } from 'rxjs/operators';
import { tap } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';

// 查询生产排班列表接口请求参数接口
interface FilterParams {
  sWorkerGroupName?: string;
  sWorkingProcedureName?: string;
  tTrackTimeStart?: number | string,
  tTrackTimeEnd?: number | string,
}

// 查询生产报工列表接口请求参数接口
interface ProdReportFilterParams {
  sOrderNo?: string,
  tFactStartTime?: number | string,
  tFactEndTime?: number | string,
}

// 查询生产监控查询接口
interface ProdMonitorFilterParams {
  sWorkingProcedureName?: string
}

@Injectable({
  providedIn: 'root'
})
export class ProductionMngService {

  constructor(
    private http: HttpClient,
    private translate: TranslateService
  ) { }

  // 获取生产排班列表
  public getProdSchedualList(pageLink: PageLink, filterParams: FilterParams, config?: RequestConfig): Observable<PageData<ProdSchedual>> {
    let queryStr: string[] = [];
    Object.keys(filterParams).forEach(key => {
      queryStr.push(`${key}=${filterParams[key]}`);
    });
    return this.http.get<PageData<ProdSchedual>>(`/api/mes/production/findPlanList${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 共用导出
  public exportPort(title, dataList) {
    let formData = {
      title: title,
      dataList: dataList
    }
    return this.http.post(`/api/excel/export`, formData, { responseType: 'arraybuffer' }).pipe(tap(res => {
      console.log(res)
      var blob = new Blob([res], { type: 'application/vnd.ms-excel;' });
      var link = document.createElement('a');
      var href = window.URL.createObjectURL(blob);
      link.href = href;
      link.download = this.translate.instant(title === '生产管理' ? 'production-mng.prod-schedual' : title === '生产监控' ? 'production-mng.prod-monitor' : title === '生产报工' ? 'production-mng.prod-report' : title === '流程卡进度' ? 'order.process-card-progress' : title === '订单进度' ? 'order.order-progress' : '');
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(href);
    }));
  }

  // 获取生产报工列表
  public getProdReportList(pageLink: PageLink, filterParams: ProdReportFilterParams, config?: RequestConfig): Observable<PageData<ProdReport>> {
    let queryStr: string[] = [];
    Object.keys(filterParams).forEach(key => {
      queryStr.push(`${key}=${filterParams[key]}`);
    });
    return this.http.get<PageData<ProdReport>>(`/api/mes/production/findWorkList${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取生产监控列表
  public getProdMonitorList(pageLink: PageLink, filterParams?: ProdMonitorFilterParams, config?: RequestConfig): Observable<PageData<ProdMonitor>> {
    let queryStr: string[] = [];
    Object.keys(filterParams).forEach(key => {
      queryStr.push(`${key}=${encodeURIComponent(filterParams[key])}`);
    });
    return this.http.get<PageData<ProdMonitor>>(`/api/mes/production/findMonitorList${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取合计数量
  public getTotalQuantity(filterParams?: any): Observable<PageData<ProdMonitor>> {
    let queryStr: string[] = [];
    Object.keys(filterParams).forEach(key => {
      queryStr.push(`${key}=${encodeURIComponent(filterParams[key])}`);
    });
    return this.http.get<PageData<ProdMonitor>>(`/api/mes/production/findMonitorList?${queryStr.join('&')}`);
  }

  // 获取工序
  public getProcedureName(config?: RequestConfig): Observable<Array<string>> {
    return this.http.get<any>(`/api/mes/production/findWorkingProcedureNameList`, defaultHttpOptionsFromConfig(config)).pipe(map(result => {
      return result;
    }));
  }

}