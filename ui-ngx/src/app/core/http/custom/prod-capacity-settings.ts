import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { RequestConfig, defaultHttpOptionsFromConfig } from "@app/core/public-api";
import { ProdCapacitySettings } from "@app/shared/models/custom/device-mng.models";
import { PageLink, PageData } from "@app/shared/public-api";
import { Observable } from "rxjs";

interface FilterParams {
  factoryId?: string;
  workshopId?: string;
  productionLineId?: string;
  deviceId?: string;
  deviceName?: string;
}

@Injectable({
  providedIn: 'root'
})

export class ProdCapacitySettingsService {

  constructor(
    private http: HttpClient
  ) { }

  // 获取产能运算配置列表
  public getProdCapacitySettings(pageLink: PageLink, params: FilterParams, config?: RequestConfig): Observable<PageData<ProdCapacitySettings>> {
    let queryStr: string[] = [];
    if (params) {
      Object.keys(params).forEach(key => {
        queryStr.push(`${key}=${params[key]}`);
      });
    }
    return this.http.get<PageData<ProdCapacitySettings>>(`/api/capacityDevice/pageQuery${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 设置设备是否参与产能计算
  public setFlag(deviceId: string, deviceFlg: boolean) {
    return this.http.get(`/api/capacityDevice/updateFlgById?deviceId=${deviceId}&deviceFlg=${deviceFlg}`, { responseType: 'text' });
  }

}