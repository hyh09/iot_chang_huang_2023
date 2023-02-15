import { OrderConsumption } from '../../../../../../shared/models/custom/potency.models';
import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { PotencyService } from '@app/core/http/custom/potency.service';
import { CellActionDescriptor, DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { EntityType, entityTypeTranslations, entityTypeResources, PageLink } from '@app/shared/public-api';
import { OrderConsumptionFilterComponent } from './order-consumption-filter.component';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { MatDialog } from '@angular/material/dialog';
import { ProcessCardsComponent } from './process-cards.component';
import { FileService } from '@app/core/public-api';

@Injectable()
export class OrderConsumptionTableConfigResolver implements Resolve<EntityTableConfig<OrderConsumption>> {

  private readonly config: EntityTableConfig<OrderConsumption> = new EntityTableConfig<OrderConsumption>();

  constructor(
    private potencyService: PotencyService,
    private datePipe: DatePipe,
    private translate: TranslateService,
    public dialog: MatDialog,
    private fileService: FileService
  ) {
    this.config.entityType = EntityType.POTENCY;
    this.config.filterComponent = OrderConsumptionFilterComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.POTENCY);
    this.config.entityResources = entityTypeResources.get(EntityType.POTENCY);

    this.config.defaultSortOrder = null;

    this.config.columns.push(
      new EntityTableColumn<OrderConsumption>('orderNo', 'potency.order-no', '100px', (entity) => (entity.orderNo || ''), () => ({}), false),
      new EntityTableColumn<OrderConsumption>('customerName', 'potency.customer', '100px', (entity) => (entity.customerName || ''), () => ({}), false),
      new EntityTableColumn<OrderConsumption>('materialName', 'potency.material-name', '180px', (entity) => (entity.materialName || ''), () => ({}), false),
      new EntityTableColumn<OrderConsumption>('colorName', 'potency.color', '80px', (entity) => (entity.colorName || ''), () => ({}), false),
      new EntityTableColumn<OrderConsumption>('numberOfOrder', 'potency.order-count', '100px', (entity) => (entity.numberOfOrder || ''), () => ({}), false),
      new EntityTableColumn<OrderConsumption>('numberOfCards', 'potency.card-count', '100px', (entity) => (entity.numberOfCards || ''), () => ({}), false),
      new EntityTableColumn<OrderConsumption>('sremark', 'potency.arrangement-requirements', '300px', (entity) => (entity.sremark || ''), () => ({}), false),
      new EntityTableColumn<OrderConsumption>('duration', 'potency.duration', '80px', (entity) => (entity.duration || ''), () => ({}), false),
      new EntityTableColumn<OrderConsumption>('water', 'potency.water-consumption', '100px', entity => entity.water || '', () => ({}), false),
      new EntityTableColumn<OrderConsumption>('electricity', 'potency.electric-consumption', '100px', entity => entity.electricity || '', () => ({}), false),
      new EntityTableColumn<OrderConsumption>('gas', 'potency.gas-consumption', '100px', entity => entity.gas || '', () => ({}), false),
      new DateEntityTableColumn<OrderConsumption>('createdTime', 'common.created-time', this.datePipe, '130px', 'yyyy-MM-dd HH:mm:ss', false)
    );
  }

  resolve(): EntityTableConfig<OrderConsumption> {

    this.config.componentsData = {
      dateRange: [],
      orderNo: '',
      colorName: '',
      customerName: '',
      materialName: ''
    }

    this.config.titleVisible = false;
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;

    this.config.entitiesFetchFunction = pageLink => {
      let createdTime: number, updatedTime: number;
      const dateRange = this.config.componentsData.dateRange;
      if (dateRange && dateRange.length === 2) {
        createdTime = (this.config.componentsData.dateRange[0] as Date).getTime();
        updatedTime = (this.config.componentsData.dateRange[1] as Date).getTime();
      }
      const params = JSON.parse(JSON.stringify(this.config.componentsData));
      delete params.dateRange;
      return this.potencyService.getOrderConsumptionList(pageLink, {
        ...params, createdTime: createdTime || '', updatedTime: updatedTime || ''
      });
    }

    this.config.cellActionDescriptors = this.configureCellActions();

    // 导出功能
    this.config.componentsData.exportTableData = () => {
      let createdTime: number, updatedTime: number;
      const dateRange = this.config.componentsData.dateRange;
      if (dateRange && dateRange.length === 2) {
        createdTime = (this.config.componentsData.dateRange[0] as Date).getTime();
        updatedTime = (this.config.componentsData.dateRange[1] as Date).getTime();
      }
      const params = JSON.parse(JSON.stringify(this.config.componentsData));
      delete params.dateRange;
      this.potencyService.getOrderConsumptionList(new PageLink(9999999, 0), {
        ...params, createdTime: createdTime || '', updatedTime: updatedTime || ''
      }).subscribe((res) => {
        const dataList = [];
        if (res.data.length > 0) {
          const titleKeys = ['potency.customer', 'potency.material-name', 'potency.color', 'potency.order-count', 'potency.card-count','potency.arrangement-requirements',
          'potency.duration', 'potency.water-consumption', 'potency.electric-consumption', 'potency.gas-consumption', 'common.created-time'];
          const titleNames = [];
          titleKeys.forEach(key => {
            titleNames.push(this.translate.instant(key));
          });
          dataList.push(titleNames);
          res.data.forEach(item => {
            dataList.push([item.customerName, item.materialName, item.colorName, item.numberOfOrder, item.numberOfCards, item.sremark,
            item.duration, item.water, item.electricity, item.gas, this.datePipe.transform(item.createdTime, 'yyyy-MM-dd HH:mm:ss')]);
          });
        }
        this.fileService.exportTable(this.translate.instant('potency.energy-consumption-order'), dataList).subscribe();
      })
    }

    return this.config;

  }

  configureCellActions(): Array<CellActionDescriptor<OrderConsumption>> {
    const actions: Array<CellActionDescriptor<OrderConsumption>> = [];
    actions.push({
      name: this.translate.instant('potency.view-order-process-cards'),
      mdiIcon: 'mdi:process',
      isEnabled: (entity) => (!!(entity && entity.uguid)),
      onAction: ($event, entity) => this.openProcessCardsDialog($event, entity)
    });
    return actions;
  }

  openProcessCardsDialog($event: Event, order: OrderConsumption): void {
    if ($event) {
      $event.stopPropagation();
    }
    if (!order || !order.uguid) {
      return;
    }
    this.dialog.open<ProcessCardsComponent, OrderConsumption, void>(ProcessCardsComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: order
    })
  }

}