import { Injectable } from "@angular/core";
import { Resolve, Router } from "@angular/router";
import { PotencyService } from "@app/core/http/custom/potency.service";
import { EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources, TimePageLink } from "@app/shared/public-api";
import { TranslateService } from "@ngx-translate/core";
import { FactoryTreeComponent } from '@app/modules/home/components/factory-tree/factory-tree.component';
import { map } from 'rxjs/operators';
import { BehaviorSubject, Observable, Observer } from "rxjs";
import { EnergyConsumptionOverviewComponent } from './energy-consumption-overview.component';
import { getTheEndOfDay, getTheStartOfDay } from "@app/core/utils";
import { DeviceEnergyConsumption } from "@app/shared/models/custom/potency.models";

@Injectable()
export class EnergyConsumptionTableConfigResolver implements Resolve<EntityTableConfig<DeviceEnergyConsumption>> {

  private readonly config: EntityTableConfig<DeviceEnergyConsumption> = new EntityTableConfig<DeviceEnergyConsumption>();

  constructor(
    private potencyService: PotencyService,
    private translate: TranslateService,
    private router: Router
  ) {
    this.config.entityType = EntityType.POTENCY;
    this.config.leftComponent = FactoryTreeComponent;
    this.config.filterComponent = EnergyConsumptionOverviewComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.POTENCY);
    this.config.entityResources = entityTypeResources.get(EntityType.POTENCY);
    
    this.config.columns.push(new EntityTableColumn<DeviceEnergyConsumption>('rename', 'potency.device-name', '200px', entity => entity.rename || '', () => ({}), false));
    this.config.columns.push(new EntityTableColumn<DeviceEnergyConsumption>('waterConsumption', 'potency.water-consumption', '100px', entity => entity.waterConsumption || '', () => ({}), false));
    this.config.columns.push(new EntityTableColumn<DeviceEnergyConsumption>('electricConsumption', 'potency.electric-consumption', '100px', entity => entity.electricConsumption || '', () => ({}), false));
    this.config.columns.push(new EntityTableColumn<DeviceEnergyConsumption>('gasConsumption', 'potency.gas-consumption', '100px', entity => entity.gasConsumption || '', () => ({}), false));
    this.config.columns.push(new EntityTableColumn<DeviceEnergyConsumption>('capacityConsumption', 'potency.capacity', '100px', entity => entity.capacityConsumption || '', () => ({}), false));
    this.config.columns.push(new EntityTableColumn<DeviceEnergyConsumption>('unitWaterConsumption', 'potency.unit-water-consumption', '', entity => entity.unitWaterConsumption || '', () => ({}), false));
    this.config.columns.push(new EntityTableColumn<DeviceEnergyConsumption>('unitElectricConsumption', 'potency.unit-electric-consumption', '', entity => entity.unitElectricConsumption || '', () => ({}), false));
    this.config.columns.push(new EntityTableColumn<DeviceEnergyConsumption>('unitGasConsumption', 'potency.unit-gas-consumption', '', entity => entity.unitGasConsumption || '', () => ({}), false));

    this.config.cellActionDescriptors = [{
      name: this.translate.instant('potency.go-to-history'),
      mdiIcon: 'mdi:history-data',
      isEnabled: () => (true),
      onAction: ($event, entity) => this.router.navigate([`/potency/energyConsumption/${entity.deviceId}/history`], {
        queryParams: {
          deviceName: encodeURIComponent(entity.rename)
        }
      })
    }];
  }

  resolve(): EntityTableConfig<DeviceEnergyConsumption> {
    this.config.titleVisible = false;
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;

    const now = new Date();
    this.config.componentsData = {
      factoryId: '',
      workshopId: '',
      productionLineId: '',
      deviceId: '',
      dateRange: [getTheStartOfDay(now, false), getTheEndOfDay(now, false)],
      totalValue: {},
      factroryChange$: new BehaviorSubject<string>('')
    };

    this.config.entitiesFetchFunction = pageLink => {
      const { factoryId, workshopId, productionLineId, deviceId } = this.config.componentsData;
      if (factoryId) {
        this.config.componentsData.factroryChange$.next(factoryId);
      }
      let startTime: number, endTime: number;
      if (this.config.componentsData.dateRange) {
        startTime = (this.config.componentsData.dateRange[0] as Date).getTime();
        endTime = (this.config.componentsData.dateRange[1] as Date).getTime();
      }
      const { pageSize, page, textSearch, sortOrder } = pageLink;
      const timePageLink = new TimePageLink(pageSize, page, textSearch, sortOrder, startTime, endTime);
      return this.potencyService.getEnergyConsumptionDatas(timePageLink, { factoryId, workshopId, productionLineId, deviceId }).pipe(map(res => {
        this.config.componentsData.totalValue = res.totalValue || {};
        return res;
      }));
    }

    return this.config;
  }

}