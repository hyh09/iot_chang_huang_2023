import { DeviceCapacity } from './../../../../../shared/models/custom/potency.models';
import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve } from "@angular/router";
import { PotencyService } from "@app/core/http/custom/potency.service";
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources, TimePageLink } from "@app/shared/public-api";
import { TranslateService } from "@ngx-translate/core";
import { map } from 'rxjs/operators';
import { ProductionCapacityOverviewComponent } from './production-capacity-overview.component';
import { getTheEndOfDay, getTheStartOfDay } from '@app/core/utils';
import { DatePipe } from '@angular/common';

@Injectable()
export class ProductionHistoryCapacityTableConfigResolver implements Resolve<EntityTableConfig<DeviceCapacity>> {

  private readonly config: EntityTableConfig<DeviceCapacity> = new EntityTableConfig<DeviceCapacity>();

  private deviceId: string = '';

  constructor(
    private potencyService: PotencyService,
    private translate: TranslateService,
    private datePipe: DatePipe
  ) {
    this.config.entityType = EntityType.POTENCY_HISTORY;
    this.config.filterComponent = ProductionCapacityOverviewComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.POTENCY_HISTORY);
    this.config.entityResources = entityTypeResources.get(EntityType.POTENCY_HISTORY);

    this.config.componentsData = {
      dateRange: null,
      totalCapacity: 0
    }

    this.config.columns.push(
      new EntityTableColumn<DeviceCapacity>('deviceName', this.translate.instant('potency.device-name'), '50%', (entity) => (entity.deviceName), () => ({}), false),
      new EntityTableColumn<DeviceCapacity>('value', this.translate.instant('potency.capacity'), '50%'),
      new DateEntityTableColumn<DeviceCapacity>('createdTime', this.translate.instant('potency.created-time'), this.datePipe, '150px'),
    );
  }

  resolve(route: ActivatedRouteSnapshot): EntityTableConfig<DeviceCapacity> {

    this.deviceId = route.params.deviceId;

    const now = new Date();
    this.config.componentsData = {
      dateRange: [now, now],
      totalCapacity: 0
    }

    this.config.tableTitle = this.translate.instant('potency.device-capacity');
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;

    this.config.entitiesFetchFunction = pageLink => {
      let startTime: number, endTime: number;
      if (this.config.componentsData.dateRange) {
        startTime = (getTheStartOfDay(this.config.componentsData.dateRange[0] as Date) as number);
        endTime = (getTheEndOfDay(this.config.componentsData.dateRange[1] as Date) as number);
      }
      const { pageSize, page, textSearch, sortOrder } = pageLink;
      const timePageLink = new TimePageLink(pageSize, page, textSearch, sortOrder, startTime, endTime);
      return this.potencyService.getDeviceCapacityHistoryList(timePageLink, this.deviceId).pipe(map(res => {
        this.config.componentsData.totalCapacity = res.totalValue || 0;
        return res;
      }));
    }

    return this.config;

  }

}