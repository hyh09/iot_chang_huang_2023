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

@Injectable()
export class EnergyHistoryTableConfigResolver implements Resolve<EntityTableConfig<any>> {

  private readonly config: EntityTableConfig<any> = new EntityTableConfig<any>();

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

    this.config.componentsData = {
      dateRange: null,
      deviceIdLoaded$: this.deviceIdLoaded$,
      deviceName: ''
    };
  }

  resolve(route: ActivatedRouteSnapshot): Observable<EntityTableConfig<any>> {
    this.deviceId = route.params.deviceId;
    this.config.componentsData.deviceName = decodeURIComponent(route.queryParams.deviceName || '');
    this.config.componentsData.deviceIdLoaded$.next(this.deviceId);
    return new Observable((observer: Observer<EntityTableConfig<any>>) => {
      this.potencyService.getEnergyHistoryTableHeader().subscribe(res => {
        const now = new Date();
        this.config.componentsData.dateRange = [now, now];

        this.config.tableTitle = this.translate.instant('potency.energy-consumption-history');
        this.config.addEnabled = false;
        this.config.searchEnabled = false;
        this.config.refreshEnabled = false;
        this.config.detailsPanelEnabled = false;
        this.config.entitiesDeleteEnabled = false;
        this.config.selectionEnabled = false;

        this.config.columns = [];
        (res || []).forEach((col, index) => {
          if (col !== 'createdTime') {
            this.config.columns.push(new EntityTableColumn<any>(col, col, index === 0 ? '200px' : '', (entity) => (entity[col] || ''), () => ({}), false));
          }
        });
        this.config.columns.push(new DateEntityTableColumn<any>('createdTime', 'potency.created-time', this.datePipe, '150px'));

        this.config.entitiesFetchFunction = pageLink => {
          let startTime: number, endTime: number;
          if (this.config.componentsData.dateRange) {
            startTime = (getTheStartOfDay(this.config.componentsData.dateRange[0] as Date) as number);
            endTime = (getTheEndOfDay(this.config.componentsData.dateRange[1] as Date) as number);
          }
          const { pageSize, page, textSearch, sortOrder } = pageLink;
          const timePageLink = new TimePageLink(pageSize, page, textSearch, sortOrder, startTime, endTime);
          return this.potencyService.getEnergyHistoryDatas(timePageLink, this.deviceId);
        }

        observer.next(this.config);
        observer.complete();
      });
    });
  }

}