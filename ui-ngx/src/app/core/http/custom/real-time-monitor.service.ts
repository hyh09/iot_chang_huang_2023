import { HttpClient } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { AuthService } from "@app/core/auth/auth.service";
import { RequestConfig, defaultHttpOptionsFromConfig, WINDOW } from "@app/core/public-api";
import { DeviceDetails, DeviceHistoryTableHeader, DeviceProp, RealTimeData } from "@app/shared/models/custom/device-monitor.models";
import { FactoryTreeNodeIds } from "@app/shared/models/custom/factory-mng.models";
import { PageData, PageLink } from "@app/shared/public-api";
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class RealTimeMonitorService {

  private telemetryUri: string;
  private isActive: boolean = false;
  private webSocket: WebSocket;
  private isOpened: boolean = false;
  private isOpening: boolean = false;
  private tempDeviceIdList: string[] = [];
  private leftDeviceCount: number = 0;

  constructor(
    private http: HttpClient,
    @Inject(WINDOW) private window: Window,
    private authService: AuthService
  ) {
    let port = this.window.location.port;
    if (this.window.location.protocol === 'https:') {
      if (!port) {
        port = '443';
      }
      this.telemetryUri = 'wss:';
    } else {
      if (!port) {
        port = '80';
      }
      this.telemetryUri = 'ws:';
    }
    this.telemetryUri += `//${this.window.location.hostname}:${port}/api/ws/plugins/telemetry`;
  }

  // 获取实时监控数据
  public getRealTimeData(pageLink: PageLink, params: FactoryTreeNodeIds, config?: RequestConfig): Observable<RealTimeData> {
    let queryStr: string[] = [];
    if (params) {
      Object.keys(params).forEach(key => {
        queryStr.push(`${key}=${params[key]}`);
      });
    }
    return this.http.get<RealTimeData>(`/api/deviceMonitor/rtMonitor/device${pageLink.toQuery()}&${queryStr.join('&')}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取设备详情数据
  public getDeviceDetails(id: string, config?: RequestConfig): Observable<DeviceDetails> {
    return this.http.get<DeviceDetails>(`/api/deviceMonitor/rtMonitor/device/${id}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取某条属性/参数的历史数据
  public getPropHistoryData(deviceId: string, groupPropertyName: string, config?: RequestConfig): Observable<DeviceProp[]> {
    return this.http.get<DeviceProp[]>(
      `/api/deviceMonitor/rtMonitor/device/groupProperty/history?deviceId=${deviceId}&groupPropertyName=${groupPropertyName}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取设备历史数据表头
  public getDeviceHistoryTableHeader(id: string, config?: RequestConfig): Observable<DeviceHistoryTableHeader> {
    return this.http.get<DeviceHistoryTableHeader>(`/api/deviceMonitor/rtMonitor/device/history/header/${id}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取设备历史数据列表
  public getDeviceHistoryDatas(pageLink: PageLink, filterParams: { startTime: number, endTime: number }, config?: RequestConfig): Observable<PageData<any>> {
    let queryStr: string[] = [];
    Object.keys(filterParams).forEach(key => {
      if (key === 'startTime' || key === 'endTime') {
        queryStr.push(`${key}=${filterParams[key] ? new Date(filterParams[key]).getTime() : ''}`);
      } else {
        queryStr.push(`${key}=${filterParams[key]}`);
      }
    });
    return this.http.get<PageData<any>>(
      `/api/deviceMonitor/rtMonitor/device/history${pageLink.toQuery()}&${queryStr.join('&')}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 开启订阅
  public subscribe(deviceIdList: string[], onMessage: Function) {
    this.leftDeviceCount = deviceIdList.length;
    if (!this.isActive) {
      this.isActive = true;
      if (!this.isOpened && !this.isOpening) {
        this.isOpening = true;
        if (AuthService.isJwtTokenValid()) {
          this.openSocket(AuthService.getJwtToken(),deviceIdList, onMessage);
        } else {
          this.authService.refreshJwtToken().subscribe(() => {
            this.openSocket(AuthService.getJwtToken(), deviceIdList, onMessage);
          }, () => {
            this.isOpening = false;
            this.authService.logout(true);
          });
        }
      }
    }
  }

  private openSocket(token: string, deviceIdList: string[], onMessage: Function) {
    this.webSocket = new WebSocket(`${this.telemetryUri}?token=${token}`);
    this.webSocket.onopen = () => {
      this.isOpened = true;
      this.isOpening = false;
      this.switchDevices(deviceIdList);
    }
    this.webSocket.onerror = () => {
      this.isOpening = false;
      setTimeout(() => {
        this.subscribe(deviceIdList, onMessage)
      }, 3000);
    }
    this.webSocket.onclose = () => {
      if (this.isActive) {
        this.isActive = false;
        this.isOpened = false;
        setTimeout(() => {
          this.subscribe(deviceIdList, onMessage)
        }, 3000);
      }
    }
    this.webSocket.onmessage = () => {
      if (this.leftDeviceCount > 0) {
        this.leftDeviceCount--;
      } else {
        onMessage();
      }
    }
  }

  // 切换订阅设备
  public switchDevices(deviceIdList: string[]) {
    if (this.isActive && this.isOpened) {
      if (this.tempDeviceIdList.length > 0) {
        this.webSocket.send(JSON.stringify({
          tsSubCmds: this.tempDeviceIdList.map(deviceId => ({
            entityType: "DEVICE",
            entityId: deviceId,
            scope: "LATEST_TELEMETRY",
            unsubscribe: true
          }))
        }));
        this.tempDeviceIdList = [];
        this.leftDeviceCount = 0;
      }
      if (deviceIdList && deviceIdList.length > 0) {
        this.tempDeviceIdList = deviceIdList;
        this.webSocket.send(JSON.stringify({
          tsSubCmds: deviceIdList.map(deviceId => ({
            entityType: "DEVICE",
            entityId: deviceId,
            scope: "LATEST_TELEMETRY"
          }))
        }));
      }
    }
  }

  // 关闭订阅
  public unsubscribe() {
    if (this.isActive && this.isOpened) {
      if (this.tempDeviceIdList.length > 0) {
        this.webSocket.send(JSON.stringify({
          tsSubCmds: this.tempDeviceIdList.map(deviceId => ({
            entityType: "DEVICE",
            entityId: deviceId,
            scope: "LATEST_TELEMETRY",
            unsubscribe: true
          }))
        }));
        this.tempDeviceIdList = [];
        this.leftDeviceCount = 0;
      }
      this.isActive = false;
      this.isOpened = false;
      this.isOpening = false;
      this.webSocket.close();
    }
  }

}