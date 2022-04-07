import { AfterViewInit, Component, EventEmitter, Inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FactoryMngService } from '@app/core/http/custom/factory-mng.service';
import { AppState, RealTimeMonitorService, TreeNodeEmitEvent, UtilsService } from '@app/core/public-api';
import { FactoryTableOriginRow, FactoryTreeNodeIds, FactoryTreeNodeOptions } from '@app/shared/models/custom/factory-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { EntityTableHeaderComponent } from '../entity/entity-table-header.component';

@Component({
  selector: 'tb-factory-tree',
  templateUrl: './factory-tree.component.html',
  styleUrls: ['./factory-tree.component.scss'],
  providers: [
    { provide: 'RealTimeMonitorService', useClass: RealTimeMonitorService }
  ]
})
export class FactoryTreeComponent extends EntityTableHeaderComponent<any> implements OnInit, AfterViewInit, OnDestroy {

  searchValue: string = '';
  treeData: FactoryTreeNodeOptions[] = [];
  scrollHeight = '';

  public selectedKeys: string[] = [];
  expandedKeys: string[] = [];

  @Input() deviceOnly: boolean = false;

  @Output() clickNode = new EventEmitter<FactoryTreeNodeIds>();

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    private factoryMngService: FactoryMngService,
    private utils: UtilsService,
    @Inject('RealTimeMonitorService') private realTimeMonitorService: RealTimeMonitorService
  ) {
    super(store);
  }

  ngOnInit() {
    this.fetchData();
  }

  ngAfterViewInit() {
    this.setTreeHeight();
    window.addEventListener('resize', () => { this.setTreeHeight(); });
  }

  ngOnDestroy() {
    window.removeEventListener('resize', () => { this.setTreeHeight(); });
    this.realTimeMonitorService.unsubscribe(true);
  }

  setTreeHeight() {
    setTimeout(() => {
      const $factoryTree = document.querySelector('.factory-tree');
      if ($factoryTree) {
        const totalHeight = $factoryTree.clientHeight;
        const searchHeight = 46;
        this.scrollHeight = `${totalHeight - searchHeight}px`;
      }
    });
  }

  fetchData() {
    this.factoryMngService.getFactoryTreeList().subscribe(res => {
      const arr: FactoryTableOriginRow[] = [];
      const treeArr: FactoryTreeNodeOptions[] = [];
      res.workshops.forEach(workShop => {
        workShop.parentId = workShop.factoryId;
      });
      res.productionLines.forEach(prodLine => {
        prodLine.parentId = prodLine.workshopId;
      });
      res.devices.forEach(device => {
        device.parentId = device.productionLineId;
        if (!device.sort) {
          device.sort = 0;
        }
      });
      res.devices.sort((curr, next) => {
        return curr.sort - next.sort;
      });
      res.undistributedDevices.forEach(device => {
        device.parentId = '-1';
      });
      arr.push(
        ...res.factories, ...res.workshops, ...res.productionLines, ...res.devices, ...res.undistributedDevices
      );
      if (res.undistributedDevices.length > 0) {
        arr.push({key: '-1', title: this.translate.instant('device-mng.undistributed-device')});
      }
      arr.forEach(item => {
        treeArr.push({
          title: item.title,
          key: item.key,
          id: item.key,
          parentId: item.parentId,
          rowType: item.rowType,
          factoryId: item.factoryId,
          workshopId: item.workshopId,
          productionLineId: item.productionLineId,
          selectable: this.deviceOnly ? item.rowType === 'device' : true,
          isOnLine: item.rowType === 'factory' || item.rowType === 'device'
        });
      });
      this.treeData = this.utils.formatTree(treeArr);
      const params = { factoryId: '', workshopId: '', productionLineId: '', deviceId: '' };
      if (this.deviceOnly) {
        const firstFactory = this.treeData[0];
        if (firstFactory && firstFactory.children && firstFactory.children[0]) {
          const firstWorkshop = firstFactory.children[0];
          if (firstWorkshop.children && firstWorkshop.children[0]) {
            const firstProdLine = firstWorkshop.children[0];
            if (firstProdLine.children && firstProdLine.children[0]) {
              const firstDevice = firstProdLine.children[0];
              this.selectedKeys = [firstDevice.key];
              const { factoryId, workshopId, productionLineId, id: deviceId } = firstDevice;
              Object.assign(params, { factoryId, workshopId, productionLineId, deviceId });
              this.expandedKeys = [factoryId, workshopId, productionLineId];
            }
          }
        }
      } else {
        this.selectedKeys = res.factories[0] ? [res.factories[0].key] : [];
        params.factoryId = this.selectedKeys[0] || '';
        params.factoryId = params.factoryId === '-1' ? '' : params.factoryId;
      }
      if (this.selectedKeys[0]) {
        this.clickNode.emit(params);
        if (this.entitiesTableConfig && this.entitiesTableConfig.componentsData) {
          Object.assign(this.entitiesTableConfig.componentsData, params);
          this.entitiesTableConfig.table.resetSortAndFilter(true);
        }
      }
      this.factoryMngService.getOnlineStatus().subscribe(_res => {
        // 初始化设备在线状态
        const deviceIdList = Object.keys(_res || {});
        const treeMap: {[id: string]: FactoryTreeNodeOptions} = {};
        treeArr.forEach(item => {
          treeMap[item.id] = item;
        });
        deviceIdList.forEach(id => {
          treeMap[id].isOnLine = _res[id];
        });
        this.factoryMngService.getFactoryOnlineStatus().subscribe(factory => {
          // 初始化工厂网关整体在线状态
          const factoryIdList = Object.keys(factory || {});
          factoryIdList.forEach(id => {
            treeMap[id].isOnLine = factory[id];
          });
          // 获取工厂下的所有网关id
          this.factoryMngService.getFactoryGatewayIds().subscribe(gateway => {
            const gatewayIds = [];
            const gatewayFactoryMap = {};
            (gateway || []).forEach(item => {
              gatewayIds.push(...(item.gatewayDeviceIds || []));
              (item.gatewayDeviceIds || []).forEach(gatewayId => {
                gatewayFactoryMap[gatewayId] = item.factoryId;
              })
            });
            // 订阅设备和网关状态变更
            this.realTimeMonitorService.switchDevices([...deviceIdList, ...gatewayIds], true);
            this.realTimeMonitorService.subscribe([...deviceIdList, ...gatewayIds], ({ deviceId, isActive }) => {
              if (deviceId === undefined || isActive === undefined) {
                return;
              }
              if (gatewayIds.includes(deviceId)) {
                const targetFactoryId = gatewayFactoryMap[deviceId]
                this.factoryMngService.getFactoryOnlineStatus(targetFactoryId).subscribe(_factory => {
                  treeMap[targetFactoryId].isOnLine = (_factory || {})[targetFactoryId];
                });
              } else {
                const target = treeArr.filter(item => (item.rowType === 'device' && item.id === deviceId));
                if (target.length > 0 && target[0].isOnLine !== !!isActive) {
                  target[0].isOnLine = !!isActive;
                }
              }
            }, true);
          });
        });
      });
    });
  }

  onClickNode(event: TreeNodeEmitEvent) {
    const { node } = event;
    if (this.deviceOnly && node.origin.rowType !== 'device') return;
    this.selectedKeys = [node.key];
    const nodeInfo: FactoryTreeNodeOptions = node.origin;
    const { rowType, id, factoryId, workshopId, productionLineId, deviceId } = nodeInfo;
    const params: FactoryTreeNodeIds = {
      factoryId: rowType === 'factory' ? id : (factoryId || ''),
      workshopId: rowType === 'workShop' ? id : (workshopId || ''),
      productionLineId: rowType === 'prodLine' ? id : (productionLineId || ''),
      deviceId: rowType === 'device' ? id : (deviceId || '')
    };
    if (this.entitiesTableConfig && this.entitiesTableConfig.componentsData) {
      Object.assign(this.entitiesTableConfig.componentsData, params);
      this.entitiesTableConfig.table.resetSortAndFilter(true);
    }
    this.clickNode.emit(params);
  }

  public setKeyState(factoryInfo: FactoryTreeNodeIds) {
    if (factoryInfo) {
      const { factoryId, workshopId, productionLineId, deviceId } = factoryInfo;
      if (deviceId) {
        this.selectedKeys = [deviceId];
        this.expandedKeys = [factoryId, workshopId, productionLineId];
      } else if (productionLineId) {
        this.selectedKeys = [productionLineId];
        this.expandedKeys = [factoryId, workshopId];
      } else if (workshopId) {
        this.selectedKeys = [workshopId];
        this.expandedKeys = [factoryId];
      } else if(factoryId) {
        this.selectedKeys = [factoryId];
        this.expandedKeys = [];
      } else {
        this.selectedKeys = ['-1'];
        this.expandedKeys = [];
      }
    }
  }

}
