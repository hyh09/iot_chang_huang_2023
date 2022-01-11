import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { OrderForm } from "@app/shared/models/custom/order-form-mng.models";
import { PageLink, PageData, HasUUID } from "@app/shared/public-api";
import { Observable } from "rxjs";
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
    private http: HttpClient
  ) { }

  // 获取订单列表
  public getOrders(pageLink: PageLink, filterParams: FetchListFilter, config?: RequestConfig): Observable<PageData<OrderForm>> {
    let queryStr: string[] = [];
    Object.keys(filterParams).forEach(key => {
      queryStr.push(`${key}=${filterParams[key]}`);
    });
    return this.http.get<PageData<OrderForm>>(`/api/orders${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取订单详情
  public getOrderForm(id: HasUUID, config?: RequestConfig): Observable<OrderForm> {
    return this.http.get<OrderForm>(`/api/order/${id}`, defaultHttpOptionsFromConfig(config));
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

}