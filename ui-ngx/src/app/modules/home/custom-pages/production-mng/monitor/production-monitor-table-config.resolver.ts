import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { EntityType, entityTypeTranslations, entityTypeResources } from '@app/shared/public-api';
import { DatePipe } from '@angular/common';
import { ProdMonitor } from '@app/shared/models/custom/production-mng.models';
import { ProdMonitorFilterComponent } from './production-monitor-filter.component';
import { ProductionMngService } from '@app/core/http/custom/production-mng.service';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class ProdMonitorTableConfigResolver implements Resolve<EntityTableConfig<ProdMonitor>> {

  private readonly config: EntityTableConfig<ProdMonitor> = new EntityTableConfig<ProdMonitor>();

  constructor(
    private productionMngService: ProductionMngService,
    private datePipe: DatePipe,
    private translate: TranslateService
  ) {
    this.config.entityType = EntityType.POTENCY;
    this.config.filterComponent = ProdMonitorFilterComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.POTENCY);
    this.config.entityResources = entityTypeResources.get(EntityType.POTENCY);

    this.config.defaultSortOrder = null;

    this.config.columns.push(
      new EntityTableColumn<ProdMonitor>('scardNo', 'potency.card-no', '100px', (entity) => (entity.scardNo || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('sorderNo', 'potency.production-order-no', '100px', (entity) => (entity.sorderNo || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('scustomerName', 'order.customer', '60px', (entity) => (entity.scustomerName || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('ddeliveryDate', 'potency.delivery-date', '120px', (entity) => (entity.ddeliveryDate || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('smaterialName', 'order.product-name', '200px', (entity) => (entity.smaterialName || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('scolorName', 'order.colour', '100px', (entity) => (entity.scolorName || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('nplanOutputQty', 'order.number-of-cards', '100px', (entity) => (entity.nplanOutputQty || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('sworkingProcedureNameFinish', 'order.completion-process', '100px', (entity) => (entity.sworkingProcedureNameFinish || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('sWorkingProcedureName', 'order.process-to-be-produced', '100px', (entity) => (entity.sworkingProcedureName || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('fnMESGetDiffTimeStr', 'order.dead-time', '120px', (entity) => (entity.fnMESGetDiffTimeStr || ''), () => ({}), false)
      // new EntityTableColumn<ProdMonitor>('sequipmentName', 'order.reasons-for-dullness', '150px', (entity) => (entity.sequipmentName || ''), () => ({}), false)
    );
  }

  resolve(): EntityTableConfig<ProdMonitor> {

    this.config.componentsData = {
      sWorkingProcedureName: '',
      operationList: [],
      totalquantity: 0
    }

    this.config.tableTitle = this.translate.instant('production-mng.prod-monitor');
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;

    this.productionMngService.getProcedureName().subscribe(res => {
      this.config.componentsData.operationList = res;
    });

    // 获取合计数量
    this.productionMngService.getTotalQuantity({
      page: 0,
      pageSize: 10000000
    }).subscribe((res) => {
      let totalquantity = 0;
      res.data.forEach((item)=> {
        totalquantity += parseFloat(item.nplanOutputQty)
      })
      this.config.componentsData.totalquantity = totalquantity;
    });

    this.config.entitiesFetchFunction = pageLink => {
      console.log(pageLink)
      const { sWorkingProcedureName } = this.config.componentsData;
      return this.productionMngService.getProdMonitorList(pageLink, {
        sWorkingProcedureName: sWorkingProcedureName || ''
      });
    }

    return this.config;

  }

}