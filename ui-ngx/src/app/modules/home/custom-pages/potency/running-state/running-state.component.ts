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
  runningStateData: RunningState = {};
  displayedProps: string[] = [];
  pageLink: PageLink = new PageLink(2, 0, null, null);
  startTime: Date;
  endTime: Date;

  constructor(private potencyService: PotencyService) {
    const now = new Date();
    this.endTime = new Date(now);
    this.startTime = new Date(now.setHours(now.getHours() - 1));
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
        this.selectedProps = this.properties.map(prop => (prop.name));
        this.displayedProps = this.selectedProps.slice(0, 2);
        this.propertyMap = {};
        this.properties.forEach(prop => {
          this.propertyMap[prop.name] = prop;
        });
        this.getRunningStateData();
      });
    } else {
      this.getRunningStateData();
    }
  }

  private getRunningStateData() {
    this.runningStateData = {};
    this.potencyService.getDeviceRunningState({
      deviceId: this.deviceId,
      keyNames: this.displayedProps,
      startTime: this.startTime.getTime(),
      endTime: this.endTime.getTime()
    }).subscribe(res => {
      this.runningStateData = res || {};
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

  onTimeChange() {
    this.endTime.setSeconds(0);
    this.endTime.setMilliseconds(0);
    const date = new Date(this.endTime);
    this.startTime = new Date(date.setHours(this.endTime.getHours() - 1));
    this.fetchData();
  }

  disabledHours(): number[] {
    const arr = [0];
    const currHour = new Date().getHours();
    for (let i = 1; i <= 23; i++) {
      if (i > currHour) {
        arr.push(i);
      }
    }
    return arr;
  }

  disabledMinutes(hour: number): number[] {
    const arr = [];
    const currHour = new Date().getHours();
    if (hour === currHour) {
      const currMinute = new Date().getMinutes();
      for (let i = 0; i <= 59; i++) {
        if (i > currMinute) {
          arr.push(i);
        }
      }
    }
    return arr;
  }

}
