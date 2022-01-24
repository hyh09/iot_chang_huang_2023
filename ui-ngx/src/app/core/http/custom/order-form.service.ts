import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { OrderCapacity, OrderForm } from "@app/shared/models/custom/order-form-mng.models";
import { ChecksumAlgorithm } from "@app/shared/models/ota-package.models";
import { PageLink, PageData, HasUUID } from "@app/shared/public-api";
import { TranslateService } from "@ngx-translate/core";
import { Observable } from "rxjs";
import { tap } from "rxjs/operators";
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';

interface FetchListFilter {
  orderNo: string;
  factoryName: string;
}

@Injectable({
  providedIn: 'root'
})
export class OrderFormService {

  constructor(
    private http: HttpClient,
    private translate: TranslateService
  ) { }

  // 获取订单列表
  public getOrders(pageLink: PageLink, filterParams: FetchListFilter, config?: RequestConfig): Observable<PageData<OrderForm>> {
    let queryStr: string[] = [];
    Object.keys(filterParams).forEach(key => {
      queryStr.push(`${key}=${filterParams[key]}`);
    });
    return this.http.get<PageData<OrderForm>>(`/api/orders${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取订单产量列表
  public getOrderCapacities(pageLink: PageLink, filterParams: FetchListFilter, config?: RequestConfig): Observable<PageData<OrderCapacity>> {
    let queryStr: string[] = [];
    Object.keys(filterParams).forEach(key => {
      queryStr.push(`${key}=${filterParams[key]}`);
    });
    return this.http.get<PageData<OrderCapacity>>(`/api/order/capacityMonitor${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取订单详情
  public getOrderForm(id: HasUUID, config?: RequestConfig): Observable<OrderForm> {
    return this.http.get<OrderForm>(`/api/order/${id}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取订单产量详情
  public getOrderCapacity(id: HasUUID, config?: RequestConfig): Observable<OrderForm> {
    return this.http.get<OrderForm>(`/api/order/${id}/capacityMonitor`, defaultHttpOptionsFromConfig(config));
  }

  // 获取可用的订单号
  public getAvailableOrderNo(): Observable<string> {
    return this.http.get(`/api/order/availableCode`, { responseType: 'text' });
  }

  // 保存订单详情
  public saveOrderForm(orderForm: OrderForm, config?: RequestConfig): Observable<OrderForm> {
    return this.http.post<OrderForm>(`/api/order`, orderForm, defaultHttpOptionsFromConfig(config));
  }

  // 删除订单
  public deleteOrder(id: HasUUID, config?: RequestConfig) {
    return this.http.delete(`/api/order/${id}`, defaultHttpOptionsFromConfig(config));
  }

  // 下载订单导入模板
  public downloadOrderTemplate() {
    return this.http.get(`/api/order/template`, { responseType: 'arraybuffer' }).pipe(tap(res => {
      var blob = new Blob([res], {type: 'application/vnd.ms-excel;'});
      var link = document.createElement('a');
      var href = window.URL.createObjectURL(blob);
      link.href = href;
      link.download = this.translate.instant('order.import-template-name');
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(href);
    }));
  }

  // 导入订单
  public importOrder(file: File, checksum?: string, checksumAlgorithmStr?: ChecksumAlgorithm): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    checksum && formData.append('checksum', checksum);
    checksumAlgorithmStr && formData.append('checksumAlgorithm', checksumAlgorithmStr);
    return this.http.post(`/api/order/import`, formData, { responseType: 'text' });
  }

}