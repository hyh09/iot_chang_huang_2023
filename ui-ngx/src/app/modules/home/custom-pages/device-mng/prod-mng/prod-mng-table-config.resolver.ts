import { Router } from '@angular/router';
import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { CellActionDescriptor, DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations, HasUUID } from "@app/shared/public-api";
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { UtilsService } from "@app/core/public-api";
import { ProdMng } from "@app/shared/models/custom/device-mng.models";
import { ProdMngFiltersComponent } from "./prod-mng-filters.component";
import { ProdMngService } from "@app/core/http/custom/prod-mng.service";
import { Direction } from '@shared/models/page/sort-order';

@Injectable()
export class ProdMngTableConfigResolver implements Resolve<EntityTableConfig<ProdMng>> {

  private readonly config: EntityTableConfig<ProdMng> = new EntityTableConfig<ProdMng>();

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private prodMngService: ProdMngService,
    private utils: UtilsService,
    private router: Router
  ) {
    this.config.entityType = EntityType.PROD_MNG;
    this.config.filterComponent = ProdMngFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.PROD_MNG);
    this.config.entityResources = entityTypeResources.get(EntityType.PROD_MNG);

    this.config.componentsData = {
      factoryName: '',
      deviceName: ''
    }
  }

  resolve(): EntityTableConfig<ProdMng> {
    this.config.columns = [
      new EntityTableColumn<ProdMng>('factoryName', 'device-mng.factory-name', '33.333333%'),
      new EntityTableColumn<ProdMng>('deviceName', 'device-mng.device-name', '33.333333%'),
      new DateEntityTableColumn<ProdMng>('startTime', 'datetime.time-from', this.datePipe, '150px'),
      new DateEntityTableColumn<ProdMng>('endTime', 'datetime.time-to', this.datePipe, '150px')
    ];

    this.config.defaultSortOrder.property = 'endTime';
    this.config.defaultSortOrder.direction = Direction.DESC;

    this.config.componentsData = {
      factoryName: '',
      deviceName: ''
    }

    this.config.tableTitle = this.translate.instant('device-mng.prod-mng');
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.afterResolved = () => {
      this.config.addEnabled = false;
      this.config.entitiesDeleteEnabled = false;
      this.config.detailsPanelEnabled = false;
      this.config.cellActionDescriptors = this.configureCellActions();
    }

    this.config.entitiesFetchFunction = pageLink => this.prodMngService.getProdMngList(pageLink, this.config.componentsData);

    return this.config;
  }

  configureCellActions(): Array<CellActionDescriptor<ProdMng>> {
    const actions: Array<CellActionDescriptor<ProdMng>> = [];
    if (this.utils.hasPermission('device-mng.mng-calendars')) {
      actions.push({
        name: this.translate.instant('device-mng.mng-calendars'),
        mdiIcon: 'mdi:calendar',
        isEnabled: (entity) => (!!(entity && entity.deviceId)),
        onAction: ($event, entity) => this.mngCalendars($event, entity)
      });
    }
    return actions;
  }

  mngCalendars($event: Event, entity: ProdMng): void {
    if ($event) {
      $event.stopPropagation();
    }
    const { factoryId, deviceId, factoryName, deviceName } = entity || {};
    this.router.navigateByUrl(`deviceManagement/prodManagement/${factoryId}/${deviceId}/calendars?factoryName=${encodeURIComponent(factoryName)}&deviceName=${encodeURIComponent(deviceName)}`);
  }

}
