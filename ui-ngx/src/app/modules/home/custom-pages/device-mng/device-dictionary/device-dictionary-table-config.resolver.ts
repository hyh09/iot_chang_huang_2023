import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig, iconCell } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations } from "@app/shared/public-api";
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { DeviceDictionary } from "@app/shared/models/custom/device-mng.models";
import { DeviceDictionaryService } from "@app/core/http/custom/device-dictionary.service";
import { DeviceDictionaryComponent } from "./device-dictionary.component";
import { DeviceDictionaryFiltersComponent } from "./device-dictionary-filters.component";

@Injectable()
export class DeviceDictionaryTableConfigResolver implements Resolve<EntityTableConfig<DeviceDictionary>> {

  private readonly config: EntityTableConfig<DeviceDictionary> = new EntityTableConfig<DeviceDictionary>();

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private deviceDictionaryService: DeviceDictionaryService
  ) {
    this.config.entityType = EntityType.DATA_DICTIONARY;
    this.config.entityComponent = DeviceDictionaryComponent;
    this.config.filterComponent = DeviceDictionaryFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.DATA_DICTIONARY);
    this.config.entityResources = entityTypeResources.get(EntityType.DATA_DICTIONARY);

    this.config.componentsData = {
      code: '',
      name: '',
      supplier: ''
    }

    this.config.deleteEntityTitle = dataDic => this.translate.instant('device-mng.delete-dic-title', {dataDicName: dataDic.name});
    this.config.deleteEntityContent = () => this.translate.instant('device-mng.delete-dic-text');
    this.config.deleteEntitiesTitle = count => this.translate.instant('device-mng.delete-dics-title', {count});
    this.config.deleteEntitiesContent = () => this.translate.instant('device-mng.delete-dics-text');

    this.config.entitiesFetchFunction = pageLink => this.deviceDictionaryService.getDeviceDictionaries(pageLink);
    this.config.loadEntity = id => this.deviceDictionaryService.getDeviceDictionary(id.id);
    this.config.saveEntity = dataDictionary => this.deviceDictionaryService.saveDeviceDictionary(dataDictionary);
    this.config.deleteEntity = id => this.deviceDictionaryService.deleteDeviceDictionary(id.id);

    this.config.columns.push(
      new EntityTableColumn<DeviceDictionary>('code', 'device-mng.code', '50%'),
      new EntityTableColumn<DeviceDictionary>('name', 'device-mng.name', '50%'),
      new EntityTableColumn<DeviceDictionary>('type', 'device-mng.type', '120px'),
      new EntityTableColumn<DeviceDictionary>('supplier', 'device-mng.supplier', '150px'),
      new EntityTableColumn<DeviceDictionary>('model', 'device-mng.model', '120px'),
      new EntityTableColumn<DeviceDictionary>('version', 'device-mng.version', '100px'),
      new DateEntityTableColumn<DeviceDictionary>('createdTime', 'common.created-time', this.datePipe, '150px')
    );
  }

  resolve(): EntityTableConfig<DeviceDictionary> {
    this.config.tableTitle = this.translate.instant('device-mng.device-dic');
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    return this.config;
  }

}
