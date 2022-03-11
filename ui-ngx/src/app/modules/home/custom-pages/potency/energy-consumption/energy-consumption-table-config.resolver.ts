import { Injectable } from "@angular/core";
import { Resolve, Router } from "@angular/router";
import { PotencyService } from "@app/core/http/custom/potency.service";
import { EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources, TimePageLink } from "@app/shared/public-api";
import { TranslateService } from "@ngx-translate/core";
import { FactoryTreeComponent } from '@app/modules/home/components/factory-tree/factory-tree.component';
import { map } from 'rxjs/operators';
import { BehaviorSubject, Observable, Observer } from "rxjs";
import { EnergyConsumptionOverviewComponent } from "./energy-consumption-overview.component";
import { getTheStartOfDay, getTheEndOfDay } from "@app/core/utils";

@Injectable()
export class EnergyConsumptionTableConfigResolver implements Resolve<EntityTableConfig<any>> {

  private readonly config: EntityTableConfig<any> = new EntityTableConfig<any>();
  private oldFactoryId: string = '';

  constructor(
    private potencyService: PotencyService,
    private translate: TranslateService,
    private router: Router
  ) {
    this.config.entityType = EntityType.POTENCY;
    this.config.leftComponent = FactoryTreeComponent;
    this.config.filterComponent = EnergyConsumptionOverviewComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.POTENCY);
    this.config.entityResources = entityTypeResources.get(EntityType.POTENCY);

    this.config.loadDataOnInit = false;

    this.config.cellActionDescriptors = [{
      name: this.translate.instant('potency.go-to-history'),
      mdiIcon: 'mdi:history-data',
      isEnabled: () => (true),
      onAction: ($event, entity) => this.router.navigate([`/potency/energyConsumption/${entity.deviceId}/history`], {
        queryParams: {
          deviceName: encodeURIComponent(entity['设备名称'])
        }
      })
    }];
  }

  resolve(): Observable<EntityTableConfig<any>> {
    const now = new Date();
    this.config.componentsData = {
      factoryId: '',
      workshopId: '',
      productionLineId: '',
      deviceId: '',
      dateRange: [now, now],
      totalValue: [],
      factroryChange$: new BehaviorSubject<string>('')
    };
    return new Observable((observer: Observer<EntityTableConfig<any>>) => {
      this.potencyService.getEnergyConsumptionTableHeader().subscribe(res => {
        this.config.tableTitle = this.translate.instant('potency.energy-consumption');
        this.config.addEnabled = false;
        this.config.searchEnabled = false;
        this.config.refreshEnabled = false;
        this.config.detailsPanelEnabled = false;
        this.config.entitiesDeleteEnabled = false;
        this.config.selectionEnabled = false;

        this.config.columns = [];
        (res || []).forEach((col, index) => {
          this.config.columns.push(new EntityTableColumn<any>(col, col, index === 0 ? '200px' : '', (entity) => (entity[col] || ''), () => ({}), false));
        });

        this.config.entitiesFetchFunction = pageLink => {
          const { factoryId, workshopId, productionLineId, deviceId } = this.config.componentsData;
          if (factoryId && this.oldFactoryId !== factoryId) {
            this.config.componentsData.factroryChange$.next(factoryId);
            this.oldFactoryId = factoryId;
          }
          let startTime: number, endTime: number;
          if (this.config.componentsData.dateRange) {
            startTime = (getTheStartOfDay(this.config.componentsData.dateRange[0] as Date) as number);
            endTime = (getTheEndOfDay(this.config.componentsData.dateRange[1] as Date) as number);
          }
          const { pageSize, page, textSearch, sortOrder } = pageLink;
          const timePageLink = new TimePageLink(pageSize, page, textSearch, sortOrder, startTime, endTime);
          return this.potencyService.getEnergyConsumptionDatas(timePageLink, { factoryId, workshopId, productionLineId, deviceId }).pipe(map(res => {
            this.config.componentsData.totalValue = res.totalValue || [];
            return res;
          }));
        }

        observer.next(this.config);
        observer.complete();
      });
    });
  }

}