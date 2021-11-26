import { UtilsService } from '@core/services/utils.service';
import { FactoryMngService } from './../../../../../core/http/custom/factory-mng.service';
import { AfterViewInit, Component, OnInit } from '@angular/core';
import { AppState, DialogService } from '@app/core/public-api';
import { FactoryTableOriginRow, FactoryTableTreeNode } from '@app/shared/models/custom/factory-mng.models';
import { BaseData, EntityType, EntityTypeResource, entityTypeResources, EntityTypeTranslation, entityTypeTranslations, HasId, HasUUID, PageComponent } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { FILTERS } from './filters-config';
import { MatDialog } from '@angular/material/dialog';
import { AddEntityDialogComponent } from '@app/modules/home/components/entity/add-entity-dialog.component';
import { AddEntityDialogData } from '@app/modules/home/models/entity/entity-component.models';
import { FactoryFormComponent } from './factory-form.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { WorkShopFormComponent } from './work-shop-form.component';
import { ProdLineFormComponent } from './prod-line-form.component';
import { DeviceFormComponent } from './device-form.component';
import { DistributeDeviceComponent, DistributeDeviceDialogData } from './distribute-device.component';

@Component({
  selector: 'tb-factory-mng',
  templateUrl: './factory-mng.component.html',
  styleUrls: ['./factory-mng.component.scss']
})
export class FactoryMngComponent extends PageComponent implements OnInit, AfterViewInit {

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
  public scrollConfig = { x: '100%', y: '' }
  public drawerEntityConfig: EntityTableConfig<BaseData<HasId>> = {};
  public currentEntityId: string = '';
  public checkedDeviceIdList = new Set<string>();
  public expandedIdList = new Set<string>();

  constructor(
    protected store: Store<AppState>,
    public translate: TranslateService,
    private factoryMngService: FactoryMngService,
    public utils: UtilsService,
    public dialog: MatDialog,
    private dialogService: DialogService
  ) {
    super(store);
  }

  ngOnInit() {
    this.fetchData();
  }

  ngAfterViewInit() {
    this.setTableHeight();
    window.onresize = this.setTableHeight;
  }

  setTableHeight() {
    const totalHeight = document.querySelector('.tb-entity-table-content').clientHeight
    const tableClientTop =  document.querySelector('.mat-table-toolbar').clientHeight + document.querySelector('.entity-filter-header').clientHeight
    this.scrollConfig = { x: '100%', y: `${totalHeight - tableClientTop - 60}px` }
  }

  fetchData() {
    this.checkedDeviceIdList = new Set<string>();
    this.factoryMngService.getFactoryList(this.filterParams).subscribe(res => {
      const arr: FactoryTableOriginRow[] = [];
      const tableArr: FactoryTableTreeNode[] = [];
      res.factoryList.forEach(factory => {
        factory.rowType = 'factory';
        factory.key = factory.id + '';
      });
      res.workshopList.forEach(workShop => {
        workShop.parentId = workShop.factoryId;
        workShop.rowType = 'workShop';
        workShop.key = workShop.id + '';
      });
      res.productionLineList.forEach(prodLine => {
        prodLine.parentId = prodLine.workshopId;
        prodLine.rowType = 'prodLine';
        prodLine.key = prodLine.id + '';
      });
      res.deviceVoList.forEach(device => {
        device.parentId = device.productionLineId;
        device.rowType = 'device';
        device.key = device.id + '';
      });
      res.notDistributionList.forEach(device => {
        device.parentId = '-1';
        device.rowType = 'device';
        device.key = device.id;
      });
      arr.push(
        ...res.factoryList, ...res.workshopList, ...res.productionLineList, ...res.deviceVoList, ...res.notDistributionList
      );
      if (res.notDistributionList.length > 0) {
        arr.push({key: '-1', name: this.translate.instant('device-mng.undistributed-device')});
      }
      arr.forEach(item => {
        tableArr.push({
          id: item.key,
          parentId: item.parentId,
          code: item.code,
          name: item.name,
          logoImages: item.logoImages,
          address: item.address,
          createdTime: item.createdTime,
          rowType: item.rowType,
          factoryId: item.factoryId,
          factoryName: item.factoryName,
          workshopId: item.workshopId,
          workshopName: item.workshopName,
          productionLineId: item.productionLineId,
          productionLineName: item.productionLineName
        });
      });
      this.tableData = this.utils.formatTableTree(tableArr);
      this.mapOfExpandedData = {};
      this.tableData.forEach(item => {
        this.mapOfExpandedData[item.id] = this.convertTreeToList(item);
      });
      this.stopExpandPropagation();
    });
  }

  stopExpandPropagation() {
    setTimeout(() => {
      document.querySelectorAll('.ant-table-row-expand-icon').forEach(el => {
        el.removeEventListener('click', ($event: Event) => {$event.stopPropagation()});
        el.addEventListener('click', ($event: Event) => {$event.stopPropagation()});
      });
    });
  }

  onDeviceCheckedChange(id: string, checked: boolean) {
    if (checked) {
      this.checkedDeviceIdList.add(id);
    } else {
      this.checkedDeviceIdList.delete(id);
    }
  }

  collapse(array: FactoryTableTreeNode[], data: FactoryTableTreeNode, $event: boolean): void {
    if (!$event) {
      this.expandedIdList.delete(data.id);
      if (data.children) {
        data.children.forEach(d => {
          const target = array.find(a => a.id === d.id)!;
          target.expand = false;
          this.expandedIdList.delete(target.id);
          this.collapse(array, target, false);
        });
      } else {
        return;
      }
    } else {
      this.expandedIdList.add(data.id);
      this.stopExpandPropagation();
    }
  }

  convertTreeToList(root: FactoryTableTreeNode): FactoryTableTreeNode[] {
    const stack: FactoryTableTreeNode[] = [];
    const array: FactoryTableTreeNode[] = [];
    const hashMap = {};
    stack.push({ ...root, level: 0, expand: this.expandedIdList.has(root.id) });

    while (stack.length !== 0) {
      const node = stack.pop()!;
      if (!hashMap[node.id]) {
        hashMap[node.id] = true;
        array.push(node);
      }
      if (node.children) {
        for (let i = node.children.length - 1; i >= 0; i--) {
          stack.push({
            ...node.children[i],
            level: node.level! + 1,
            expand: this.expandedIdList.has(node.children[i].id),
            parent: node
          });
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
    this.dialog.open<AddEntityDialogComponent, AddEntityDialogData<BaseData<HasId>>, BaseData<HasId>>(AddEntityDialogComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        entitiesTableConfig: {
          entityTranslations: entityTypeTranslations.get(EntityType.FACTORY),
          entityResources: entityTypeResources.get(EntityType.FACTORY),
          entityComponent: FactoryFormComponent,
          saveEntity: entity => this.factoryMngService.saveFactory(entity)
        }
      }
    }).afterClosed().subscribe(res => {
      res && this.fetchData();
    });
  }

  addWorkShop({ id: factoryId, name: factoryName }: { [key: string]: string }) {
    this.dialog.open<AddEntityDialogComponent, AddEntityDialogData<BaseData<HasId>>, BaseData<HasId>>(AddEntityDialogComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        entitiesTableConfig: {
          entityTranslations: entityTypeTranslations.get(EntityType.WORK_SHOP),
          entityResources: entityTypeResources.get(EntityType.WORK_SHOP),
          entityComponent: WorkShopFormComponent,
          saveEntity: entity => this.factoryMngService.saveWorkShop(entity),
          componentsData: { factoryId, factoryName }
        }
      }
    }).afterClosed().subscribe(res => {
      res && this.fetchData();
    });
  }

  addProdLine({ factoryId, factoryName, id: workshopId, name: workshopName }: { [key: string]: string }) {
    this.dialog.open<AddEntityDialogComponent, AddEntityDialogData<BaseData<HasId>>, BaseData<HasId>>(AddEntityDialogComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        entitiesTableConfig: {
          entityTranslations: entityTypeTranslations.get(EntityType.PROD_LINE),
          entityResources: entityTypeResources.get(EntityType.PROD_LINE),
          entityComponent: ProdLineFormComponent,
          saveEntity: entity => this.factoryMngService.saveProdLine(entity),
          componentsData: { factoryId, factoryName, workshopId, workshopName }
        }
      }
    }).afterClosed().subscribe(res => {
      res && this.fetchData();
    });
  }

  addDevice({ factoryId, factoryName, workshopId, workshopName, id: productionLineId, name: productionLineName }: { [key: string]: string }) {
    this.dialog.open<AddEntityDialogComponent, AddEntityDialogData<BaseData<HasId>>, BaseData<HasId>>(AddEntityDialogComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        entitiesTableConfig: {
          entityTranslations: entityTypeTranslations.get(EntityType.DEVICE),
          entityResources: entityTypeResources.get(EntityType.DEVICE),
          entityComponent: DeviceFormComponent,
          saveEntity: entity => this.factoryMngService.saveDevice(entity),
          componentsData: { factoryId, factoryName, workshopId, workshopName, productionLineId, productionLineName }
        }
      }
    }).afterClosed().subscribe(res => {
      res && this.fetchData();
    });
  }

  distributeDevice(deviceIdList?: string[]) {
    this.dialog.open<DistributeDeviceComponent, DistributeDeviceDialogData>(DistributeDeviceComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        deviceIdList: deviceIdList || Array.from(this.checkedDeviceIdList)
      }
    }).afterClosed().subscribe(res => {
      res && this.fetchData();
      this.checkedDeviceIdList.clear();
    });
  }

  del(entity: FactoryTableTreeNode) {
    let type = '';
    let fnName: string;
    let params: string | object;
    if (entity.rowType === 'factory') {
      type = 'factory';
      fnName = 'deleteFactory';
    } else if (entity.rowType === 'workShop') {
      type = 'work-shop';
      fnName = 'deleteWorkShop';
    } else if (entity.rowType === 'prodLine') {
      type = 'prod-line';
      fnName = 'deleteProdLine';
    } else if (entity.rowType === 'device') {
      type = 'device';
      fnName = 'removeDevice';
      params = { deviceIdList: [entity.id], productionLineId: entity.productionLineId }
    }
    this.dialogService.confirm(
      this.translate.instant(`device-mng.delete-${type}-title`, { name: entity.name }),
      '',
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((result) => {
      if (result) {
        this.factoryMngService[fnName](params || entity.id).subscribe(() => {
          this.fetchData();
        });
      }
    });
  }

  onRowClick($event: Event, entity: FactoryTableTreeNode) {
    if ($event) {
      $event.stopPropagation();
    }
    if (entity.id !== this.currentEntityId) {
      this.currentEntityId = entity.id;
      let entityTranslations: EntityTypeTranslation;
      let entityResources: EntityTypeResource<any>;
      let entityComponent: any;
      let saveEntity: any;
      let loadEntity: any;
      if (entity.rowType === 'factory') {
        entityTranslations = entityTypeTranslations.get(EntityType.FACTORY);
        entityResources = entityTypeResources.get(EntityType.FACTORY);
        entityComponent = FactoryFormComponent;
        saveEntity = (entity: BaseData<HasId>) => this.factoryMngService.saveFactory(entity);
        loadEntity = (id: HasUUID) => this.factoryMngService.getFactory(id + '');
      } else if (entity.rowType === 'workShop') {
        entityTranslations = entityTypeTranslations.get(EntityType.WORK_SHOP);
        entityResources = entityTypeResources.get(EntityType.WORK_SHOP);
        entityComponent = WorkShopFormComponent;
        saveEntity = (entity: BaseData<HasId>) => this.factoryMngService.saveWorkShop(entity);
        loadEntity = (id: HasUUID) => this.factoryMngService.getWorkShop(id + '');
      } else if (entity.rowType === 'prodLine') {
        entityTranslations = entityTypeTranslations.get(EntityType.PROD_LINE);
        entityResources = entityTypeResources.get(EntityType.PROD_LINE);
        entityComponent = ProdLineFormComponent;
        saveEntity = (entity: BaseData<HasId>) => this.factoryMngService.saveProdLine(entity);
        loadEntity = (id: HasUUID) => this.factoryMngService.getProdLine(id + '');
      } else if (entity.rowType === 'device') {
        entityTranslations = entityTypeTranslations.get(EntityType.DEVICE);
        entityResources = entityTypeResources.get(EntityType.DEVICE);
        entityComponent = DeviceFormComponent;
        saveEntity = (entity: BaseData<HasId>) => this.factoryMngService.saveDevice(entity);
        loadEntity = (id: HasUUID) => this.factoryMngService.getDevice(id + '');
      }
      this.drawerEntityConfig = {
        entityTranslations,
        entityResources,
        entityComponent,
        saveEntity,
        loadEntity,
        entityTitle: entity => entity?.name,
        detailsReadonly: () => (!this.utils.hasPermission('action.edit')),
        componentsData: {}
      }
      this.isDetailsOpen = true;
    } else {
      this.isDetailsOpen = false;
    }
  }

}
