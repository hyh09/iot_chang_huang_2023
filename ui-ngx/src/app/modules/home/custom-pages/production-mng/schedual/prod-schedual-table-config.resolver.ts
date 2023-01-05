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

    this.config.columns.push( // TODO 自行修改
      // new EntityTableColumn<ProdSchedual>('workOrderNumber', 'potency.process-no', '100px', (entity) => (entity.workOrderNumber || ''), () => ({}), false),
      // new EntityTableColumn<ProdSchedual>('workingProcedureName', 'potency.process-name', '100px', (entity) => (entity.workingProcedureName || ''), () => ({}), false),
      // new EntityTableColumn<ProdSchedual>('workerGroupName', 'potency.team-name', '100px', (entity) => (entity.workerGroupName || ''), () => ({}), false),
      // new EntityTableColumn<ProdSchedual>('workerNameList', 'potency.team-members', '150px', (entity) => (entity.workerNameList || ''), () => ({}), false),
      // new EntityTableColumn<ProdSchedual>('ntrackQty', 'potency.output', '100px', (entity) => (entity.ntrackQty || ''), () => ({}), false),
      // new EntityTableColumn<ProdSchedual>('unit', 'potency.unit', '60px', (entity) => (entity.unit || ''), () => ({}), false),
      // new EntityTableColumn<ProdSchedual>('cardNo', 'potency.card-no', '100px', (entity) => (entity.cardNo || ''), () => ({}), false),
      // new EntityTableColumn<ProdSchedual>('materialNo', 'potency.material-no', '150px', (entity) => (entity.materialNo || ''), () => ({}), false),
      // new EntityTableColumn<ProdSchedual>('colorName', 'potency.color-name', '100px', (entity) => (entity.colorName || ''), () => ({}), false),
      // new DateEntityTableColumn<ProdSchedual>('createdTime', 'common.start-time', this.datePipe, '130px', 'yyyy-MM-dd HH:mm:ss', false),
      // new DateEntityTableColumn<ProdSchedual>('updatedTime', 'common.end-time', this.datePipe, '130px', 'yyyy-MM-dd HH:mm:ss', false),
      // new EntityTableColumn<ProdSchedual>('duration', 'potency.duration', '80px', (entity) => (entity.duration || ''), () => ({}), false)
    );
  }

  resolve(): EntityTableConfig<ProdSchedual> {

    this.config.componentsData = {
      dateRange: [],
      // TODO 请求参数 - 班组名称
      // TODO 请求参数 - 当前工序
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
      const {  } = this.config.componentsData; // TODO 取出班组名称和当前工序
      return this.productionMngService.getProdSchedualList(pageLink, {
        startTime: startTime || '', endTime: endTime || '', // TODO 加上上面取出的班组名称和当前工序
      });
    }

    return this.config;

  }

}