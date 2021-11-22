import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { RealTimeMonitorService } from '@app/core/http/custom/real-time-monitor.service';
import { AlarmTimesListItem, DeviceItem } from '@app/shared/models/custom/device-monitor.models';
import { FactoryTreeNodeIds } from '@app/shared/models/custom/factory-mng.models';
import { PageLink } from '@app/shared/public-api';

@Component({
  selector: 'tb-real-time-monitor',
  templateUrl: './real-time-monitor.component.html',
  styleUrls: ['./real-time-monitor.component.scss']
})
export class RealTimeMonitorComponent implements OnDestroy {

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
    private realTimeMonitorService: RealTimeMonitorService,
    private router: Router
  ) { }

  ngOnDestroy() {
    this.realTimeMonitorService.unsubscribe();
  }

  fetchData(factoryInfo?: FactoryTreeNodeIds) {
    if (factoryInfo) {
      this.factoryInfo = factoryInfo;
    }
    this.realTimeMonitorService.getRealTimeData(this.pageLink, this.factoryInfo).subscribe(res => {
      this.totalDevices = res.allDeviceCount || 0;
      this.deviceList = res.devicePageData.data || [];
      this.runStateData = {
        onLineDeviceCount: res.onLineDeviceCount || 0,
        offLineDeviceCount: res.offLineDeviceCount || 0
      }
      this.alarmTimesList = res.alarmTimesList || [];
      this.realTimeMonitorService.switchDevices(res.deviceIdList);
      this.realTimeMonitorService.subscribe(res.deviceIdList || [], () => {
        this.fetchData();
      });
    });
  }

  goToDetail(deviceId: string) {
    this.router.navigateByUrl(`deviceMonitor/realTimeMonitor/${deviceId}/details`);
  }

}
