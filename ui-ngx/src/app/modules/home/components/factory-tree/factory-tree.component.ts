import { AfterViewInit, Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FactoryMngService } from '@app/core/http/custom/factory-mng.service';
import { AppState, TreeNodeEmitEvent, UtilsService } from '@app/core/public-api';
import { FactoryTableOriginRow, FactoryTreeNodeIds, FactoryTreeNodeOptions } from '@app/shared/models/custom/factory-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
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

  public selectedKeys: string[] = [];

  @Output() clickNode = new EventEmitter<FactoryTreeNodeIds>();

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
    setTimeout(() => {
      const totalHeight = document.querySelector('.factory-tree').clientHeight;
      const searchHeight = 46;
      this.scrollHeight = `${totalHeight - searchHeight}px`;
    });
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
      this.selectedKeys = treeArr[0] ? [treeArr[0].key] : [];
      this.treeData = this.utils.formatTree(treeArr);
      const params = {
        factoryId: this.selectedKeys[0] || '',
        workshopId: '',
        productionLineId: '',
        deviceId: ''
      };
      this.clickNode.emit(params);
      if (this.entitiesTableConfig && this.entitiesTableConfig.componentsData) {
        Object.assign(this.entitiesTableConfig.componentsData, params);
        this.entitiesTableConfig.table.resetSortAndFilter(true);
      }
    });
  }

  onClickNode(event: TreeNodeEmitEvent) {
    const { node } = event;
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

}
