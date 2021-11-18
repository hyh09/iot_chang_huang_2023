import { Component, AfterViewInit } from '@angular/core';
import { RealTimeMonitorService } from '@app/core/http/custom/real-time-monitor.service';
import { AlarmTimesListItem, DeviceItem } from '@app/shared/models/custom/device-monitor.models';
import { FactoryTreeNodeIds } from '@app/shared/models/custom/factory-mng.models';
import { PageLink } from '@app/shared/public-api';

@Component({
  selector: 'tb-real-time-monitor',
  templateUrl: './real-time-monitor.component.html',
  styleUrls: ['./real-time-monitor.component.scss']
})
export class RealTimeMonitorComponent implements AfterViewInit {

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
    private realTimeMonitorService: RealTimeMonitorService
  ) { }

  ngAfterViewInit() {
    this.fetchData()
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
    });
  }

}
