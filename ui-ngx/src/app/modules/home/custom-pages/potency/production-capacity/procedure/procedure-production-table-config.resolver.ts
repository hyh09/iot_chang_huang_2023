import { ProcedureProduction } from '../../../../../../shared/models/custom/potency.models';
import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { PotencyService } from '@app/core/http/custom/potency.service';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { EntityType, entityTypeTranslations, entityTypeResources, PageLink } from '@app/shared/public-api';
import { ProcedureProductionFilterComponent } from './procedure-production-filter.component';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { FileService } from '@app/core/public-api';

@Injectable()
export class ProcedureProductionTableConfigResolver implements Resolve<EntityTableConfig<ProcedureProduction>> {

  private readonly config: EntityTableConfig<ProcedureProduction> = new EntityTableConfig<ProcedureProduction>();

  constructor(
    private potencyService: PotencyService,
    private datePipe: DatePipe,
    private translate: TranslateService,
    private fileService: FileService
  ) {
    this.config.entityType = EntityType.POTENCY_PROCEDURE;
    this.config.filterComponent = ProcedureProductionFilterComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.POTENCY_PROCEDURE);
    this.config.entityResources = entityTypeResources.get(EntityType.POTENCY_PROCEDURE);

    this.config.defaultSortOrder = null;

    this.config.columns.push(
      new EntityTableColumn<ProcedureProduction>('cardNo', 'potency.card-no', '100px', (entity) => (entity.cardNo || ''), () => ({}), false),
      new EntityTableColumn<ProcedureProduction>('orderNo', 'potency.order-no', '100px', (entity) => (entity.orderNo || ''), () => ({}), false),
      new EntityTableColumn<ProcedureProduction>('customerName', 'potency.customer', '100px', (entity) => (entity.customerName || ''), () => ({}), false),
      new EntityTableColumn<ProcedureProduction>('materialName', 'potency.material-name', '180px', (entity) => (entity.materialName || ''), () => ({}), false),
      new EntityTableColumn<ProcedureProduction>('colorName', 'potency.color', '80px', (entity) => (entity.colorName || ''), () => ({}), false),
      new EntityTableColumn<ProcedureProduction>('numberOfCards', 'potency.card-count', '100px', (entity) => (entity.numberOfCards || ''), () => ({}), false),
      new EntityTableColumn<ProcedureProduction>('sremark', 'potency.arrangement-requirements', '300px', (entity) => (entity.sremark || ''), () => ({}), false),
      new EntityTableColumn<ProcedureProduction>('workingProcedureName', 'potency.procedure-name', '100px', (entity) => (entity.workingProcedureName || ''), () => ({}), false),
      new EntityTableColumn<ProcedureProduction>('workerGroupName', 'potency.team-name', '100px', (entity) => (entity.workerGroupName || ''), () => ({}), false),
      new EntityTableColumn<ProcedureProduction>('ntrackQty', 'potency.capacity', '100px', (entity) => (entity.ntrackQty || ''), () => ({}), false),
      new DateEntityTableColumn<ProcedureProduction>('createdTime', 'common.start-time', this.datePipe, '130px', 'yyyy-MM-dd HH:mm:ss', false),
      new DateEntityTableColumn<ProcedureProduction>('updatedTime', 'common.end-time', this.datePipe, '130px', 'yyyy-MM-dd HH:mm:ss', false),
      new EntityTableColumn<ProcedureProduction>('theoreticalTime', 'potency.theoretical-time-cost', '100px', (entity) => (entity.theoreticalTime || ''), () => ({}), false),
      new EntityTableColumn<ProcedureProduction>('actualTime', 'potency.actual-time-cost', '100px', (entity) => (entity.actualTime || ''), () => ({}), false),
      new EntityTableColumn<ProcedureProduction>('timeoutMinutes', 'potency.timeout-on-minutes', '100px', (entity) => (entity.timeoutMinutes || ''), () => ({}), false),
      new EntityTableColumn<ProcedureProduction>('overTimeRatio', 'potency.timeout-on-ratio', '100px', (entity) => (entity.overTimeRatio || ''), () => ({}), false),
    );
  }

  resolve(): EntityTableConfig<ProcedureProduction> {

    this.config.componentsData = {
      dateRange: [],
      workingProcedureName: '',
      colorName: '',
      materialName: '',
      customerName: '',
      orderNo: '',
      cardNo: ''
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
      return this.potencyService.getProcedureProductionList(pageLink, {
        ...params, createdTime: createdTime || '', updatedTime: updatedTime || ''
      });
    }

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
      this.potencyService.getProcedureProductionList(new PageLink(9999999, 0), {
        ...params, createdTime: createdTime || '', updatedTime: updatedTime || ''
      }).subscribe((res) => {
        const dataList = [];
        if (res.data.length > 0) {
          const titleKeys = ['potency.card-no', 'potency.order-no', 'potency.customer', 'potency.material-name', 'potency.color', 'potency.card-count',
          'potency.arrangement-requirements', 'potency.procedure-name', 'potency.team-name', 'potency.capacity', 'common.start-time', 'common.end-time',
          'potency.theoretical-time-cost', 'potency.actual-time-cost', 'potency.timeout-on-minutes', 'potency.timeout-on-ratio'];
          const titleNames = [];
          titleKeys.forEach(key => {
            titleNames.push(this.translate.instant(key));
          });
          dataList.push(titleNames);
          res.data.forEach(item => {
            dataList.push([item.cardNo, item.orderNo, item.customerName, item.materialName, item.colorName, item.numberOfCards,item.sremark,
            item.workingProcedureName, item.workerGroupName, item.ntrackQty, this.datePipe.transform(item.createdTime, 'yyyy-MM-dd HH:mm:ss'),
            this.datePipe.transform(item.updatedTime, 'yyyy-MM-dd HH:mm:ss'), item.theoreticalTime, item.actualTime, item.timeoutMinutes, item.overTimeRatio]);
          });
        }
        this.fileService.exportTable(this.translate.instant('potency.capacity-procedure'), dataList).subscribe();
      })
    }

    return this.config;

  }

}