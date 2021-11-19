import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations } from "@app/shared/public-api";
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { DeviceDictionary } from "@app/shared/models/custom/device-mng.models";
import { DeviceDictionaryService } from "@app/core/http/custom/device-dictionary.service";
import { DeviceDictionaryComponent } from "./device-dictionary.component";
import { DeviceDictionaryFiltersComponent } from "./device-dictionary-filters.component";
import { map } from "rxjs/operators";
import { UtilsService } from "@app/core/public-api";

@Injectable()
export class DeviceDictionaryTableConfigResolver implements Resolve<EntityTableConfig<DeviceDictionary>> {

  private readonly config: EntityTableConfig<DeviceDictionary> = new EntityTableConfig<DeviceDictionary>();

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private deviceDictionaryService: DeviceDictionaryService,
    private utils: UtilsService
  ) {
    this.config.entityType = EntityType.DEVICE_DICTIONARY;
    this.config.entityComponent = DeviceDictionaryComponent;
    this.config.filterComponent = DeviceDictionaryFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.DEVICE_DICTIONARY);
    this.config.entityResources = entityTypeResources.get(EntityType.DEVICE_DICTIONARY);

    this.config.componentsData = {
      code: '',
      name: '',
      supplier: '',
      availableCode: ''
    }

    this.config.addDialogStyle = {width: '900px'};

    this.config.deleteEntityTitle = deviceDic => this.translate.instant('device-mng.delete-device-dic-title', {deviceDicName: deviceDic.name});
    this.config.deleteEntityContent = () => this.translate.instant('device-mng.delete-device-dic-text');
    this.config.deleteEntitiesTitle = count => this.translate.instant('device-mng.delete-device-dics-title', {count});
    this.config.deleteEntitiesContent = () => this.translate.instant('device-mng.delete-device-dics-text');

    this.config.columns.push(
      new EntityTableColumn<DeviceDictionary>('code', 'device-mng.code', '50%'),
      new EntityTableColumn<DeviceDictionary>('name', 'device-mng.name', '50%'),
      new EntityTableColumn<DeviceDictionary>('type', 'device-mng.device-type', '120px'),
      new EntityTableColumn<DeviceDictionary>('supplier', 'device-mng.supplier', '150px'),
      new EntityTableColumn<DeviceDictionary>('model', 'device-mng.model', '120px'),
      new EntityTableColumn<DeviceDictionary>('version', 'device-mng.version', '100px'),
      new DateEntityTableColumn<DeviceDictionary>('createdTime', 'common.created-time', this.datePipe, '150px')
    );
  }

  resolve(): EntityTableConfig<DeviceDictionary> {
    this.config.componentsData = {
      code: '',
      name: '',
      supplier: '',
      availableCode: ''
    }
    
    this.setAvailableCode();

    this.config.tableTitle = this.translate.instant('device-mng.device-dic');
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.afterResolved = () => {
      this.config.addEnabled = this.utils.hasPermission('device-mng.add-device-dic');
      this.config.entitiesDeleteEnabled = this.utils.hasPermission('action.delete');
      this.config.detailsReadonly = () => (!this.utils.hasPermission('action.edit'));
    }

    this.config.entitiesFetchFunction = pageLink => this.deviceDictionaryService.getDeviceDictionaries(pageLink, this.config.componentsData);
    this.config.loadEntity = id => this.deviceDictionaryService.getDeviceDictionary(id);
    this.config.saveEntity = deviceDictionary => this.deviceDictionaryService.saveDeviceDictionary(deviceDictionary);
    this.config.entityAdded = () => {
      this.setAvailableCode();
    }
    this.config.deleteEntity = id => {
      return this.deviceDictionaryService.deleteDeviceDictionary(id).pipe(map(result => {
        this.setAvailableCode();
        return result;
      }));
    }

    return this.config;
  }

  setAvailableCode(): void {
    this.deviceDictionaryService.getAvailableCode().subscribe(code => {
      this.config.componentsData.availableCode = code
    });
  }

}
