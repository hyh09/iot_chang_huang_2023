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
      new EntityTableColumn<ProdSchedual>('sworkerGroupName', 'potency.team-name', '100px', (entity) => (entity.sworkerGroupName || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('sworkerNameList ', 'potency.team-members', '100px', (entity) => (entity.sworkerNameList || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('sworkingProcedureName', 'potency.process-name', '100px', (entity) => (entity.sworkingProcedureName || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('sorderNo', 'order.order-no', '80px', (entity) => (entity.sorderNo || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('scardNo', 'potency.card-no', '80px', (entity) => (entity.scardNo || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('nplanOutputQty', 'potency.planned-quantity', '100px', (entity) => (entity.nplanOutputQty || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('ntrackQty', 'potency.actual-quantity', '100px', (entity) => (entity.ntrackQty || ''), () => ({}), false),
      new DateEntityTableColumn<ProdSchedual>('ttrackTime', 'common.start-time', this.datePipe, '130px', 'yyyy-MM-dd', false),
      new EntityTableColumn<ProdSchedual>('tplanEndTime', 'order.intended-complete-date', '100px', (entity) => (entity.tplanEndTime || ''), () => ({}), false),
      new EntityTableColumn<ProdSchedual>('timeout', 'potency.timeout', '100px', (entity) => (entity.timeout || ''), () => ({}), false)
    );
  }

  resolve(): EntityTableConfig<ProdSchedual> {

    this.config.componentsData = {
      sWorkerGroupName: '',
      sWorkingProcedureName: '',
      dateRange: [],
      exportTableData: null,
      tableList: []
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
      const { sWorkerGroupName, sWorkingProcedureName } = this.config.componentsData;
      let tableList = this.productionMngService.getProdSchedualList(pageLink, {
        sWorkerGroupName: sWorkerGroupName || '',
        sWorkingProcedureName: sWorkingProcedureName || '',
        tTrackTimeStart: startTime || '', tTrackTimeEnd: endTime || ''
      })
      this.config.componentsData.tableList = tableList
      return tableList
    }

    // 导出功能
    this.config.componentsData.exportTableData = () => {
      this.config.componentsData.tableList.subscribe((res) => {
        //this.productionMngService.exportProdSchedulRecords().subscribe();
        let dataList = []
        let titleList = ['开始时间', '班组名称', '班组成员', '工序名称', '订单号', '卡号', '计划完工日期', '计划数量', '实际数量', '超时（ms）']
        dataList.push(titleList)
        if (res.data.length > 0) {
          res.data.forEach(item => {
              let itemList = [item.ttrackTime,item.sworkerGroupName,item.sworkerNameList,item.sworkingProcedureName,item.sorderNo,item.scardNo,item.tplanEndTime,item.nplanOutputQty,item.ntrackQty,item.timeout]
              dataList.push(itemList)
            });
        }
        this.productionMngService.exportPort('生产排班', dataList).subscribe();
        console.log(dataList)
      })
    }
    return this.config;

  }

}