import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { EntityType, entityTypeTranslations, entityTypeResources, PageLink } from '@app/shared/public-api';
import { ProdReport } from '@app/shared/models/custom/production-mng.models';
import { ProductionReportFilterComponent } from './production-report-filter.component';
import { ProductionMngService } from '@app/core/http/custom/production-mng.service';
import { TranslateService } from '@ngx-translate/core';
import { FileService } from '@app/core/http/custom/file.service';
import { DatePipe } from '@angular/common';
import { updateReturn } from 'typescript';

@Injectable()
export class ProdReportTableConfigResolver implements Resolve<EntityTableConfig<ProdReport>> {

  private readonly config: EntityTableConfig<ProdReport> = new EntityTableConfig<ProdReport>();

  constructor(
    private productionMngService: ProductionMngService,
    private fileService: FileService,
    private translate: TranslateService,
    private datePipe: DatePipe
  ) {
    this.config.entityType = EntityType.POTENCY;
    this.config.filterComponent = ProductionReportFilterComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.POTENCY);
    this.config.entityResources = entityTypeResources.get(EntityType.POTENCY);

    this.config.defaultSortOrder = null;

    this.config.columns.push(
      new EntityTableColumn<ProdReport>('sorderNo', 'order.order-no', '80px', (entity) => (entity.sorderNo || ''), () => ({}), false),
      new EntityTableColumn<ProdReport>('scardNo', 'potency.card-no', '80px', (entity) => (entity.scardNo || ''), () => ({}), false),
      new EntityTableColumn<ProdReport>('sequipmentName', 'potency.device-name', '120px', (entity) => (entity.sequipmentName || ''), () => ({}), false),
      new EntityTableColumn<ProdReport>('sworkerGroupName', 'potency.team-name', '100px', (entity) => (entity.sworkerGroupName || ''), () => ({}), false),
      new EntityTableColumn<ProdReport>('scolorNo', 'potency.color-number', '60px', (entity) => (entity.scolorNo || ''), () => ({}), false),
      new EntityTableColumn<ProdReport>('ntrackQty', 'potency.capacity', '100px', (entity) => (entity.ntrackQty || ''), () => ({}), false),
      new DateEntityTableColumn<ProdReport>('tfactStartTime', 'datetime.date-from', this.datePipe, '120px', 'yyyy-MM-dd HH:mm', false),
      new DateEntityTableColumn<ProdReport>('tfactEndTime', 'datetime.date-to', this.datePipe, '120px', 'yyyy-MM-dd HH:mm', false),
      new EntityTableColumn<ProdReport>('fnMESGetDiffTimeStr', 'potency.duration', '80px', (entity) => (entity.fnMESGetDiffTimeStr || ''), () => ({}), false)
    );
  }

  resolve(): EntityTableConfig<ProdReport> {

    this.config.componentsData = {
      sOrderNo: '',
      dateRange: [],
      exportTableData: null
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
        const startDate = this.config.componentsData.dateRange[0] as Date;
        startDate.setSeconds(0);
        startDate.setMilliseconds(0);
        startTime = startDate.getTime();
        const endDate = this.config.componentsData.dateRange[1] as Date;
        endDate.setSeconds(59);
        endDate.setMilliseconds(999);
        endTime = endDate.getTime();
      }
      const { sOrderNo } = this.config.componentsData;
      return this.productionMngService.getProdReportList(pageLink, {
        sOrderNo: sOrderNo || '',
        tFactStartTime: startTime || '', tFactEndTime: endTime || ''
      });
    }

    // 导出功能
    this.config.componentsData.exportTableData = () => {
      let startTime: number, endTime: number;
      const dateRange = this.config.componentsData.dateRange;
      if (dateRange && dateRange.length === 2) {
        const startDate = this.config.componentsData.dateRange[0] as Date;
        startDate.setSeconds(0);
        startDate.setMilliseconds(0);
        startTime = startDate.getTime();
        const endDate = this.config.componentsData.dateRange[1] as Date;
        endDate.setSeconds(59);
        endDate.setMilliseconds(999);
        endTime = endDate.getTime();
      }
      const { sOrderNo } = this.config.componentsData;
      this.productionMngService.getProdReportList(new PageLink(9999999, 0), {
        sOrderNo, tFactStartTime: startTime || '', tFactEndTime: endTime || ''
      }).subscribe((res) => {
        const dataList = [];
        if (res.data.length > 0) {
          const titleKeys = ['order.order-no', 'potency.card-no', 'potency.device-name', 'potency.team-name', 'potency.color-number', 'potency.capacity', 'datetime.date-from', 'datetime.date-to', 'potency.duration'];
          const titleNames = [];
          titleKeys.forEach(key => {
            titleNames.push(this.translate.instant(key));
          });
          dataList.push(titleNames);
          res.data.forEach(item => {
            dataList.push([item.sorderNo, item.scardNo, item.sequipmentName, item.sworkerGroupName, item.scolorNo, item.ntrackQty, this.datePipe.transform(item.tfactStartTime, 'yyyy-MM-dd HH:mm'), this.datePipe.transform(item.tfactEndTime, 'yyyy-MM-dd HH:mm'), item.fnMESGetDiffTimeStr]);
          });
        }
        this.fileService.exportTable(this.translate.instant('production-mng.prod-report'), dataList).subscribe();
      })
    }

    return this.config;

  }

}