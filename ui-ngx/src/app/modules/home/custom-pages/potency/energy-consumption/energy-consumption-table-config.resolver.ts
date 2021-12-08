import { Injectable } from "@angular/core";
import { Resolve, Router } from "@angular/router";
import { PotencyService } from "@app/core/http/custom/potency.service";
import { EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources } from "@app/shared/public-api";
import { TranslateService } from "@ngx-translate/core";
import { FactoryTreeComponent } from '@app/modules/home/components/factory-tree/factory-tree.component';
import { map } from 'rxjs/operators';
import { Observable, Observer } from "rxjs";
import { EnergyConsumptionOverviewComponent } from "./energy-consumption-overview.component";

@Injectable()
export class EnergyConsumptionTableConfigResolver implements Resolve<EntityTableConfig<any>> {

  private readonly config: EntityTableConfig<any> = new EntityTableConfig<any>();

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

    this.config.componentsData = {
      factoryId: '',
      workshopId: '',
      productionLineId: '',
      deviceId: '',
      totalValue: []
    };

    this.config.cellActionDescriptors = [{
      name: this.translate.instant('potency.go-to-history'),
      mdiIcon: 'mdi:history-data',
      isEnabled: () => (true),
      onAction: ($event, entity) => this.router.navigateByUrl(`/potency/energyConsumption/${entity.deviceId}/history`)
    }];
  }

  resolve(): Observable<EntityTableConfig<any>> {
    this.config.componentsData = {
      factoryId: '',
      workshopId: '',
      productionLineId: '',
      deviceId: '',
      totalValue: []
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
        this.config.useTimePageLink = true;
        this.config.timeWindowInFilter = true;

        this.config.columns = [];
        (res || []).forEach((col, index) => {
          this.config.columns.push(new EntityTableColumn<any>(col, col, index === 0 ? '200px' : '', (entity) => (entity[col] || ''), () => ({}), false));
        });

        this.config.entitiesFetchFunction = pageLink => {
          const { factoryId, workshopId, productionLineId, deviceId } = this.config.componentsData;
          return this.potencyService.getEnergyConsumptionDatas(pageLink, { factoryId, workshopId, productionLineId, deviceId }).pipe(map(res => {
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