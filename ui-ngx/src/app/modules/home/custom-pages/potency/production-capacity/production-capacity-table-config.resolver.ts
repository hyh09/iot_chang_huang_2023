import { DeviceCapacity } from './../../../../../shared/models/custom/potency.models';
import { Injectable } from "@angular/core";
import { Resolve } from "@angular/router";
import { PotencyService } from "@app/core/http/custom/potency.service";
import { EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources } from "@app/shared/public-api";
import { TranslateService } from "@ngx-translate/core";
import { FactoryTreeComponent } from '@app/modules/home/components/factory-tree/factory-tree.component';
import { map } from 'rxjs/operators';
import { ProductionCapacityOverviewComponent } from './production-capacity-overview.component';

@Injectable()
export class ProductionCapacityTableConfigResolver implements Resolve<EntityTableConfig<DeviceCapacity>> {

  private readonly config: EntityTableConfig<DeviceCapacity> = new EntityTableConfig<DeviceCapacity>();

  constructor(
    private potencyService: PotencyService,
    private translate: TranslateService
  ) {
    this.config.entityType = EntityType.POTENCY;
    this.config.leftComponent = FactoryTreeComponent;
    this.config.filterComponent = ProductionCapacityOverviewComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.POTENCY);
    this.config.entityResources = entityTypeResources.get(EntityType.POTENCY);

    this.config.componentsData = {
      factoryId: '',
      workshopId: '',
      productionLineId: '',
      deviceId: '',
      totalCapacity: 0
    }

    this.config.columns.push(
      new EntityTableColumn<DeviceCapacity>('deviceName', this.translate.instant('potency.device-name'), '', (entity) => (entity.deviceName), () => ({}), false),
      new EntityTableColumn<DeviceCapacity>('value', this.translate.instant('potency.capacity'))
    );
  }

  resolve(): EntityTableConfig<DeviceCapacity> {

    this.config.componentsData = {
      factoryId: '',
      workshopId: '',
      productionLineId: '',
      deviceId: '',
      totalCapacity: 0
    }

    this.config.tableTitle = this.translate.instant('potency.device-capacity');
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;
    this.config.useTimePageLink = true;
    this.config.timeWindowInFilter = true;
    this.config.loadDataOnInit = false;

    this.config.entitiesFetchFunction = pageLink => {
      const { factoryId, workshopId, productionLineId, deviceId } = this.config.componentsData;
      return this.potencyService.getDeviceCapacityList(pageLink, { factoryId, workshopId, productionLineId, deviceId }).pipe(map(res => {
        this.config.componentsData.totalCapacity = res.totalValue || 0;
        return res;
      }));
    }

    return this.config;

  }

}