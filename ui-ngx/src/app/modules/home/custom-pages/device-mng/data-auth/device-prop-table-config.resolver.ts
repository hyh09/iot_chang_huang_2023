import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve } from "@angular/router";
import { EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources } from "@app/shared/public-api";
import { TranslateService } from "@ngx-translate/core";
import { DeviceAuthProp } from "@app/shared/models/custom/device-mng.models";
import { FactoryMngService } from "@app/core/public-api";
import { DevicePropFiltersComponent } from "./device-prop-filters.component";

@Injectable()
export class DevicePropTableConfigResolver implements Resolve<EntityTableConfig<DeviceAuthProp>> {

  private readonly config: EntityTableConfig<DeviceAuthProp> = new EntityTableConfig<DeviceAuthProp>();

  constructor(
    private translate: TranslateService,
    private factoryMngService: FactoryMngService
  ) {
    this.config.entityType = EntityType.CHART_SETTINGS;
    this.config.filterComponent = DevicePropFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.CHART_SETTINGS);
    this.config.entityResources = entityTypeResources.get(EntityType.CHART_SETTINGS);

    this.config.componentsData = {
      deviceId: '',
      propertyTitle: ''
    }

    this.config.columns.push(
      new EntityTableColumn<DeviceAuthProp>('propertyName', 'device-mng.device-data-name', '150px', ({propertyName}) => propertyName || '', () => ({}), false),
      new EntityTableColumn<DeviceAuthProp>('propertyTitle', 'device-mng.device-data-desc', '150px'),
      new EntityTableColumn<DeviceAuthProp>('propertyType', 'device-mng.property-type', '150px',
      ({propertyType}) => propertyType === 'DEVICE' ? this.translate.instant('device-mng.device-property') : propertyType === 'COMPONENT' ? this.translate.instant('device-mng.component-property') : ''),
      new EntityTableColumn<DeviceAuthProp>('propertySwitch', 'device-mng.visible-to-factory', '150px', () => (''), () => ({ overflow: 'visible' }), false, () => ({}),
      () => undefined, false, null, () => false, true, (entity) => {
        this.factoryMngService.switchDevicePropFactoryVisible([entity]).subscribe();
      })
    );
  }

  resolve(route: ActivatedRouteSnapshot): EntityTableConfig<DeviceAuthProp> {

    this.config.componentsData = {
      deviceId: route.params.deviceId || '',
      propertyTitle: ''
    }

    this.config.tableTitle = this.translate.instant('device-mng.mng-prop-auth');
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;

    this.config.entitiesFetchFunction = pageLink => this.factoryMngService.getDeviceProperties(pageLink, this.config.componentsData);

    return this.config;

  }

}