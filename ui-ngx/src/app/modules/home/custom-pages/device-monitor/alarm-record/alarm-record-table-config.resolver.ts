import { DatePipe } from "@angular/common";
import { Injectable } from "@angular/core";
import { Resolve } from "@angular/router";
import { AlarmRecordService } from "@app/core/http/custom/alarm-record.service";
import { FactoryTreeComponent } from "@app/modules/home/components/factory-tree/factory-tree.component";
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
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
    private alarmRecordService: AlarmRecordService
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
      startTime: '',
      endTime: '',
      status: AlarmStatusType.ANY,
      level: AlarmLevelType.ANY
    }

    this.config.columns.push(
      new DateEntityTableColumn<AlarmRecord>('createdTime', 'common.created-time', this.datePipe, '150px'),
      new EntityTableColumn<AlarmRecord>('name', 'device-monitor.device-name', '25%'),
      new EntityTableColumn<AlarmRecord>('title', 'device-monitor.alarm-title', '25%'),
      new EntityTableColumn<AlarmRecord>('info', 'device-monitor.alarm-info', '50%'),
      new EntityTableColumn<AlarmRecord>('statusStr', 'device-monitor.alarm-status', '80px', ({statusStr}) => {
        return this.translate.instant(`device-monitor.${statusStr}`);
      }),
      new EntityTableColumn<AlarmRecord>('levelStr', 'device-monitor.alarm-level', '80px', ({levelStr}) => {
        return this.translate.instant(`device-monitor.${levelStr}`);
      })
    );
  }

  resolve(): EntityTableConfig<AlarmRecord> {
    this.config.componentsData = {
      factoryId: '',
      workshopId: '',
      productionLineId: '',
      deviceId: '',
      startTime: '',
      endTime: '',
      status: AlarmStatusType.ANY,
      level: AlarmLevelType.ANY
    }

    this.config.tableTitle = this.translate.instant('device-monitor.alarm-record');
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;

    this.config.entitiesFetchFunction = pageLink => this.alarmRecordService.getAlarmRecords(pageLink, this.config.componentsData);

    return this.config;
  }

}