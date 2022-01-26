import { ActivatedRouteSnapshot } from '@angular/router';
import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations } from "@app/shared/public-api";
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { UtilsService } from '@app/core/public-api';
import { Chart } from '@app/shared/models/custom/chart-settings.model';
import { ChartsComponent } from './charts.component';
import { ChartSettingsService } from '@app/core/http/custom/chart-settings.service';

@Injectable()
export class ChartsTableConfigResolver implements Resolve<EntityTableConfig<Chart>> {

  private readonly config: EntityTableConfig<Chart> = new EntityTableConfig<Chart>();

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private chartSettingsService: ChartSettingsService,
    private utils: UtilsService
  ) {
    this.config.entityType = EntityType.CHART;
    this.config.entityComponent = ChartsComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.CHART);
    this.config.entityResources = entityTypeResources.get(EntityType.CHART);

    this.config.deleteEntityTitle = dataDic => this.translate.instant('device-mng.delete-data-dic-title', {dataDicName: dataDic.name});
    this.config.deleteEntityContent = () => this.translate.instant('device-mng.delete-data-dic-text');
    this.config.deleteEntitiesTitle = count => this.translate.instant('device-mng.delete-data-dics-title', {count});
    this.config.deleteEntitiesContent = () => this.translate.instant('device-mng.delete-data-dics-text');

    this.config.componentsData = { dictDeviceId: '' };

    this.config.columns.push(
      new EntityTableColumn<Chart>('name', 'device-mng.chart-title', '200px'),
      new EntityTableColumn<Chart>('enable', 'device-mng.chart-visible', '150px', entity => (
        this.translate.instant(entity.enable ? 'action.yes' : 'action.no'))),
      new EntityTableColumn<Chart>('properties', 'device-mng.device-data', '100%', entity => (
        (entity.properties || []).map(prop => { prop.name }).join('、'))),
      new DateEntityTableColumn<Chart>('createdTime', 'common.created-time', this.datePipe, '150px')
    );
  }

  resolve(route: ActivatedRouteSnapshot): EntityTableConfig<Chart> {
    this.config.componentsData = { dictDeviceId: '' };

    const deviceDictName = decodeURIComponent(route.queryParams.deviceDictName || '');
    const deviceDictId = route.params.deviceDictId;
    this.config.componentsData.dictDeviceId = deviceDictId;

    this.config.tableTitle = `${this.translate.instant('device-mng.bind-chart')}: ${deviceDictName}`;
    this.config.searchEnabled = false;
    this.config.afterResolved = () => {
      this.config.addEnabled = this.utils.hasPermission('action.add');
      this.config.entitiesDeleteEnabled = this.utils.hasPermission('action.delete');
      this.config.detailsReadonly = () => (!this.utils.hasPermission('action.edit'));
    }

    this.config.entitiesFetchFunction = () => this.chartSettingsService.getCharts(deviceDictId);
    this.config.loadEntity = id => this.chartSettingsService.getChart(id);
    this.config.saveEntity = chart => {
      delete chart['propertyIds'];
      return this.chartSettingsService.saveChart(deviceDictId, chart);
    };
    this.config.deleteEntity = id => this.chartSettingsService.deleteChart(id);

    return this.config;
  }

}
