import { GroupProduction } from '../../../../../../shared/models/custom/potency.models';
import { Injectable } from "@angular/core";
import { Resolve } from "@angular/router";
import { PotencyService } from "@app/core/http/custom/potency.service";
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources, PageLink } from "@app/shared/public-api";
import { GroupProductionFilterComponent } from './group-production-filter.component';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { FileService } from '@app/core/public-api';

@Injectable()
export class GroupProductionTableConfigResolver implements Resolve<EntityTableConfig<GroupProduction>> {

  private readonly config: EntityTableConfig<GroupProduction> = new EntityTableConfig<GroupProduction>();

  constructor(
    private potencyService: PotencyService,
    private datePipe: DatePipe,
    private translate: TranslateService,
    private fileService: FileService
  ) {
    this.config.entityType = EntityType.POTENCY_GROUP;
    this.config.filterComponent = GroupProductionFilterComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.POTENCY_GROUP);
    this.config.entityResources = entityTypeResources.get(EntityType.POTENCY_GROUP);

    this.config.defaultSortOrder = null;

    this.config.columns.push(
      new EntityTableColumn<GroupProduction>('workOrderNumber', 'potency.procedure-no', '100px', (entity) => (entity.workOrderNumber || ''), () => ({}), false),
      new EntityTableColumn<GroupProduction>('workingProcedureName', 'potency.procedure-name', '100px', (entity) => (entity.workingProcedureName || ''), () => ({}), false),
      new EntityTableColumn<GroupProduction>('workerGroupName', 'potency.team-name', '100px', (entity) => (entity.workerGroupName || ''), () => ({}), false),
      new EntityTableColumn<GroupProduction>('workerNameList', 'potency.team-members', '150px', (entity) => (entity.workerNameList || ''), () => ({}), false),
      new EntityTableColumn<GroupProduction>('cardNo', 'potency.card-no', '100px', (entity) => (entity.cardNo || ''), () => ({}), false),
      new EntityTableColumn<GroupProduction>('materialNo', 'potency.material-no', '150px', (entity) => (entity.materialNo || ''), () => ({}), false),
      new EntityTableColumn<GroupProduction>('colorName', 'potency.color', '100px', (entity) => (entity.colorName || ''), () => ({}), false),
      new EntityTableColumn<GroupProduction>('ntrackQty', 'potency.capacity', '100px', (entity) => (entity.ntrackQty || ''), () => ({}), false),
      new DateEntityTableColumn<GroupProduction>('createdTime', 'common.start-time', this.datePipe, '130px', 'yyyy-MM-dd HH:mm:ss', false),
      new DateEntityTableColumn<GroupProduction>('updatedTime', 'common.end-time', this.datePipe, '130px', 'yyyy-MM-dd HH:mm:ss', false),
      new EntityTableColumn<GroupProduction>('duration', 'potency.duration', '80px', (entity) => (entity.duration || ''), () => ({}), false)
    );
  }

  resolve(): EntityTableConfig<GroupProduction> {

    this.config.componentsData = {
      dateRange: [],
      workingProcedureName: '',
      workerNameList: '',
      workerGroupName: ''
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
      const { workingProcedureName, workerNameList, workerGroupName } = this.config.componentsData;
      return this.potencyService.getGroupProductionList(pageLink, {
        workingProcedureName, workerNameList, workerGroupName, createdTime: createdTime || '', updatedTime: updatedTime || ''
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
      const { workingProcedureName, workerNameList, workerGroupName } = this.config.componentsData;
      this.potencyService.getGroupProductionList(new PageLink(9999999, 0), {
        workingProcedureName, workerNameList, workerGroupName, createdTime: createdTime || '', updatedTime: updatedTime || ''
      }).subscribe((res) => {
        const dataList = [];
        if (res.data.length > 0) {
          const titleKeys = ['potency.procedure-no', 'potency.procedure-name', 'potency.team-name', 'potency.team-members', 'potency.card-no', 'potency.material-no', 'potency.color', 'potency.capacity', 'common.start-time', 'common.end-time', 'potency.duration'];
          const titleNames = [];
          titleKeys.forEach(key => {
            titleNames.push(this.translate.instant(key));
          });
          dataList.push(titleNames);
          res.data.forEach(item => {
            dataList.push([item.workOrderNumber, item.workingProcedureName, item.workerGroupName, item.workerNameList, item.cardNo, item.materialNo, item.colorName, item.ntrackQty, this.datePipe.transform(item.createdTime, 'yyyy-MM-dd HH:mm:ss'), this.datePipe.transform(item.updatedTime, 'yyyy-MM-dd HH:mm:ss'), item.duration]);
          });
        }
        this.fileService.exportTable(this.translate.instant('potency.capacity-team'), dataList).subscribe();
      })
    }

    return this.config;

  }

}