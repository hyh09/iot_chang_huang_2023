import { GroupProduction } from '../../../../../../shared/models/custom/potency.models';
import { Injectable } from "@angular/core";
import { Resolve } from "@angular/router";
import { PotencyService } from "@app/core/http/custom/potency.service";
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources } from "@app/shared/public-api";
import { GroupProductionFilterComponent } from './group-production-filter.component';
import { DatePipe } from '@angular/common';

@Injectable()
export class GroupProductionTableConfigResolver implements Resolve<EntityTableConfig<GroupProduction>> {

  private readonly config: EntityTableConfig<GroupProduction> = new EntityTableConfig<GroupProduction>();

  constructor(
    private potencyService: PotencyService,
    private datePipe: DatePipe
  ) {
    this.config.entityType = EntityType.POTENCY;
    this.config.filterComponent = GroupProductionFilterComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.POTENCY);
    this.config.entityResources = entityTypeResources.get(EntityType.POTENCY);

    this.config.defaultSortOrder = null;

    this.config.columns.push(
      new EntityTableColumn<GroupProduction>('workOrderNumber', 'potency.process-no', '100px', (entity) => (entity.workOrderNumber || ''), () => ({}), false),
      new EntityTableColumn<GroupProduction>('workingProcedureName', 'potency.process-name', '100px', (entity) => (entity.workingProcedureName || ''), () => ({}), false),
      new EntityTableColumn<GroupProduction>('workerGroupName', 'potency.team-name', '100px', (entity) => (entity.workerGroupName || ''), () => ({}), false),
      new EntityTableColumn<GroupProduction>('workerNameList', 'potency.team-members', '150px', (entity) => (entity.workerNameList || ''), () => ({}), false),
      new EntityTableColumn<GroupProduction>('ntrackQty', 'potency.output', '100px', (entity) => (entity.ntrackQty || ''), () => ({}), false),
      new EntityTableColumn<GroupProduction>('unit', 'potency.unit', '60px', (entity) => (entity.unit || ''), () => ({}), false),
      new EntityTableColumn<GroupProduction>('cardNo', 'potency.card-no', '100px', (entity) => (entity.cardNo || ''), () => ({}), false),
      new EntityTableColumn<GroupProduction>('materialNo', 'potency.material-no', '150px', (entity) => (entity.materialNo || ''), () => ({}), false),
      new EntityTableColumn<GroupProduction>('colorName', 'potency.color-name', '100px', (entity) => (entity.colorName || ''), () => ({}), false),
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

    return this.config;

  }

}