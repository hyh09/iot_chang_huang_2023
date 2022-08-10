import { DeviceCapacity } from './../../../../../shared/models/custom/potency.models';
import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve } from "@angular/router";
import { PotencyService } from "@app/core/http/custom/potency.service";
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources, TimePageLink } from "@app/shared/public-api";
import { TranslateService } from "@ngx-translate/core";
import { ProductionCapacityOverviewComponent } from './production-capacity-overview.component';
import { getTheEndOfDay, getTheStartOfDay } from '@app/core/utils';
import { DatePipe } from '@angular/common';
import { BehaviorSubject } from 'rxjs';

@Injectable()
export class ProductionHistoryCapacityTableConfigResolver implements Resolve<EntityTableConfig<DeviceCapacity>> {

  private readonly config: EntityTableConfig<DeviceCapacity> = new EntityTableConfig<DeviceCapacity>();

  private deviceId: string = '';
  private deviceIdLoaded$: BehaviorSubject<string> = new BehaviorSubject<string>('');

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
      deviceIdLoaded$: this.deviceIdLoaded$,
      deviceName: '',
      timePageLink: null,
      exportTableData: null
    }

    this.config.columns.push(
      new EntityTableColumn<DeviceCapacity>('rename', this.translate.instant('potency.device-name'), '50%', (entity) => (entity.rename || ''), () => ({}), false),
      new EntityTableColumn<DeviceCapacity>('value', this.translate.instant('potency.capacity'), '50%'),
      new DateEntityTableColumn<DeviceCapacity>('createdTime', this.translate.instant('potency.created-time'), this.datePipe, '150px', 'yyyy-MM-dd HH:mm:ss', false),
    );
  }

  resolve(route: ActivatedRouteSnapshot): EntityTableConfig<DeviceCapacity> {
    this.deviceId = route.params.deviceId;
    this.config.componentsData.deviceName = decodeURIComponent(route.queryParams.deviceName || '');
    this.config.componentsData.deviceIdLoaded$.next(this.deviceId);

    const now = new Date();
    this.config.componentsData.dateRange = [getTheStartOfDay(now, false), getTheEndOfDay(now, false)];

    this.config.tableTitle = this.translate.instant('potency.device-capacity-history');
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;

    this.config.entitiesFetchFunction = pageLink => {
      let startTime: number, endTime: number;
      if (this.config.componentsData.dateRange) {
        startTime = (this.config.componentsData.dateRange[0] as Date).getTime();
        endTime = (this.config.componentsData.dateRange[1] as Date).getTime();
      }
      const { pageSize, page, textSearch, sortOrder } = pageLink;
      const timePageLink = new TimePageLink(pageSize, page, textSearch, sortOrder, startTime, endTime);
      this.config.componentsData.timePageLink = timePageLink;
      return this.potencyService.getDeviceCapacityHistoryList(timePageLink, this.deviceId);
    }

    this.config.componentsData.exportTableData = () => {
      const { timePageLink } = this.config.componentsData;
      this.potencyService.exportDeviceCapacityHistoryList(timePageLink, this.deviceId).subscribe();
    }

    return this.config;

  }

}