import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve } from "@angular/router";
import { PotencyService } from "@app/core/http/custom/potency.service";
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources, TimePageLink } from "@app/shared/public-api";
import { TranslateService } from "@ngx-translate/core";
import { BehaviorSubject, Observable, Observer } from "rxjs";
import { DatePipe } from "@angular/common";
import { getTheStartOfDay, getTheEndOfDay } from "@app/core/utils";
import { EnergyHistoryFilterComponent } from "./energy-history-filter.component";
import { DeviceEnergyConsumption } from "@app/shared/models/custom/potency.models";

@Injectable()
export class EnergyHistoryTableConfigResolver implements Resolve<EntityTableConfig<DeviceEnergyConsumption>> {

  private readonly config: EntityTableConfig<DeviceEnergyConsumption> = new EntityTableConfig<DeviceEnergyConsumption>();

  private deviceId: string = '';
  private deviceIdLoaded$: BehaviorSubject<string> = new BehaviorSubject<string>('');

  constructor(
    private potencyService: PotencyService,
    private translate: TranslateService,
    private datePipe: DatePipe
  ) {
    this.config.entityType = EntityType.DEVICE_HISTORY;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.DEVICE_HISTORY);
    this.config.entityResources = entityTypeResources.get(EntityType.DEVICE_HISTORY);

    this.config.filterComponent = EnergyHistoryFilterComponent;

    this.config.tableTitle = this.translate.instant('potency.energy-consumption-history');
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;

    this.config.columns.push(new EntityTableColumn<DeviceEnergyConsumption>('deviceName', 'potency.device-name', '200px', entity => entity.deviceName || '', () => ({}), false));
    this.config.columns.push(new EntityTableColumn<DeviceEnergyConsumption>('waterConsumption', 'potency.water-consumption', '100px', entity => entity.waterConsumption || '', () => ({}), false));
    this.config.columns.push(new EntityTableColumn<DeviceEnergyConsumption>('electricConsumption', 'potency.electric-consumption', '100px', entity => entity.electricConsumption || '', () => ({}), false));
    this.config.columns.push(new EntityTableColumn<DeviceEnergyConsumption>('gasConsumption', 'potency.gas-consumption', '100px', entity => entity.gasConsumption || '', () => ({}), false));
    this.config.columns.push(new DateEntityTableColumn<DeviceEnergyConsumption>('createdTime', 'potency.created-time', this.datePipe, '150px', 'yyyy-MM-dd HH:mm:ss', false));

    this.config.componentsData = {
      dateRange: null,
      deviceIdLoaded$: this.deviceIdLoaded$,
      deviceName: ''
    };
  }

  resolve(route: ActivatedRouteSnapshot): EntityTableConfig<DeviceEnergyConsumption> {
    this.deviceId = route.params.deviceId;
    this.config.componentsData.deviceName = decodeURIComponent(route.queryParams.deviceName || '');
    this.config.componentsData.deviceIdLoaded$.next(this.deviceId);
    const now = new Date();
    this.config.componentsData.dateRange = [getTheStartOfDay(now, false), getTheEndOfDay(now, false)];

    this.config.entitiesFetchFunction = pageLink => {
      let startTime: number, endTime: number;
      if (this.config.componentsData.dateRange) {
        startTime = (this.config.componentsData.dateRange[0] as Date).getTime();
        endTime = (this.config.componentsData.dateRange[1] as Date).getTime();
      }
      const { pageSize, page, textSearch, sortOrder } = pageLink;
      const timePageLink = new TimePageLink(pageSize, page, textSearch, sortOrder, startTime, endTime);
      return this.potencyService.getEnergyHistoryDatas(timePageLink, this.deviceId);
    }

    return this.config;
  }

}