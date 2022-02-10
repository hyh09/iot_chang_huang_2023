import { Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RealTimeMonitorService } from '@app/core/http/custom/real-time-monitor.service';
import { DeviceComp, DeviceCompTreeNode } from '@app/shared/models/custom/device-mng.models';
import { AlarmTimesListItem, DeviceBaseInfo, DeviceProp, DevicePropGroup, DevicePropHistory } from '@app/shared/models/custom/device-monitor.models';
import { PropDataChartComponent } from './prop-data-chart.component';

@Component({
  selector: 'tb-device-details',
  templateUrl: './device-details.component.html',
  styleUrls: ['./device-details.component.scss'],
  providers: [
    { provide: 'RealTimeMonitorService', useClass: RealTimeMonitorService }
  ]
})
export class DeviceDetailsComponent implements OnInit, OnDestroy {

  @ViewChild('propDataChart') propDataChart: PropDataChartComponent;

  private deviceId: string = '';
  private deviceName: string = '';
  baseInfo: DeviceBaseInfo = {}; // 基本信息
  currPropName: string = ''; // 当前选中的参数名称
  relatedPropName: string[] = []; // 当前选中的参数及其关联的其它参数名称
  propHistoryData: DevicePropHistory = {}; // 参数历史数据
  alarmTimesList: AlarmTimesListItem[] = []; // 预警统计
  deviceData: DevicePropGroup[] = []; // 设备参数
  devcieComp: DeviceComp[] = []; // 设备部件
  propMap: { [name: string]: DeviceProp } = {};
  mapOfExpandedComp: { [code: string]: DeviceCompTreeNode[] } = {};
  showRealTimeChart: boolean;

  constructor(
    @Inject('RealTimeMonitorService') private realTimeMonitorService: RealTimeMonitorService,
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
        this.deviceData.forEach(group => {
          (group.groupPropertyList || []).forEach(prop => {
            this.propMap[prop.name] = prop;
          });
        });
        const setCompPropMap = (comp: DeviceComp) => {
          (comp.propertyList || []).forEach(prop => {
            this.propMap[prop.name] = prop;
          });
          if (comp.componentList && comp.componentList.length > 0) {
            comp.componentList.forEach(_comp => {
              setCompPropMap(_comp);
            });
          }
        }
        this.devcieComp.forEach(comp => {
          setCompPropMap(comp);
        });
        this.setMapOfExpandedComp();
        if (isMqtt) {
          this.fetchPropHistoryData(this.currPropName);
        } else if (this.deviceData.length > 0 && this.deviceData[0].groupPropertyList.length > 0) {
          const { name } = this.deviceData[0].groupPropertyList[0];
          this.fetchPropHistoryData(name, () => { this.subscribe(); });
        } else {
          this.currPropName = '';
          this.relatedPropName = [];
          this.subscribe();
        }
      });
    }
  }

  fetchPropHistoryData(propName: string, callFn?: Function) {
    this.currPropName = propName;
    this.relatedPropName = [];
    this.realTimeMonitorService.getPropHistoryData(this.deviceId, propName).subscribe(propData => {
      if (propData.enable) {
        this.propHistoryData = propData || {};
        this.relatedPropName = (propData.properties || []).map(item => (item.name));
        this.showRealTimeChart = true;
      } else {
        this.showRealTimeChart = false;
      }
      callFn && callFn();
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
    this.realTimeMonitorService.subscribe([this.deviceId], (data: { name: string; createdTime: number; content: string; }[]) => {
      (data || []).forEach(prop => {
        if (prop.content && /^(-)?\d+(\.\d+)?$/.test(prop.content)) {
          const num = parseFloat(prop.content);
          prop.content = Math.round((num + Number.EPSILON) * 100) / 100 + '';
        }
        const target = this.propMap[prop.name];
        if (target) {
          Object.assign(target, prop);
          if (this.showRealTimeChart && this.relatedPropName.includes(prop.name)) {
            this.propDataChart.pushData(prop.name, { ts: target.createdTime, value: target.content });
          }
        }
      });
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
