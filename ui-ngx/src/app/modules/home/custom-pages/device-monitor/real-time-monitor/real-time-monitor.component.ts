import { Component, Inject, OnDestroy, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { RealTimeMonitorService } from '@app/core/http/custom/real-time-monitor.service';
import { FactoryTreeComponent } from '@app/modules/home/components/factory-tree/factory-tree.component';
import { AlarmTimesListItem, DeviceItem } from '@app/shared/models/custom/device-monitor.models';
import { FactoryTreeNodeIds } from '@app/shared/models/custom/factory-mng.models';
import { PageLink } from '@app/shared/public-api';

@Component({
  selector: 'tb-real-time-monitor',
  templateUrl: './real-time-monitor.component.html',
  styleUrls: ['./real-time-monitor.component.scss'],
  providers: [
    { provide: 'RealTimeMonitorService', useClass: RealTimeMonitorService }
  ]
})
export class RealTimeMonitorComponent implements OnDestroy {

  @ViewChild('factoryTree') factoryTree: FactoryTreeComponent;

  factoryInfo: FactoryTreeNodeIds = {
    factoryId: '',
    workshopId: '',
    productionLineId: '',
    deviceId: ''
  };

  runStateData = {
    onLineDeviceCount: 0,
    offLineDeviceCount: 0
  };

  alarmTimesList: AlarmTimesListItem[] = [];

  pageLink: PageLink = new PageLink(8, 0, null, null);
  totalDevices: number = 0;
  deviceList: DeviceItem[] = [];

  constructor(
    @Inject('RealTimeMonitorService') private realTimeMonitorService: RealTimeMonitorService,
    private router: Router
  ) { }

  ngOnDestroy() {
    this.realTimeMonitorService.unsubscribe(true);
  }

  fetchData(factoryInfo?: FactoryTreeNodeIds) {
    const pageState = sessionStorage.getItem('realTimePageSate');
    if (pageState) {
      sessionStorage.removeItem('realTimePageSate');
      const { factoryInfo, page } = JSON.parse(pageState);
      Object.assign(this.factoryInfo, factoryInfo || {});
      this.pageLink.page = page;
      this.factoryTree.setKeyState(this.factoryInfo);
    } else if (factoryInfo) {
      if (JSON.stringify(factoryInfo) !== JSON.stringify(this.factoryInfo)) {
        this.pageLink.page = 0;
      }
      this.factoryInfo = factoryInfo;
    }
    this.getDevices();
    this.getOnlineOverview();
    this.getAlarmStatistics();
  }

  getDevices() {
    this.realTimeMonitorService.getRealTimeDevices(this.pageLink, this.factoryInfo).subscribe(res => {
      this.deviceList = res.data || [];
      this.totalDevices = res.totalElements || 0;
    });
  }

  getOnlineOverview() {
    this.realTimeMonitorService.getRealTimeOnlineOverview(this.factoryInfo).subscribe(res => {
      this.runStateData = {
        onLineDeviceCount: res.onLineDeviceCount || 0,
        offLineDeviceCount: res.offLineDeviceCount || 0
      }
      this.realTimeMonitorService.switchDevices(res.deviceIdList, true);
      this.realTimeMonitorService.subscribe(res.deviceIdList || [], ({ deviceId, isActive }) => {
        if (deviceId === undefined || isActive === undefined) {
          return;
        }
        const targetDevice = this.deviceList.filter(device => (device.id === deviceId));
        if (targetDevice.length > 0 && targetDevice[0].isOnLine !== !!isActive) {
          targetDevice[0].isOnLine = !!isActive;
          const { onLineDeviceCount, offLineDeviceCount } = this.runStateData;
          this.runStateData = {
            onLineDeviceCount: isActive ? (onLineDeviceCount + 1) : (onLineDeviceCount - 1),
            offLineDeviceCount: isActive ? (offLineDeviceCount - 1) : (offLineDeviceCount + 1)
          };
        }
      }, true);
    });
  }

  getAlarmStatistics() {
    this.realTimeMonitorService.getRealTimeAlarmStatistics(this.factoryInfo).subscribe(res => {
      this.alarmTimesList = res || [];
    });
  }

  goToDetail(deviceId: string) {
    sessionStorage.setItem('realTimePageSate', JSON.stringify({
      factoryInfo: this.factoryInfo,
      page: this.pageLink.page
    }));
    this.router.navigateByUrl(`deviceMonitor/realTimeMonitor/${deviceId}/details`);
  }

}
