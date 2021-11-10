import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { defaultHttpOptionsFromConfig, RequestConfig } from "@app/core/public-api";
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

  // 
  // public getFactories(params: FetchListFilter, config?: RequestConfig): Observable<Array<Menu>> {
  //   return this.http.get<Array<Menu>>(`/api/menu/getOneLevel?menuType=${menuType}`, defaultHttpOptionsFromConfig(config));
  // }

}