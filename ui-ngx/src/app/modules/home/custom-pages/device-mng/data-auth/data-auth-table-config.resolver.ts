import { Injectable } from "@angular/core";
import { Resolve, Router } from '@angular/router';
import { CellActionDescriptor, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations } from "@app/shared/public-api";
import { TranslateService } from '@ngx-translate/core';
import { DeviceDataAuth } from '@app/shared/models/custom/device-mng.models';
import { FactoryMngService } from '@app/core/public-api';
import { DataAuthFiltersComponent } from "./data-auth-filters.component";

@Injectable()
export class DataAuthTableConfigResolver implements Resolve<EntityTableConfig<DeviceDataAuth>> {

  private readonly config: EntityTableConfig<DeviceDataAuth> = new EntityTableConfig<DeviceDataAuth>();

  constructor(
    private translate: TranslateService,
    private factoryMngService: FactoryMngService,
    private router: Router
  ) {
    this.config.entityType = EntityType.DEVICE;
    this.config.entityComponent = null;
    this.config.filterComponent = DataAuthFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.DEVICE);
    this.config.entityResources = entityTypeResources.get(EntityType.DEVICE);

    this.config.componentsData = { factoryId: '', deviceName: '' };

    this.config.columns.push(
      new EntityTableColumn<DeviceDataAuth>('deviceName', 'device-mng.device-name', '200px', ({deviceName}) => deviceName || '', () => ({}), false),
      new EntityTableColumn<DeviceDataAuth>('factoryName', 'device-mng.factory', '200px', ({factoryName}) => factoryName || '', () => ({}), false),
      new EntityTableColumn<DeviceDataAuth>('productionLineName', 'device-mng.work-shop', '200px', ({productionLineName}) => productionLineName || '', () => ({}), false),
      new EntityTableColumn<DeviceDataAuth>('workshopName', 'device-mng.prod-line', '200px', ({workshopName}) => workshopName || '', () => ({}), false)
    );
  }

  resolve(): EntityTableConfig<DeviceDataAuth> {
    this.config.componentsData = { factoryId: '', deviceName: '' };

    this.config.tableTitle = this.translate.instant('device-mng.data-auth');
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.deleteEnabled = () => false;
    this.config.entitiesDeleteEnabled = false;
    this.config.detailsPanelEnabled = false;

    this.config.entitiesFetchFunction = (pageLink) => this.factoryMngService.getDataAuthDevices(pageLink, this.config.componentsData);

    this.config.cellActionDescriptors = this.configureCellActions();

    return this.config;
  }

  configureCellActions(): Array<CellActionDescriptor<DeviceDataAuth>> {
    const actions: Array<CellActionDescriptor<DeviceDataAuth>> = [];
    actions.push({
      name: this.translate.instant('device-mng.mng-prop-auth'),
      mdiIcon: 'mdi:switch-settings',
      isEnabled: (entity) => (!!(entity && entity.deviceId)),
      onAction: ($event, entity) => {
        this.router.navigate([`/deviceManagement/dataAuth/${entity.deviceId}/properties`], {
          queryParams: {
            deviceName: encodeURIComponent(entity.deviceName)
          }
        });
      }
    });
    return actions;
  }

}
