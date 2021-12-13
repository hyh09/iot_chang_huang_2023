import { HttpClient } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { AuthService } from "@app/core/auth/auth.service";
import { RequestConfig, defaultHttpOptionsFromConfig, WINDOW } from "@app/core/public-api";
import { DeviceDetails, DevicePropHistory, RealTimeData } from "@app/shared/models/custom/device-monitor.models";
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
  public getPropHistoryData(deviceId: string, groupPropertyName: string, config?: RequestConfig): Observable<DevicePropHistory> {
    return this.http.get<DevicePropHistory>(
      `/api/deviceMonitor/rtMonitor/device/groupProperty/history?deviceId=${deviceId}&groupPropertyName=${groupPropertyName}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 获取设备历史数据表头
  public getDeviceHistoryTableHeader(id: string, config?: RequestConfig): Observable<{ name: string; title: string }[]> {
    return this.http.get<{ name: string; title: string }[]>(`/api/deviceMonitor/rtMonitor/device/history/header?deviceId=${id}`, defaultHttpOptionsFromConfig(config));
  }

  // 获取设备历史数据列表
  public getDeviceHistoryDatas(pageLink: PageLink, deviceId: string, config?: RequestConfig): Observable<PageData<object>> {
    return this.http.get<PageData<object>>(
      `/api/deviceMonitor/rtMonitor/device/history${pageLink.toQuery()}&deviceId=${deviceId}`,
      defaultHttpOptionsFromConfig(config)
    );
  }

  // 开启订阅
  public subscribe(deviceIdList: string[], onMessage: Function, listenDeviceActive = false) {
    this.leftDeviceCount = deviceIdList.length;
    if (!this.isActive) {
      this.isActive = true;
      if (!this.isOpened && !this.isOpening) {
        this.isOpening = true;
        if (AuthService.isJwtTokenValid()) {
          this.openSocket(AuthService.getJwtToken(),deviceIdList, onMessage, listenDeviceActive);
        } else {
          this.authService.refreshJwtToken().subscribe(() => {
            this.openSocket(AuthService.getJwtToken(), deviceIdList, onMessage, listenDeviceActive);
          }, () => {
            this.isOpening = false;
            this.authService.logout(true);
          });
        }
      }
    }
  }

  private openSocket(token: string, deviceIdList: string[], onMessage: Function, listenDeviceActive = false) {
    this.webSocket = new WebSocket(`${this.telemetryUri}?token=${token}`);
    this.webSocket.onopen = () => {
      this.isOpened = true;
      this.isOpening = false;
      setTimeout(() => {
        this.switchDevices(deviceIdList, listenDeviceActive);
      }, 1000);
    }
    this.webSocket.onerror = () => {
      this.isOpening = false;
      setTimeout(() => {
        this.subscribe(deviceIdList, onMessage, listenDeviceActive);
      }, 3000);
    }
    this.webSocket.onclose = () => {
      if (this.isActive) {
        this.isActive = false;
        this.isOpened = false;
        setTimeout(() => {
          this.subscribe(deviceIdList, onMessage, listenDeviceActive);
        }, 3000);
      }
    }
    this.webSocket.onmessage = res => {
      if (this.leftDeviceCount > 0) {
        this.leftDeviceCount--;
      } else if (listenDeviceActive) {
        const _res = JSON.parse(res.data);
        if (_res.data && _res.latestValues) {
          const latestActiveTime = _res.latestValues.active;
          const latestDeviceTime = _res.latestValues.attrDeviceId;
          if (_res.data.active && _res.data.attrDeviceId) {
            const latestActiveInfo = (_res.data.active as Array<string[]>).filter(item => (item[0] === latestActiveTime));
            const latestDeviceInfo = (_res.data.attrDeviceId as Array<string[]>).filter(item => (item[0] === latestDeviceTime));
            const isActive = latestActiveInfo[0] && latestActiveInfo[0][1] === 'true';
            const deviceId = latestDeviceInfo[0] ? (latestDeviceInfo[0][1] || '') : '';
            onMessage({ deviceId, isActive });
          } else {
            onMessage({});
          }
        } else {
          onMessage({});
        }
      } else {
        onMessage();
      }
    }
  }

  // 切换订阅设备
  public switchDevices(deviceIdList: string[], listenDeviceActive = false) {
    if (this.isActive && this.isOpened) {
      if (this.tempDeviceIdList.length > 0) {
        const msg: { attrSubCmds?: any; tsSubCmds?: any } = {};
        const content: any[] = [{
          cmdId: 0,
          entityType: 'DEVICE',
          entityId: null,
          scope: listenDeviceActive ? 'SERVER_SCOPE' : 'LATEST_TELEMETRY',
          unsubscribe: true
        }];
        if (listenDeviceActive) {
          content.forEach(item => (item.keys = 'active'));
        }
        listenDeviceActive ? (msg.attrSubCmds = content) : (msg.tsSubCmds = content);
        this.webSocket.send(JSON.stringify(msg));
        this.tempDeviceIdList = [];
        this.leftDeviceCount = 0;
      }
      if (deviceIdList && deviceIdList.length > 0) {
        this.tempDeviceIdList = deviceIdList;
        const msg: { attrSubCmds?: any; tsSubCmds?: any } = {};
        const content: any[] = this.tempDeviceIdList.map((deviceId, index) => ({
          cmdId: index,
          entityType: "DEVICE",
          entityId: deviceId,
          scope: listenDeviceActive ? 'SERVER_SCOPE' : 'LATEST_TELEMETRY'
        }));
        if (listenDeviceActive) {
          content.forEach(item => (item.keys = 'active'));
        }
        listenDeviceActive ? (msg.attrSubCmds = content) : (msg.tsSubCmds = content);
        this.webSocket.send(JSON.stringify(msg));
      }
    }
  }

  // 关闭订阅
  public unsubscribe(listenDeviceActive = false) {
    if (this.isActive && this.isOpened) {
      if (this.tempDeviceIdList.length > 0) {
        this.webSocket.send(JSON.stringify({
          tsSubCmds: [{
            cmdId: 0,
            entityType: 'DEVICE',
            entityId: null,
            scope: listenDeviceActive ? 'SERVER_SCOPE' : 'LATEST_TELEMETRY',
            unsubscribe: true
          }]
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