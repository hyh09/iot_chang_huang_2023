import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { defaultHttpOptionsFromConfig, RequestConfig } from "../http-utils";

export interface City {
  id?: string;
  cityName: string;
  countryName: string;
  longitude: string;
  latitude: string;
  language: 'zh_cn' | 'en_us';
  postcode: string;
  displayName: string;
}

@Injectable({
  providedIn: 'root'
})

export class GeographyService {

  constructor(
    private http: HttpClient
  ) { }

  public queryCity(cityName: string, countryName = '', config?: RequestConfig): Observable<City[]> {
    return this.http.get<City[]>(`/api/geo/cities?cityName=${cityName}&countryName=${countryName}`, defaultHttpOptionsFromConfig(config));
  }

}
