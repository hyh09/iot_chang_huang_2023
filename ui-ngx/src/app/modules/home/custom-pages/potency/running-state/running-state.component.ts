import { Component } from '@angular/core';
import { PotencyService } from '@app/core/http/custom/potency.service';
import { DeviceProp } from '@app/shared/models/custom/device-monitor.models';
import { FactoryTreeNodeIds } from '@app/shared/models/custom/factory-mng.models';
import { RunningState } from '@app/shared/models/custom/potency.models';
import { PageLink } from '@app/shared/public-api';

@Component({
  selector: 'tb-running-state',
  templateUrl: './running-state.component.html',
  styleUrls: ['./running-state.component.scss']
})
export class RunningStateComponent {

  deviceId: string = '';
  selectedProps: string[] = [];
  properties: DeviceProp[] = [];
  propertyMap: { [key: string]: DeviceProp; } = {};
  runningStateData: { [key: string]: RunningState } = {};
  displayedProps: string[] = [];
  pageLink: PageLink = new PageLink(2, 0, null, null);
  rangeTime: Date[] = [];

  constructor(private potencyService: PotencyService) {
    const now = new Date();
    this.rangeTime = [new Date(now), new Date(now.setHours(now.getHours() - 1))];
  }

  fetchData(factoryInfo?: FactoryTreeNodeIds) {
    let deviceChanged = false;
    if (factoryInfo) {
      deviceChanged = this.deviceId !== factoryInfo.deviceId;
      this.deviceId = factoryInfo.deviceId;
    }
    if (deviceChanged) {
      this.pageLink.page = 0;
      this.displayedProps = [];
      this.potencyService.getDeviceProps(this.deviceId).subscribe(properties => {
        this.properties = properties || [];
        this.selectedProps = this.properties.map(prop => (prop.name || prop.chartId));
        this.displayedProps = this.selectedProps.slice(0, 2);
        this.propertyMap = {};
        this.properties.forEach(prop => {
          this.propertyMap[prop.name || prop.chartId] = prop;
        });
        this.getRunningStateData();
      });
    } else {
      this.getRunningStateData();
    }
  }

  private getRunningStateData() {
    this.runningStateData = {};
    let startTime, endTime;
    if (this.rangeTime && this.rangeTime.length === 2) {
      startTime = this.rangeTime[0].getTime();
      endTime = this.rangeTime[1].getTime();
    }
    this.potencyService.getDeviceRunningState({
      deviceId: this.deviceId,
      attributeParameterList: this.displayedProps.map(prop => (this.propertyMap[prop])),
      startTime,
      endTime
    }).subscribe(res => {
      (res || []).forEach(item => {
        this.runningStateData[item.chartId || item.keyName] = item;
      });
    });
  }

  onSelectedPropsChange() {
    this.pageLink.page = 0;
    this.displayedProps = this.selectedProps.slice(0, 2);
    this.fetchData();
  }

  onPageChange(pageIndex: number) {
    this.pageLink.page = pageIndex;
    this.displayedProps = this.selectedProps.slice(pageIndex * 2, pageIndex * 2 + 2);
    this.fetchData();
  }

}
