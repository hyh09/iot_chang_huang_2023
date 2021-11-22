import { DatePipe } from "@angular/common";
import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve } from "@angular/router";
import { RealTimeMonitorService } from "@app/core/http/custom/real-time-monitor.service";
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources } from "@app/shared/public-api";
import { Observable, Observer } from "rxjs";

@Injectable()
export class DeviceHistoryTableConfigResolver implements Resolve<EntityTableConfig<object>> {

  private readonly config: EntityTableConfig<object> = new EntityTableConfig<object>();

  private deviceId: string = '';
  private deviceName: string = '';

  constructor(
    private datePipe: DatePipe,
    private realTimeMonitorService: RealTimeMonitorService
  ) {
    this.config.entityType = EntityType.DEVICE_HISTORY;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.DEVICE_HISTORY);
    this.config.entityResources = entityTypeResources.get(EntityType.DEVICE_HISTORY);

    this.config.columns.push(
      new DateEntityTableColumn<object>('createdTime', 'common.created-time', this.datePipe, '150px')
    );
  }

  resolve(route: ActivatedRouteSnapshot): Observable<EntityTableConfig<object>> {
    this.deviceId = route.parent.params.deviceId;
    this.deviceName = route.queryParams.deviceName;

    return new Observable((observer: Observer<EntityTableConfig<object>>) => {
      this.realTimeMonitorService.getDeviceHistoryTableHeader(this.deviceId).subscribe(res => {
        this.config.tableTitle = this.deviceName;
        this.config.addEnabled = false;
        this.config.searchEnabled = false;
        this.config.refreshEnabled = false;
        this.config.detailsPanelEnabled = false;
        this.config.entitiesDeleteEnabled = false;
        this.config.selectionEnabled = false;
        this.config.useTimePageLink = true;
        this.config.timeWindowInFilter = true

        this.config.columns.splice(1, this.config.columns.length - 1);
        (res || []).forEach(col => {
          if (col.name !== 'createdTime') {
            this.config.columns.push(new EntityTableColumn<object>(col.name, col.name, '', (entity) => (entity[col.name]), () => ({}), false));
          }
        });

        this.config.entitiesFetchFunction = pageLink => this.realTimeMonitorService.getDeviceHistoryDatas(pageLink, this.deviceId);

        observer.next(this.config);
        observer.complete();
      });
    });
  }

}