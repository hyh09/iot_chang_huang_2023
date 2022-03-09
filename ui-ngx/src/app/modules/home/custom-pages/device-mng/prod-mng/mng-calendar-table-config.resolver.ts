import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations } from "@app/shared/public-api";
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { MatDialog } from "@angular/material/dialog";
import { MngCalendarComponent } from "./mng-calendar.component";
import { ProdCalendar } from "@app/shared/models/custom/device-mng.models";
import { ProdMngService } from "@app/core/http/custom/prod-mng.service";

@Injectable()
export class MngCalendarTableConfigResolver implements Resolve<EntityTableConfig<ProdCalendar>>  {

  private readonly config: EntityTableConfig<ProdCalendar> = new EntityTableConfig<ProdCalendar>();

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private prodMngService: ProdMngService,
    public dialog: MatDialog
  ) {
    this.config.entityType = EntityType.MNG_CALENDAR;
    this.config.entityComponent = MngCalendarComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.MNG_CALENDAR);
    this.config.entityResources = entityTypeResources.get(EntityType.MNG_CALENDAR);

    this.config.componentsData = {
      factoryId: '',
      deviceId: '',
      factoryName: '',
      deviceName: ''
    }

    this.config.columns.push(
      new EntityTableColumn<ProdCalendar>('startTime', 'device-mng.device-schedual', '50%', (entity) => (
        `${this.datePipe.transform(entity.startTime, 'yyyy-MM-dd HH:mm')} ~ ${this.datePipe.transform(entity.endTime, 'yyyy-MM-dd HH:mm')}`
      )),
      new DateEntityTableColumn<ProdCalendar>('createdTime', 'common.created-time', this.datePipe, '25%'),
      new DateEntityTableColumn<ProdCalendar>('updatedTime', 'common.updated-time', this.datePipe, '25%')
    );
  }

  resolve(route: ActivatedRouteSnapshot): EntityTableConfig<ProdCalendar> {
    const { factoryId, deviceId } = route.params;
    const { factoryName, deviceName } = route.queryParams;
    Object.assign(this.config.componentsData, {
      factoryId, deviceId,
      factoryName: decodeURIComponent(factoryName),
      deviceName: decodeURIComponent(deviceName)
    });

    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.displayPagination = false;
    this.config.tableTitle = `${decodeURIComponent(factoryName || '')}${this.translate.instant('common.colon')}${decodeURIComponent(deviceName || '')}`;
    this.config.deleteEntityTitle = () => this.translate.instant('device-mng.delete-prod-calendar-title');
    this.config.deleteEntitiesTitle = count => this.translate.instant('device-mng.delete-prod-calendar-title', {count});

    this.config.entitiesFetchFunction = () => this.prodMngService.getProdCalendarList(this.config.componentsData.deviceId);
    this.config.saveEntity = calendar => this.prodMngService.saveProdCalendar(calendar);
    this.config.deleteEntity = id => this.prodMngService.deleteProdCalendar(id);

    return this.config;
  }

}
