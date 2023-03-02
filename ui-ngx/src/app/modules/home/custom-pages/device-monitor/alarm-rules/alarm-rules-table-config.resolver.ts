import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import {
  DateEntityTableColumn,
  EntityTableColumn,
  EntityTableConfig
} from '@home/models/entity/entities-table-config.models';
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { EntityType, entityTypeResources, entityTypeTranslations } from '@shared/models/entity-type.models';
import { AlarmRulesTabsComponent } from './alarm-rules-tabs.component';
import { MatDialog } from '@angular/material/dialog';
import { AlarmRulesComponent } from './alarm-rules.component';
import { AlarmRuleInfo } from '@app/shared/models/custom/device-monitor.models';
import { AlarmRuleService } from '@app/core/http/custom/alarm-rule.service';
import { AddAlarmRuleDialogComponent, AddAlarmRuleDialogData } from './add-alarm-rule-dialog.component';
import { AlarmRulesTableFilterComponent } from './alarm-rules-table-filter.component';
import { UtilsService } from '@app/core/public-api';

@Injectable()
export class AlarmRulesTableConfigResolver implements Resolve<EntityTableConfig<AlarmRuleInfo>> {

  private readonly config: EntityTableConfig<AlarmRuleInfo> = new EntityTableConfig<AlarmRuleInfo>();

  constructor(
    private alarmRuleService: AlarmRuleService,
    private translate: TranslateService,
    private datePipe: DatePipe,
    private dialog: MatDialog,
    private utils: UtilsService
  ) {
    this.config.entityType = EntityType.ALARM_RULES;
    this.config.entityComponent = AlarmRulesComponent;
    this.config.filterComponent = AlarmRulesTableFilterComponent;
    this.config.entityTabsComponent = AlarmRulesTabsComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.ALARM_RULES);
    this.config.entityResources = entityTypeResources.get(EntityType.ALARM_RULES);

    this.config.hideDetailsTabsOnEdit = false;
    this.config.refreshEnabled = false;
    this.config.searchEnabled = false;

    this.config.addDialogStyle = {width: '1000px'};

    this.config.componentsData = {
      name: ''
    }

    this.config.columns.push(
      new EntityTableColumn<AlarmRuleInfo>('name', 'device-profile.name', '35%'),
      new EntityTableColumn<AlarmRuleInfo>('description', 'device-profile.description', '65%'),
      new DateEntityTableColumn<AlarmRuleInfo>('createdTime', 'common.created-time', this.datePipe, '150px')
    );

    this.config.deleteEntityTitle = deviceProfile => this.translate.instant('device-monitor.delete-alarm-rule-text',
      { deviceProfileName: deviceProfile.name });
    this.config.deleteEntityContent = () => this.translate.instant('device-monitor.delete-alarm-rule-text');
    this.config.deleteEntitiesTitle = count => this.translate.instant('device-monitor.delete-alarm-rules-title', {count});
    this.config.deleteEntitiesContent = () => this.translate.instant('device-monitor.delete-alarm-rules-text');

    this.config.entitiesFetchFunction = pageLink => this.alarmRuleService.getAlarmRules(pageLink, this.config.componentsData.name);
    this.config.addEntity = () => this.addAlarmRule();
    this.config.loadEntity = id => this.alarmRuleService.getAlarmRule(id.id);
    this.config.saveEntity = alarmRuleInfo => this.alarmRuleService.saveAlarmRule(alarmRuleInfo);
    this.config.deleteEntity = id => this.alarmRuleService.deleteAlarmRule(id.id);
    this.config.deleteEnabled = (deviceProfile) => deviceProfile && !deviceProfile.default;
    this.config.entitySelectionEnabled = (deviceProfile) => deviceProfile && !deviceProfile.default;
  }

  resolve(): EntityTableConfig<AlarmRuleInfo> {
    this.config.tableTitle = this.translate.instant('device-monitor.alarm-rules');

    this.config.componentsData = {
      name: ''
    }

    this.config.refreshEnabled = false;
    this.config.searchEnabled = false;
    this.config.afterResolved = () => {
      this.config.addEnabled = this.utils.hasPermission('device-monitor.add-alarm-rule');
      this.config.entitiesDeleteEnabled = this.utils.hasPermission('action.delete');
      this.config.detailsReadonly = () => (!this.utils.hasPermission('action.edit'));
    }

    return this.config;
  }

  addAlarmRule() {
    return this.dialog.open<AddAlarmRuleDialogComponent, AddAlarmRuleDialogData, AlarmRuleInfo>(AddAlarmRuleDialogComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        deviceProfileName: null,
        transportType: null
      }
    }).afterClosed();
  }

}
