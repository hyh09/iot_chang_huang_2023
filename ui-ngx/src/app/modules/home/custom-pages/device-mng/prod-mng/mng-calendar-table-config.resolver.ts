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
      rename: ''
    }

    this.config.columns.push(
      new EntityTableColumn<ProdCalendar>('startTime', 'device-mng.device-schedual', '50%', (entity) => (
        `${this.datePipe.transform(entity.startTime, 'yyyy-MM-dd HH:mm')} ~ ${this.datePipe.transform(entity.endTime, 'yyyy-MM-dd HH:mm')}`
      ), () => ({}), false),
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
      rename: decodeURIComponent(deviceName)
    });

    this.config.searchEnabled = false;
    this.config.tableTitle = `${decodeURIComponent(factoryName || '')}${this.translate.instant('common.colon')}${decodeURIComponent(deviceName || '')}`;
    this.config.deleteEntityTitle = () => this.translate.instant('device-mng.delete-prod-calendar-title');
    this.config.deleteEntitiesTitle = count => this.translate.instant('device-mng.delete-prod-calendar-title', {count});

    this.config.entitiesFetchFunction = pageLink => this.prodMngService.getProdCalendarList(pageLink, deviceId);
    this.config.saveEntity = calendar => {
      const { date, start, end } = calendar;
      const _date = new Date(new Date(new Date(date).toLocaleDateString()));
      _date.setHours(start.getHours());
      _date.setMinutes(start.getMinutes());
      const startTime = _date.getTime();
      _date.setHours(end.getHours());
      _date.setMinutes(end.getMinutes());
      const endTime = _date.getTime();
      return this.prodMngService.saveProdCalendar({
        ...calendar, factoryId, deviceId, rename: decodeURIComponent(deviceName), startTime, endTime
      });
    }
    this.config.loadEntity = id => this.prodMngService.getProdCalendar(id);
    this.config.deleteEntity = id => this.prodMngService.deleteProdCalendar(id);

    return this.config;
  }

}
