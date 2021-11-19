import { HttpClient } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { AuthService } from "@app/core/auth/auth.service";
import { RequestConfig, defaultHttpOptionsFromConfig, WINDOW } from "@app/core/public-api";
import { RealTimeData } from "@app/shared/models/custom/device-monitor.models";
import { FactoryTreeNodeIds } from "@app/shared/models/custom/factory-mng.models";
import { PageLink } from "@app/shared/public-api";
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

  // 开启订阅
  public subscribe(deviceIdList: string[], onMessage: Function) {
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
    this.tempDeviceIdList = deviceIdList;
    this.webSocket = new WebSocket(`${this.telemetryUri}?token=${token}`);
    this.webSocket.onopen = () => {
      this.webSocket.send(JSON.stringify({
        tsSubCmds: deviceIdList.map(deviceId => ({
          entityType: "DEVICE",
          entityId: deviceId,
          scope: "LATEST_TELEMETRY"
        }))
      }));
      this.isOpened = true;
      this.isOpening = false;
    }
    this.webSocket.onerror = () => {
      this.isOpening = false;
      setTimeout(() => {
        this.subscribe(deviceIdList, onMessage)
      }, 3000);
    }
    this.webSocket.onmessage = () => {
      onMessage();
    }
  }

  // 取消订阅设备
  public unSubscribeDevices() {
    if (this.isActive && this.isOpened && this.tempDeviceIdList.length > 0) {
      this.webSocket.send(JSON.stringify({
        tsSubCmds: this.tempDeviceIdList.map(deviceId => ({
          entityType: "DEVICE",
          entityId: deviceId,
          scope: "LATEST_TELEMETRY",
          unsubscribe: true
        }))
      }));
      this.tempDeviceIdList = [];
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
      }
      this.webSocket.close();
      this.isActive = false;
      this.isOpened = false;
      this.isOpening = false;
    }
  }

}