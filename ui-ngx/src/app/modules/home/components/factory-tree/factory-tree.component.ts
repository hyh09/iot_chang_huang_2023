import { AfterViewInit, Component, OnInit } from '@angular/core';
import { FactoryMngService } from '@app/core/http/custom/factory-mng.service';
import { AppState, TreeNodeEmitEvent, UtilsService } from '@app/core/public-api';
import { FactoryTableOriginRow, FactoryTreeNodeOptions } from '@app/shared/models/custom/factory-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { NzFormatEmitEvent } from 'ng-zorro-antd/tree';
import { EntityTableHeaderComponent } from '../entity/entity-table-header.component';

@Component({
  selector: 'tb-factory-tree',
  templateUrl: './factory-tree.component.html',
  styleUrls: ['./factory-tree.component.scss']
})
export class FactoryTreeComponent extends EntityTableHeaderComponent<any> implements OnInit, AfterViewInit {

  public searchValue: string = '';
  public treeData: FactoryTreeNodeOptions[] = [];
  public scrollHeight = '';

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    private factoryMngService: FactoryMngService,
    private utils: UtilsService
  ) {
    super(store);
  }

  ngOnInit() {
    this.fetchData();
  }

  ngAfterViewInit() {
    this.setTreeHeight();
    window.onresize = this.setTreeHeight;
  }

  setTreeHeight() {
    const totalHeight = document.querySelector('.factory-tree').clientHeight;
    const searchHeight = document.querySelector('.factory-tree-search').clientHeight;
    this.scrollHeight = `${totalHeight - searchHeight}px`
  }

  fetchData() {
    this.factoryMngService.getFactoryList().subscribe(res => {
      const arr: FactoryTableOriginRow[] = [];
      const treeArr: FactoryTreeNodeOptions[] = [];
      res.factoryList.forEach(factory => {
        factory.rowType = 'factory';
        factory.key = factory.id + '';
        factory.title = factory.name;
      });
      res.workshopList.forEach(workShop => {
        workShop.parentId = workShop.factoryId;
        workShop.rowType = 'workShop';
        workShop.key = workShop.id + '';
        workShop.title = workShop.name;
      });
      res.productionLineList.forEach(prodLine => {
        prodLine.parentId = prodLine.workshopId;
        prodLine.rowType = 'prodLine';
        prodLine.key = prodLine.id + '';
        prodLine.title = prodLine.name;
      });
      res.deviceVoList.forEach(device => {
        device.parentId = device.productionLineId;
        device.rowType = 'device';
        device.key = device.id + '';
        device.title = device.name;
      });
      arr.push(...res.factoryList, ...res.workshopList, ...res.productionLineList, ...res.deviceVoList);
      arr.forEach(item => {
        treeArr.push({
          title: item.name,
          key: item.key,
          id: item.key,
          parentId: item.parentId,
          rowType: item.rowType,
          factoryId: item.factoryId,
          factoryName: item.factoryName,
          workshopId: item.workshopId,
          workshopName: item.workshopName,
          productionLineId: item.productionLineId,
          productionLineName: item.productionLineName
        });
      });
      this.treeData = this.utils.formatTree(treeArr);
    });
  }

  onClickNode(event: TreeNodeEmitEvent) {
    const { keys, node } = event;
    if (keys && keys.length > 0) {
      const nodeInfo: FactoryTreeNodeOptions = node.origin;
      const { rowType, id, factoryId, workshopId, productionLineId, deviceId } = nodeInfo;
      Object.assign(this.entitiesTableConfig.componentsData, {
        factoryId: rowType === 'factory' ? id : (factoryId || ''),
        workshopId: rowType === 'workShop' ? id : (workshopId || ''),
        productionLineId: rowType === 'prodLine' ? id : (productionLineId || ''),
        deviceId: rowType === 'device' ? id : (deviceId || '')
      });
    } else {
      Object.assign(this.entitiesTableConfig.componentsData, {
        factoryId: '',
        workshopId: '',
        productionLineId: '',
        deviceId: '',
      });
    }
    this.entitiesTableConfig.table.resetSortAndFilter(true);
  }

}
