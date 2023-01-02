import { DeviceCapacity } from '../../../../../../shared/models/custom/potency.models';
import { Injectable } from "@angular/core";
import { Resolve, Router } from "@angular/router";
import { PotencyService } from "@app/core/http/custom/potency.service";
import { EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources, TimePageLink } from "@app/shared/public-api";
import { TranslateService } from "@ngx-translate/core";
import { FactoryTreeComponent } from '@app/modules/home/components/factory-tree/factory-tree.component';
import { map } from 'rxjs/operators';
import { ProductionCapacityOverviewComponent } from './production-capacity-overview.component';
import { getTheEndOfDay, getTheStartOfDay } from '@app/core/utils';
import { BehaviorSubject } from 'rxjs';

@Injectable()
export class ProductionCapacityTableConfigResolver implements Resolve<EntityTableConfig<DeviceCapacity>> {

  private readonly config: EntityTableConfig<DeviceCapacity> = new EntityTableConfig<DeviceCapacity>();

  constructor(
    private potencyService: PotencyService,
    private translate: TranslateService,
    private router: Router
  ) {
    this.config.entityType = EntityType.POTENCY;
    this.config.leftComponent = FactoryTreeComponent;
    this.config.filterComponent = ProductionCapacityOverviewComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.POTENCY);
    this.config.entityResources = entityTypeResources.get(EntityType.POTENCY);

    this.config.columns.push(
      new EntityTableColumn<DeviceCapacity>('rename', 'potency.device-name', '50%', (entity) => (entity.rename || ''), () => ({}), false),
      new EntityTableColumn<DeviceCapacity>('value', 'potency.capacity', '50%')
    );
  }

  resolve(): EntityTableConfig<DeviceCapacity> {

    const now = new Date();
    this.config.componentsData = {
      factoryId: '',
      workshopId: '',
      productionLineId: '',
      deviceId: '',
      dateRange: [getTheStartOfDay(now, false), getTheEndOfDay(now, false)],
      totalCapacity: 0,
      factroryChange$: new BehaviorSubject<string>(''),
      timePageLink: null
    }
    this.config.titleVisible = false;
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;
    this.config.loadDataOnInit = false;

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
      this.config.componentsData.timePageLink = timePageLink;
      return this.potencyService.getDeviceCapacityList(timePageLink, { factoryId, workshopId, productionLineId, deviceId }).pipe(map(res => {
        this.config.componentsData.totalCapacity = res.totalValue || 0;
        return res;
      }));
    }

    this.config.cellActionDescriptors = [{
      name: this.translate.instant('potency.go-to-history'),
      mdiIcon: 'mdi:history-data',
      isEnabled: () => (true),
      onAction: ($event, entity) => this.router.navigate([`/potency/outputAnalysis/factory/${entity.deviceId}/history`], {
        queryParams: {
          deviceName: encodeURIComponent(entity.rename)
        }
      })
    }];

    return this.config;

  }

}