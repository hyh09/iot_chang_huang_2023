import { Component, OnInit } from '@angular/core';
import { PotencyService } from '@app/core/http/custom/potency.service';
import { DeviceProp } from '@app/shared/models/custom/device-monitor.models';
import { FactoryTreeNodeIds } from '@app/shared/models/custom/factory-mng.models';
import { RunningState } from '@app/shared/models/custom/potency.models';
import { Timewindow, historyInterval, DAY, TimeRange, HistoryWindowType, calculateIntervalStartEndTime, PageLink } from '@app/shared/public-api';

@Component({
  selector: 'tb-running-state',
  templateUrl: './running-state.component.html',
  styleUrls: ['./running-state.component.scss']
})
export class RunningStateComponent {

  deviceId: string = '';
  timewindow: Timewindow = historyInterval(DAY);
  selectedProps: string[] = [];
  properties: DeviceProp[] = [];
  propertyMap: { [key: string]: DeviceProp; } = {};
  runningStateData: RunningState = {};
  displayedProps: string[] = [];
  pageLink: PageLink = new PageLink(2, 0, null, null);

  constructor(private potencyService: PotencyService) { }

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
      });
    }
    this.runningStateData = {};
    this.potencyService.getDeviceRunningState({ deviceId: this.deviceId, ...this.generateTimeRange() }).subscribe(res => {
      this.runningStateData = res || {};
    });
  }

  generateTimeRange(): TimeRange {
    const timeRange: TimeRange = { startTime: null, endTime: null };
    if (this.timewindow.history.historyType === HistoryWindowType.LAST_INTERVAL) {
      const currentTime = Date.now();
      timeRange.startTime = currentTime - this.timewindow.history.timewindowMs;
      timeRange.endTime = currentTime;
    } else if (this.timewindow.history.historyType === HistoryWindowType.INTERVAL) {
      const startEndTime = calculateIntervalStartEndTime(this.timewindow.history.quickInterval);
      timeRange.startTime = startEndTime[0];
      timeRange.endTime = startEndTime[1];
    } else {
      timeRange.startTime = this.timewindow.history.fixedTimewindow.startTimeMs;
      timeRange.endTime = this.timewindow.history.fixedTimewindow.endTimeMs;
    }
    return timeRange;
  }

  onSelectedPropsChange() {
    this.pageLink.page = 0;
    this.displayedProps = this.selectedProps.slice(0, 2);
  }

  onPageChange(pageIndex: number) {
    this.pageLink.page = pageIndex;
    this.displayedProps = this.selectedProps.slice(pageIndex * 2, pageIndex * 2 + 2);
  }

}
