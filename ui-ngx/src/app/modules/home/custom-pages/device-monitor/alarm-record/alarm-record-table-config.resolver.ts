import { DatePipe } from "@angular/common";
import { Injectable } from "@angular/core";
import { Resolve } from "@angular/router";
import { AlarmRecordService } from "@app/core/http/custom/alarm-record.service";
import { UtilsService } from "@app/core/public-api";
import { FactoryTreeComponent } from "@app/modules/home/components/factory-tree/factory-tree.component";
import { CellActionDescriptor, DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { AlarmLevelType, AlarmRecord, AlarmStatusType } from "@app/shared/models/custom/device-monitor.models";
import { EntityType, entityTypeTranslations, entityTypeResources } from "@app/shared/public-api";
import { TranslateService } from "@ngx-translate/core";
import { AlarmRecordFiltersComponent } from "./alarm-record-filters.component";

@Injectable()
export class AlarmRecordTableConfigResolver implements Resolve<EntityTableConfig<AlarmRecord>> {

  private readonly config: EntityTableConfig<AlarmRecord> = new EntityTableConfig<AlarmRecord>();

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private alarmRecordService: AlarmRecordService,
    private utils: UtilsService
  ) {
    this.config.entityType = EntityType.ALARM_RECORD;
    this.config.leftComponent = FactoryTreeComponent;
    this.config.filterComponent = AlarmRecordFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.ALARM_RECORD);
    this.config.entityResources = entityTypeResources.get(EntityType.ALARM_RECORD);

    this.config.componentsData = {
      factoryId: '',
      workshopId: '',
      productionLineId: '',
      deviceId: '',
      status: AlarmStatusType.ANY,
      level: AlarmLevelType.ANY,
      timePageLink: null,
      exportTableData: null
    }

    this.config.columns.push(
      new DateEntityTableColumn<AlarmRecord>('createdTime', 'common.created-time', this.datePipe, '150px'),
      new EntityTableColumn<AlarmRecord>('name', 'device-monitor.device-name', '25%', ({ name }) => (name || ''), () => ({}), false),
      new EntityTableColumn<AlarmRecord>('title', 'device-monitor.alarm-title', '25%', ({ title }) => (title || ''), () => ({}), false),
      new EntityTableColumn<AlarmRecord>('info', 'device-monitor.alarm-info', '50%', ({ info }) => (info || ''), () => ({}), false),
      new EntityTableColumn<AlarmRecord>('statusStr', 'device-monitor.alarm-status', '80px', ({statusStr}) => {
        return this.translate.instant(`device-monitor.${statusStr}`);
      }, () => ({}), false),
      new EntityTableColumn<AlarmRecord>('levelStr', 'device-monitor.alarm-level', '80px', ({levelStr}) => {
        return this.translate.instant(`device-monitor.${levelStr}`);
      }, () => ({}), false)
    );
  }

  resolve(): EntityTableConfig<AlarmRecord> {
    this.config.componentsData = {
      factoryId: '',
      workshopId: '',
      productionLineId: '',
      deviceId: '',
      status: AlarmStatusType.ANY,
      level: AlarmLevelType.ANY,
      pageLink: null,
      exportTableData: null
    }

    this.config.tableTitle = this.translate.instant('device-monitor.alarm-record');
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;
    this.config.useTimePageLink = true;
    this.config.timeWindowInFilter = true;
    this.config.afterResolved = () => {
      this.config.cellActionDescriptors = this.configureCellActions();
    }

    this.config.entitiesFetchFunction = pageLink => {
      this.config.componentsData.pageLink = pageLink;
      const { factoryId, workshopId, productionLineId, deviceId, status, level } = this.config.componentsData;
      return this.alarmRecordService.getAlarmRecords(pageLink, { factoryId, workshopId, productionLineId, deviceId, status, level });
    }

    this.config.componentsData.exportTableData = () => {
      const { pageLink } = this.config.componentsData;
      const { factoryId, workshopId, productionLineId, deviceId, status, level } = this.config.componentsData;
      this.alarmRecordService.exportAlarmRecords(pageLink, { factoryId, workshopId, productionLineId, deviceId, status, level }).subscribe();
    }

    this.config.loadDataOnInit = false;

    return this.config;
  }

  configureCellActions(): Array<CellActionDescriptor<AlarmRecord>> {
    const actions: Array<CellActionDescriptor<AlarmRecord>> = [];
    if (this.utils.hasPermission('action.confirm')) {
      actions.push({
        name: this.translate.instant('action.confirm'),
        mdiIcon: 'mdi:confirm',
        isEnabled: (entity) => (!!(entity && entity.id && entity.isCanBeConfirm)),
        onAction: ($event, entity) => this.confirmAlarmRecord($event, entity.id + '')
      });
    }
    if (this.utils.hasPermission('alarm.clear')) {
      actions.push({
        name: this.translate.instant('alarm.clear'),
        icon: 'delete',
        isEnabled: (entity) => (!!(entity && entity.id && entity.isCanBeClear)),
        onAction: ($event, entity) => this.clearAlarmRecord($event, entity.id + '')
      });
    }
    return actions;
  }

  confirmAlarmRecord($event: Event, id: string) {
    if ($event) {
      $event.stopPropagation();
    }
    this.alarmRecordService.confirmAlarmRecord(id).subscribe(() => {
      this.config.table.updateData();
    });
  }

  clearAlarmRecord($event: Event, id: string) {
    if ($event) {
      $event.stopPropagation();
    }
    this.alarmRecordService.clearAlarmRecord(id).subscribe(() => {
      this.config.table.updateData();
    });
  }

}