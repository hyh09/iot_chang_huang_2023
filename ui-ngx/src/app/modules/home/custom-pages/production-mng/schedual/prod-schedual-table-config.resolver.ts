import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { EntityType, entityTypeTranslations, entityTypeResources } from '@app/shared/public-api';
import { DatePipe } from '@angular/common';
import { ProdSchedual } from '@app/shared/models/custom/production-mng.models';
import { ProdSchedualFilterComponent } from './prod-schedual-filter.component';
import { ProductionMngService } from '@app/core/http/custom/production-mng.service';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class ProdSchedualTableConfigResolver implements Resolve<EntityTableConfig<ProdSchedual>> {

  private readonly config: EntityTableConfig<ProdSchedual> = new EntityTableConfig<ProdSchedual>();

  constructor(
    private productionMngService: ProductionMngService,
    private datePipe: DatePipe,
    private translate: TranslateService
  ) {
    this.config.entityType = EntityType.POTENCY;
    this.config.filterComponent = ProdSchedualFilterComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.POTENCY);
    this.config.entityResources = entityTypeResources.get(EntityType.POTENCY);

    this.config.defaultSortOrder = null;

    this.config.columns.push(
      new DateEntityTableColumn<ProdSchedual>('ttrackTime', 'common.start-time', this.datePipe, '130px', 'yyyy-MM-dd', false),
      new EntityTableColumn<ProdSchedual>('sworkerGroupName', 'potency.team-name', '100px', (entity) => (entity.sworkerGroupName || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('sworkerNameList ', 'potency.team-members', '100px', (entity) => (entity.sworkerNameList  || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('sworkingProcedureName', 'potency.process-name', '150px', (entity) => (entity.sworkingProcedureName || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('sorderNo', 'order.order-no', '100px', (entity) => (entity.sorderNo || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('scardNo', 'potency.card-no', '60px', (entity) => (entity.scardNo || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('tplanEndTime', 'order.intended-complete-date', '100px', (entity) => (entity.tplanEndTime || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('nplanOutputQty', 'potency.planned-quantity', '150px', (entity) => (entity.nplanOutputQty || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('ntrackQty', 'potency.actual-quantity', '100px', (entity) => (entity.ntrackQty || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('timeout', 'admin.timeout-msec', '100px', (entity) => (entity.timeout || ''), () => ({}), false),
    );
  }

  resolve(): EntityTableConfig<ProdSchedual> {

    this.config.componentsData = {
      sWorkerGroupName:'',
      sWorkingProcedureName:'',
      dateRange:[]
    }

    this.config.tableTitle = this.translate.instant('production-mng.prod-schedual');
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;

    this.config.entitiesFetchFunction = pageLink => {
      let startTime: number, endTime: number;
      const dateRange = this.config.componentsData.dateRange;
      if (dateRange && dateRange.length === 2) {
        startTime = (this.config.componentsData.dateRange[0] as Date).getTime();
        endTime = (this.config.componentsData.dateRange[1] as Date).getTime();
      }
      const { sWorkerGroupName, sWorkingProcedureName } = this.config.componentsData; // TODO 取出班组名称和当前工序
      return this.productionMngService.getProdSchedualList(pageLink, {
        sWorkerGroupName:  sWorkerGroupName || '',
        sWorkingProcedureName: sWorkingProcedureName || '',
        tTrackTimeStart: startTime || '', tTrackTimeEnd:  endTime || ''
      });
    }

    return this.config;

  }

}