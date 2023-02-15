import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { EntityType, entityTypeTranslations, entityTypeResources, PageLink } from '@app/shared/public-api';
import { DatePipe } from '@angular/common';
import { ProdSchedual } from '@app/shared/models/custom/production-mng.models';
import { ProdSchedualFilterComponent } from './prod-schedual-filter.component';
import { ProductionMngService } from '@app/core/http/custom/production-mng.service';
import { TranslateService } from '@ngx-translate/core';
import { FileService } from '@app/core/http/custom/file.service';
import { getTheEndOfDay, getTheStartOfDay } from '@app/core/utils';

@Injectable()
export class ProdSchedualTableConfigResolver implements Resolve<EntityTableConfig<ProdSchedual>> {

  private readonly config: EntityTableConfig<ProdSchedual> = new EntityTableConfig<ProdSchedual>();

  constructor(
    private productionMngService: ProductionMngService,
    private fileService: FileService,
    private datePipe: DatePipe,
    private translate: TranslateService
  ) {
    this.config.entityType = EntityType.ORDER;
    this.config.filterComponent = ProdSchedualFilterComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.ORDER);
    this.config.entityResources = entityTypeResources.get(EntityType.ORDER);

    this.config.defaultSortOrder = null;

    this.config.columns.push(
      new EntityTableColumn<ProdSchedual>('sorderNo', 'order.order-no', '80px', (entity) => (entity.sorderNo || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('scardNo', 'potency.card-no', '80px', (entity) => (entity.scardNo || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('sworkingProcedureName', 'potency.procedure-name', '100px', (entity) => (entity.sworkingProcedureName || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('sworkerGroupName', 'potency.team-name', '100px', (entity) => (entity.sworkerGroupName || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('sworkerNameList ', 'potency.team-members', '120px', (entity) => (entity.sworkerNameList || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('nplanOutputQty', 'order.intended-capacity', '100px', (entity) => (entity.nplanOutputQty || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('ntrackQty', 'order.actual-capacity', '100px', (entity) => (entity.ntrackQty || ''), () => ({}), false),
      new DateEntityTableColumn<ProdSchedual>('ttrackTime', 'production-mng.start-date', this.datePipe, '80px', 'yyyy-MM-dd', false),
      new EntityTableColumn<ProdSchedual>('tplanEndTime', 'order.intended-complete-date', '80px', (entity) => (entity.tplanEndTime || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('timeout', 'potency.timeout-on-minutes', '100px', (entity) => (entity.timeout || ''), () => ({}), false)
    );
  }

  resolve(): EntityTableConfig<ProdSchedual> {

    this.config.componentsData = {
      sWorkerGroupName: '',
      sWorkingProcedureName: '',
      dateRange: [],
      exportTableData: null
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
        startTime = getTheStartOfDay(this.config.componentsData.dateRange[0] as Date) as number;
        endTime = getTheEndOfDay(this.config.componentsData.dateRange[1] as Date) as number;
      }
      const { sWorkerGroupName, sWorkingProcedureName } = this.config.componentsData;
      return this.productionMngService.getProdSchedualList(pageLink, {
        sWorkerGroupName, sWorkingProcedureName,
        tTrackTimeStart: startTime || '', tTrackTimeEnd: endTime || ''
      });
    }

    // 导出功能
    this.config.componentsData.exportTableData = () => {
      let startTime: number, endTime: number;
      const dateRange = this.config.componentsData.dateRange;
      if (dateRange && dateRange.length === 2) {
        startTime = getTheStartOfDay(this.config.componentsData.dateRange[0] as Date) as number;
        endTime = getTheEndOfDay(this.config.componentsData.dateRange[1] as Date) as number;
      }
      const { sWorkerGroupName, sWorkingProcedureName } = this.config.componentsData;
      this.productionMngService.getProdSchedualList(new PageLink(9999999, 0), {
        sWorkerGroupName, sWorkingProcedureName,
        tTrackTimeStart: startTime || '', tTrackTimeEnd: endTime || ''
      }).subscribe((res) => {
        const dataList = [];
        if (res.data.length > 0) {
          const titleKeys = ['order.order-no', 'potency.card-no', 'potency.procedure-name', 'potency.team-name', 'potency.team-members', 'order.intended-capacity', 'order.actual-capacity', 'production-mng.start-date', 'order.intended-complete-date', 'potency.timeout-on-minutes'];
          const titleNames = [];
          titleKeys.forEach(key => {
            titleNames.push(this.translate.instant(key));
          });
          dataList.push(titleNames);
          res.data.forEach(item => {
            dataList.push([item.sorderNo, item.scardNo, item.sworkingProcedureName, item.sworkerGroupName, item.sworkerNameList, item.nplanOutputQty, item.ntrackQty, this.datePipe.transform(item.ttrackTime, 'yyyy-MM-dd'), this.datePipe.transform(item.tplanEndTime, 'yyyy-MM-dd'), item.timeout]);
          });
        }
        this.fileService.exportTable(this.translate.instant('production-mng.prod-schedual'), dataList).subscribe();
      })
    }

    return this.config;

  }

}