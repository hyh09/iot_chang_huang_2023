import { UtilsService } from '@core/services/utils.service';
import { FactoryMngService } from './../../../../../core/http/custom/factory-mng.service';
import { Component, OnInit } from '@angular/core';
import { AppState } from '@app/core/public-api';
import { FactoryTableOriginRow, FactoryTableTreeNode } from '@app/shared/models/custom/factory-mng.models';
import { PageComponent } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { FILTERS } from './filters-config';

@Component({
  selector: 'tb-factory-mng',
  templateUrl: './factory-mng.component.html',
  styleUrls: ['./factory-mng.component.scss']
})
export class FactoryMngComponent extends PageComponent implements OnInit {

  public isDetailsOpen: boolean = false;
  public filters = FILTERS;
  public filterParams = {
    name: '',
    workshopName: '',
    productionlineName: '',
    deviceName: ''
  }
  public tableData: FactoryTableTreeNode[] = [];
  public mapOfExpandedData: { [key: string]: FactoryTableTreeNode[] } = {};

  constructor(
    protected store: Store<AppState>,
    public translate: TranslateService,
    private factoryMngService: FactoryMngService,
    private utils: UtilsService
  ) {
    super(store);
  }

  ngOnInit() {
    this.fetchData();
  }

  fetchData() {
    this.factoryMngService.getFactoryList(this.filterParams).subscribe(res => {
      const arr: FactoryTableOriginRow[] = [];
      const tableArr: FactoryTableTreeNode[] = [];
      res.workshopEntityList.forEach(workShop => {
        workShop.parentId = workShop.factoryId;
        workShop.key = workShop.id.id;
      });
      res.productionLineEntityList.forEach(prodLine => {
        prodLine.parentId = prodLine.workshopId;
        prodLine.key = prodLine.id.id;
      });
      res.deviceEntityList.forEach(device => {
        device.parentId = device.productionLineId;
        device.key = device.id.id;
      });
      arr.push(...res.factoryEntityList, ...res.workshopEntityList, ...res.productionLineEntityList, ...res.deviceEntityList);
      arr.forEach(item => {
        tableArr.push({
          id: item.key,
          code: item.code,
          name: item.name,
          image: item.images || item.logoImages,
          address: item.address,
          createdTime: item.createdTime
        });
      });
      this.tableData = this.utils.formatTableTree(tableArr);
    });
  }

  collapse(array: FactoryTableTreeNode[], data: FactoryTableTreeNode, $event: boolean): void {
    if (!$event) {
      if (data.children) {
        data.children.forEach(d => {
          const target = array.find(a => a.id === d.id)!;
          target.expand = false;
          this.collapse(array, target, false);
        });
      } else {
        return;
      }
    }
  }

  convertTreeToList(root: FactoryTableTreeNode): FactoryTableTreeNode[] {
    const stack: FactoryTableTreeNode[] = [];
    const array: FactoryTableTreeNode[] = [];
    const hashMap = {};
    stack.push({ ...root, level: 0, expand: false });

    while (stack.length !== 0) {
      const node = stack.pop()!;
      if (!hashMap[node.id]) {
        hashMap[node.id] = true;
        array.push(node);
      }
      if (node.children) {
        for (let i = node.children.length - 1; i >= 0; i--) {
          stack.push({ ...node.children[i], level: node.level! + 1, expand: false, parent: node });
        }
      }
    }

    return array;
  }

  onClear(param: string): void {
    this.filterParams[param] = '';
    this.fetchData();
  }

  addFactory() {

  }

}
