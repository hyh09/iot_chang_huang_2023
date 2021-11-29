import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RealTimeMonitorService } from '@app/core/http/custom/real-time-monitor.service';
import { DeviceComp, DeviceCompTreeNode } from '@app/shared/models/custom/device-mng.models';
import { AlarmTimesListItem, DeviceBaseInfo, DeviceProp, DevicePropGroup } from '@app/shared/models/custom/device-monitor.models';

@Component({
  selector: 'tb-device-details',
  templateUrl: './device-details.component.html',
  styleUrls: ['./device-details.component.scss']
})
export class DeviceDetailsComponent implements OnInit, OnDestroy {

  private deviceId: string = '';
  private deviceName: string = '';
  baseInfo: DeviceBaseInfo = {}; // 基本信息
  currPropName: string = ''; // 当前选中的参数名称
  currPropTitle: string = ''; // 当前选中的参数描述
  propHistoryData: DeviceProp[] = []; // 参数历史数据
  alarmTimesList: AlarmTimesListItem[] = []; // 预警统计
  deviceData: DevicePropGroup[] = []; // 设备参数
  devcieComp: DeviceComp[] = []; // 设备部件
  mapOfExpandedComp: { [code: string]: DeviceCompTreeNode[] } = {};
  showRealTimeChart: boolean;

  constructor(
    private realTimeMonitorService: RealTimeMonitorService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.deviceId = this.route.snapshot.params.deviceId;
  }

  ngOnInit() {
    this.fetchData();
  }

  ngOnDestroy() {
    this.realTimeMonitorService.unsubscribe();
  }

  fetchData(isMqtt?: boolean) {
    if (this.deviceId) {
      this.realTimeMonitorService.getDeviceDetails(this.deviceId).subscribe(res => {
        const { picture, name, factoryName, workShopName, productionLineName } = res;
        this.baseInfo = { picture, name, factoryName, workShopName, productionLineName };
        this.deviceName = name;
        this.alarmTimesList = res.alarmTimesList || [];
        this.deviceData = res.resultList || [];
        this.deviceData.push(res.resultUngrouped || { groupPropertyList: [] });
        this.devcieComp = res.componentList || [];
        this.setMapOfExpandedComp();
        if (isMqtt) {
          this.fetchPropHistoryData(this.currPropName, this.currPropTitle, () => { this.subscribe(); });
        } else if (this.deviceData.length > 0 && this.deviceData[0].groupPropertyList.length > 0) {
          const { name, title } = this.deviceData[0].groupPropertyList[0];
          this.fetchPropHistoryData(name, title, () => { this.subscribe(); });
        } else {
          this.currPropName = '';
          this.currPropTitle = '';
          this.subscribe();
        }
      });
    }
  }

  fetchPropHistoryData(propName: string, propTitle: string, callFn?: Function) {
    this.currPropName = propName;
    this.currPropTitle = propTitle;
    this.realTimeMonitorService.getPropHistoryData(this.deviceId, propName).subscribe(propData => {
      if (propData.isShowChart) {
        this.propHistoryData = propData.propertyVOList || [];
        callFn && callFn();
        this.showRealTimeChart = true;
      } else {
        this.showRealTimeChart = false;
      }
    });
  }

  setMapOfExpandedComp() {
    const map: { [code: string]: DeviceCompTreeNode[] } = {};
    this.devcieComp.forEach((item: DeviceComp) => {
      map[item.code] = this.convertTreeToList(item);
    });
    this.mapOfExpandedComp = map;
  }

  convertTreeToList(root: DeviceComp): DeviceCompTreeNode[] {
    const stack: DeviceCompTreeNode[] = [];
    const array: DeviceCompTreeNode[] = [];
    const hashMap = {};
    stack.push({ ...root, level: 0, expand: true });
    while (stack.length !== 0) {
      const node = stack.pop()!;
      if (!hashMap[node.code]) {
        hashMap[node.code] = true;
        array.push(node);
      }
      if (node.componentList) {
        for (let i = node.componentList.length - 1; i >= 0; i--) {
          stack.push({
            ...node.componentList[i],
            level: node.level! + 1,
            expand: true,
            parent: node
          });
        }
      }
    }

    return array;
  }

  collapse(array: Array<DeviceCompTreeNode>, data: DeviceCompTreeNode, $event: boolean) {
    if (data.componentList) {
      data.componentList.forEach(d => {
        const target = array.find(a => a.code === d.code)!;
        target.expand = $event;
        this.collapse(array, target, $event);
      });
    } else {
      return;
    }
  }

  subscribe() {
    this.realTimeMonitorService.subscribe([this.deviceId], () => {
      this.fetchData(true);
    });
  }

  gotoHistory() {
    this.router.navigate([`history`], {
      relativeTo: this.route,
      queryParams: {
        deviceName: this.deviceName
      }
    });
  }

}
