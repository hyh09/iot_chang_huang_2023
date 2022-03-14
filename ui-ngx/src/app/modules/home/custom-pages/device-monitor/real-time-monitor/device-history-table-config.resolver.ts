import { DatePipe } from "@angular/common";
import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve } from "@angular/router";
import { RealTimeMonitorService } from "@app/core/http/custom/real-time-monitor.service";
import { getTheEndOfDay, getTheStartOfDay } from "@app/core/utils";
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources, TimePageLink } from "@app/shared/public-api";
import { Observable, Observer } from "rxjs";
import { DeviceHistoryFilterComponent } from "./device-history-filter.component";

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

    this.config.filterComponent = DeviceHistoryFilterComponent;

    this.config.columns.push(
      new DateEntityTableColumn<object>('createdTime', 'common.created-time', this.datePipe, '150px', 'yyyy-MM-dd HH:mm:ss', false)
    );
  }

  resolve(route: ActivatedRouteSnapshot): Observable<EntityTableConfig<object>> {
    this.deviceId = route.parent.params.deviceId;
    this.deviceName = route.queryParams.deviceName;

    return new Observable((observer: Observer<EntityTableConfig<object>>) => {
      this.realTimeMonitorService.getDeviceHistoryTableHeader(this.deviceId).subscribe(res => {
        let headers = res || [];

        this.config.tableTitle = this.deviceName;
        this.config.addEnabled = false;
        this.config.searchEnabled = false;
        this.config.detailsPanelEnabled = false;
        this.config.entitiesDeleteEnabled = false;
        this.config.selectionEnabled = false;

        const now = new Date();
        this.config.componentsData = {
          dateRange: [getTheStartOfDay(now), getTheEndOfDay(now)]
        };

        this.realTimeMonitorService.getDeviceRelatedParams(this.deviceId).subscribe(_res => {
          const relatedHeaderNames = [];
          (_res || []).forEach(item => {
            if (item.properties && item.properties.length > 0) {
              headers.splice(0, 0, item);
              relatedHeaderNames.push(...(item.properties.map(prop => (prop.name))));
            }
          });
          headers = headers.filter(header => (!relatedHeaderNames.includes(header.name)));

          this.config.columns.splice(1, this.config.columns.length - 1);
          headers.forEach(col => {
            if (col.name !== 'createdTime') {
              if (col.properties) {
                this.config.columns.push(new EntityTableColumn<object>(col.name, col.name, '', (entity) => {
                  let content = '';
                  col.properties.forEach(prop => (content += `${entity[prop.name] || ''}${prop.unit || ''}${prop.suffix || ''}`));
                  return content;
                }, () => ({}), false));
              } else {
                this.config.columns.push(new EntityTableColumn<object>(col.name, col.title || col.name, '', (entity) => (entity[col.name] || ''), () => ({}), false));
              }
            }
          });

          this.config.entitiesFetchFunction = pageLink => {
            let startTime: number, endTime: number;
            if (this.config.componentsData.dateRange) {
              startTime = (getTheStartOfDay(this.config.componentsData.dateRange[0] as Date) as number);
              endTime = (getTheEndOfDay(this.config.componentsData.dateRange[1] as Date) as number);
            }
            const { pageSize, page, textSearch, sortOrder } = pageLink;
            const timePageLink = new TimePageLink(pageSize, page, textSearch, sortOrder, startTime, endTime);
            return this.realTimeMonitorService.getDeviceHistoryDatas(timePageLink, this.deviceId);
          };

          observer.next(this.config);
          observer.complete();
        });
      });
    });
  }

}