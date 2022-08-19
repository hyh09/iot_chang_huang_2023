import { Injectable } from "@angular/core";
import { Resolve } from "@angular/router";
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeTranslations, entityTypeResources } from "@app/shared/public-api";
import { TranslateService } from "@ngx-translate/core";
import { FactoryVersionFiltersComponent } from './factory-version-filters.component';
import { FactoryVersion } from '@app/shared/models/custom/system-mng.models';
import { DatePipe } from "@angular/common";
import { SystemMngService } from "@app/core/http/custom/system-mng.service";

@Injectable()
export class FactoryVersionTableConfigResolver implements Resolve<EntityTableConfig<FactoryVersion>> {

  private readonly config: EntityTableConfig<FactoryVersion> = new EntityTableConfig<FactoryVersion>();

  constructor(
    private systemMngService: SystemMngService,
    private translate: TranslateService,
    private datePipe: DatePipe
  ) {
    this.config.entityType = EntityType.FACTORY_VERSION;
    this.config.filterComponent = FactoryVersionFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.FACTORY_VERSION);
    this.config.entityResources = entityTypeResources.get(EntityType.FACTORY_VERSION);

    this.config.componentsData = {
      factoryName: '',
      gatewayName: ''
    }

    this.config.columns.push(
      new EntityTableColumn<FactoryVersion>('gatewayName','system-mng.gateway-name', '40%'),
      new EntityTableColumn<FactoryVersion>('factoryVersion', 'system-mng.version', '150px'),
      new EntityTableColumn<FactoryVersion>('factoryName', 'system-mng.factory-belong', '60%'),
      new EntityTableColumn<FactoryVersion>('active', 'system-mng.online-offline', '150px',({ active }) => {
        return active ? this.translate.instant('system-mng.online') : this.translate.instant('system-mng.offline');
      }),
      new DateEntityTableColumn<FactoryVersion>('publishTime', 'system-mng.publish-time', this.datePipe, '150px')
    );
  }

  resolve(): EntityTableConfig<FactoryVersion> {

    this.config.componentsData = {
      factoryName: '',
      gatewayName: ''
    }

    this.config.tableTitle = this.translate.instant('system-mng.factory-version');
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;

    this.config.entitiesFetchFunction = pageLink => this.systemMngService.getFactoryVersions(pageLink, this.config.componentsData);

    return this.config;

  }

}