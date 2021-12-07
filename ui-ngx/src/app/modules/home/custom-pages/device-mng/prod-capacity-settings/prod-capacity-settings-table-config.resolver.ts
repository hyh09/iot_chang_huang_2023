import { Injectable } from "@angular/core";
import { Resolve } from "@angular/router";
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources } from "@app/shared/public-api";
import { TranslateService } from "@ngx-translate/core";
import { FactoryTreeComponent } from '@app/modules/home/components/factory-tree/factory-tree.component';
import { ProdCapacitySettings } from "@app/shared/models/custom/device-mng.models";
import { ProdCapacitySettingsFiltersComponent } from "./prod-capacity-settings-filters.component";
import { DatePipe } from "@angular/common";
import { ProdCapacitySettingsService } from "@app/core/http/custom/prod-capacity-settings";

@Injectable()
export class ProdCapacitySettingsTableConfigResolver implements Resolve<EntityTableConfig<ProdCapacitySettings>> {

  private readonly config: EntityTableConfig<ProdCapacitySettings> = new EntityTableConfig<ProdCapacitySettings>();

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private prodCapacitySettingsService: ProdCapacitySettingsService
  ) {
    this.config.entityType = EntityType.PROD_CAPACITY_SETTINGS;
    this.config.leftComponent = FactoryTreeComponent;
    this.config.filterComponent = ProdCapacitySettingsFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.PROD_CAPACITY_SETTINGS);
    this.config.entityResources = entityTypeResources.get(EntityType.PROD_CAPACITY_SETTINGS);

    this.config.componentsData = {
      factoryId: '',
      workshopId: '',
      productionLineId: '',
      deviceId: '',
      deviceName: ''
    }

    this.config.columns.push(
      new EntityTableColumn<ProdCapacitySettings>('flg', this.translate.instant('device-mng.in-calculation'), '80px', () => (''),
      () => ({ textAlign: 'center' }), true, () => ({ textAlign: 'center' }), () => undefined, false, null, false, true, (entity, flag) => {
        this.prodCapacitySettingsService.setFlag(entity.deviceId, flag).subscribe();
      }),
      new EntityTableColumn<ProdCapacitySettings>('deviceName', this.translate.instant('device-mng.device-name'), '34%'),
      new EntityTableColumn<ProdCapacitySettings>('deviceFileName', this.translate.instant('device-profile.device-profile'), '33%'),
      new EntityTableColumn<ProdCapacitySettings>('dictName', this.translate.instant('device-mng.device-dic'), '33%'),
      new EntityTableColumn<ProdCapacitySettings>('deviceNo', this.translate.instant('device-mng.model'), '150px'),
      new DateEntityTableColumn<ProdCapacitySettings>('createdTime', 'common.created-time', this.datePipe, '150px')
    );
  }

  resolve(): EntityTableConfig<ProdCapacitySettings> {

    this.config.componentsData = {
      factoryId: '',
      workshopId: '',
      productionLineId: '',
      deviceId: '',
      deviceName: ''
    }

    this.config.tableTitle = this.translate.instant('device-mng.prod-capactity-settings');
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;
    this.config.loadDataOnInit = false;

    this.config.entitiesFetchFunction = pageLink => this.prodCapacitySettingsService.getProdCapacitySettings(pageLink, this.config.componentsData);

    return this.config;

  }

}