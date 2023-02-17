import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { EntityType, entityTypeTranslations, entityTypeResources, PageLink } from '@app/shared/public-api';
import { DatePipe } from '@angular/common';
import { ProdMonitor } from '@app/shared/models/custom/production-mng.models';
import { ProdMonitorFilterComponent } from './production-monitor-filter.component';
import { ProductionMngService } from '@app/core/http/custom/production-mng.service';
import { TranslateService } from '@ngx-translate/core';
import { FileService } from '@app/core/http/custom/file.service';

@Injectable()
export class ProdMonitorTableConfigResolver implements Resolve<EntityTableConfig<ProdMonitor>> {

  private readonly config: EntityTableConfig<ProdMonitor> = new EntityTableConfig<ProdMonitor>();

  constructor(
    private productionMngService: ProductionMngService,
    private fileService: FileService,
    private translate: TranslateService,
    private datePipe: DatePipe
  ) {
    this.config.entityType = EntityType.COMMON;
    this.config.filterComponent = ProdMonitorFilterComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.COMMON);
    this.config.entityResources = entityTypeResources.get(EntityType.COMMON);

    this.config.defaultSortOrder = null;

    this.config.columns.push(
      new EntityTableColumn<ProdMonitor>('scardNo', 'potency.card-no', '80px', (entity) => (entity.scardNo || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('sorderNo', 'order.order-no', '80px', (entity) => (entity.sorderNo || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('scustomerName', 'order.customer', '80px', (entity) => (entity.scustomerName || ''), () => ({}), false),
      new DateEntityTableColumn<ProdMonitor>('ddeliveryDate', 'potency.delivery-date', this.datePipe, '120px', 'yyyy-MM-dd HH:mm', false),
      new EntityTableColumn<ProdMonitor>('smaterialName', 'order.product-name', '300px', (entity) => (entity.smaterialName || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('scolorName', 'order.color', '80px', (entity) => (entity.scolorName || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('nplanOutputQty', 'order.number-of-cards', '80px', (entity) => (entity.nplanOutputQty || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('sworkingProcedureNameFinish', 'order.completion-process', '100px', (entity) => (entity.sworkingProcedureNameFinish || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('sworkingProcedureName', 'order.process-to-be-produced', '100px', (entity) => (entity.sworkingProcedureName || ''), () => ({}), false),
      new EntityTableColumn<ProdMonitor>('fnMESGetDiffTimeStr', 'order.dead-time', '120px', (entity) => (entity.fnMESGetDiffTimeStr || ''), () => ({}), false)
    );
  }

  resolve(): EntityTableConfig<ProdMonitor> {

    this.config.componentsData = {
      sWorkingProcedureName: '',
      operationList: [],
      totalquantity: 0,
      exportTableData: null
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

    this.config.entitiesFetchFunction = pageLink => {
      const { sWorkingProcedureName } = this.config.componentsData;
      return this.productionMngService.getProdMonitorList(pageLink, { sWorkingProcedureName });
    }

    // 导出功能
    this.config.componentsData.exportTableData = () => {
      const { sWorkingProcedureName } = this.config.componentsData;
      this.productionMngService.getProdMonitorList(new PageLink(9999999, 0), { sWorkingProcedureName }).subscribe((res) => {
        const dataList = [];
        if (res.data.length > 0) {
          const titleKeys = ['order.order-no', 'potency.card-no', 'order.customer', 'potency.delivery-date', 'order.product-name', 'order.color', 'order.number-of-cards', 'order.completion-process', 'order.process-to-be-produced', 'order.dead-time'];
          const titleNames = [];
          titleKeys.forEach(key => {
            titleNames.push(this.translate.instant(key));
          });
          dataList.push(titleNames);
          res.data.forEach(item => {
            dataList.push([item.sorderNo, item.scardNo, item.scustomerName, this.datePipe.transform(item.ddeliveryDate, 'yyyy-MM-dd'), item.smaterialName, item.scolorName, item.nplanOutputQty, item.sworkingProcedureNameFinish, item.sworkingProcedureName, item.fnMESGetDiffTimeStr]);
          });
        }
        this.fileService.exportTable(this.translate.instant('production-mng.prod-monitor'), dataList).subscribe();
      })
    }

    return this.config;

  }
}