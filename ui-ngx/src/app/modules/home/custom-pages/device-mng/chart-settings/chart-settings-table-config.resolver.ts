import { Injectable } from "@angular/core";
import { Resolve, Router } from "@angular/router";
import { CellActionDescriptor, DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources } from "@app/shared/public-api";
import { TranslateService } from "@ngx-translate/core";
import { DeviceDictionary } from "@app/shared/models/custom/device-mng.models";
import { DatePipe } from "@angular/common";
import { ChartSettingsFiltersComponent } from "./chart-settings-filters.component";
import { ChartSettingsService } from "@app/core/http/custom/chart-settings.service";

@Injectable()
export class ChartSettingsTableConfigResolver implements Resolve<EntityTableConfig<DeviceDictionary>> {

  private readonly config: EntityTableConfig<DeviceDictionary> = new EntityTableConfig<DeviceDictionary>();

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private chartSettingsService: ChartSettingsService,
    private router: Router
  ) {
    this.config.entityType = EntityType.CHART_SETTINGS;
    this.config.filterComponent = ChartSettingsFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.CHART_SETTINGS);
    this.config.entityResources = entityTypeResources.get(EntityType.CHART_SETTINGS);

    this.config.componentsData = {
      code: '',
      name: ''
    }

    this.config.columns.push(
      new EntityTableColumn<DeviceDictionary>('code', 'device-mng.code', '50%'),
      new EntityTableColumn<DeviceDictionary>('name', 'device-mng.name', '50%'),
      new DateEntityTableColumn<DeviceDictionary>('createdTime', 'common.created-time', this.datePipe, '150px')
    );
  }

  resolve(): EntityTableConfig<DeviceDictionary> {

    this.config.componentsData = {
      code: '',
      name: ''
    }

    this.config.tableTitle = this.translate.instant('device-mng.chart-settings');
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;

    this.config.entitiesFetchFunction = pageLink => this.chartSettingsService.getDeviceDictionaries(pageLink, this.config.componentsData);

    this.config.cellActionDescriptors = this.configureCellActions();

    return this.config;

  }

  configureCellActions(): Array<CellActionDescriptor<DeviceDictionary>> {
    const actions: Array<CellActionDescriptor<DeviceDictionary>> = [];
    actions.push({
      name: this.translate.instant('device-mng.bind-chart'),
      mdiIcon: 'mdi:bind-chart',
      isEnabled: (entity) => (!!(entity && entity.id)),
      onAction: ($event, entity) => {
        this.router.navigate([`/deviceManagement/chartSettings/${entity.id}/charts`], {
          queryParams: {
            deviceDictName: encodeURIComponent(entity.name)
          }
        });
      }
    });
    return actions;
  }

}