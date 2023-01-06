import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { EntityType, entityTypeTranslations, entityTypeResources } from '@app/shared/public-api';
import { DatePipe } from '@angular/common';
import { ProdReport } from '@app/shared/models/custom/production-mng.models';
import { ProductionReportFilterComponent } from './production-report-filter.component';
import { ProductionMngService } from '@app/core/http/custom/production-mng.service';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class ProdReportTableConfigResolver implements Resolve<EntityTableConfig<ProdReport>> {

  private readonly config: EntityTableConfig<ProdReport> = new EntityTableConfig<ProdReport>();

  constructor(
    private productionMngService: ProductionMngService,
    private datePipe: DatePipe,
    private translate: TranslateService
  ) {
    this.config.entityType = EntityType.POTENCY;
    this.config.filterComponent = ProductionReportFilterComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.POTENCY);
    this.config.entityResources = entityTypeResources.get(EntityType.POTENCY);

    this.config.defaultSortOrder = null;

    this.config.columns.push(
      new EntityTableColumn<ProdReport>('sorderNo', 'potency.production-order-no', '60px', (entity) => (entity.sorderNo || ''), () => ({}), false),
      new EntityTableColumn<ProdReport>('scardNo', 'potency.card-no', '60px', (entity) => (entity.scardNo || ''), () => ({}), false),
      new EntityTableColumn<ProdReport>('scolorNo', 'potency.color-number', '60px', (entity) => (entity.scolorNo || ''), () => ({}), false),
      new EntityTableColumn<ProdReport>('tfactStartTime', 'alarm.start-time', '100px', (entity) => (entity.tfactStartTime || ''), () => ({}), false),
      new EntityTableColumn<ProdReport>('tfactEndTime', 'alarm.end-time', '100px', (entity) => (entity.tfactEndTime  || ''), () => ({}), false),
      new EntityTableColumn<ProdReport>('fnMESGetDiffTimeStr', 'potency.duration', '150px', (entity) => (entity.fnMESGetDiffTimeStr || ''), () => ({}), false),
      new EntityTableColumn<ProdReport>('ntrackQty', 'potency.production-output', '100px', (entity) => (entity.ntrackQty || ''), () => ({}), false),
      new EntityTableColumn<ProdReport>('sworkerGroupName', 'potency.team-name', '100px', (entity) => (entity.sworkerGroupName || ''), () => ({}), false),
      new EntityTableColumn<ProdReport>('sequipmentName', 'potency.production-machine', '150px', (entity) => (entity.sequipmentName || ''), () => ({}), false)
    );
  }

  resolve(): EntityTableConfig<ProdReport> {

    this.config.componentsData = {
      sOrderNo:'',
      dateRange:[]
    }

    this.config.tableTitle = this.translate.instant('production-mng.prod-report');
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
      const { sOrderNo } = this.config.componentsData; // TODO 取出班组名称和当前工序
      return this.productionMngService.getProdReportList(pageLink, {
        sOrderNo:  sOrderNo || '',
        tFactStartTime: startTime || '', tFactEndTime:  endTime || ''
      });
    }

    return this.config;

  }

}