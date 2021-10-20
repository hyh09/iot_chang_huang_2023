import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig, iconCell } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations } from "@app/shared/public-api";
import { DataDictionaryComponent } from "./data-dictionary.component";
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { DataDictionaryService } from '@app/core/http/custom/data-dictionary.service';
import { DataDictionary } from '@app/shared/models/custom/device-mng.models';
import { DataDictionaryFiltersComponent } from "./data-dictionary-filters.component";

@Injectable()
export class DataDictionaryTableConfigResolver implements Resolve<EntityTableConfig<DataDictionary>> {

  private readonly config: EntityTableConfig<DataDictionary> = new EntityTableConfig<DataDictionary>();

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private dataDictionaryService: DataDictionaryService
  ) {
    this.config.entityType = EntityType.DATA_DICTIONARY;
    this.config.entityComponent = DataDictionaryComponent;
    this.config.filterComponent = DataDictionaryFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.DATA_DICTIONARY);
    this.config.entityResources = entityTypeResources.get(EntityType.DATA_DICTIONARY);

    this.config.componentsData = {
      code: '',
      name: '',
      type: ''
    }

    this.config.deleteEntityTitle = dataDic => this.translate.instant('device-mng.delete-dic-title', {dataDicName: dataDic.name});
    this.config.deleteEntityContent = () => this.translate.instant('device-mng.delete-dic-text');
    this.config.deleteEntitiesTitle = count => this.translate.instant('device-mng.delete-dics-title', {count});
    this.config.deleteEntitiesContent = () => this.translate.instant('device-mng.delete-dics-text');

    this.config.entitiesFetchFunction = pageLink => this.dataDictionaryService.getDataDictionaries(pageLink);
    this.config.loadEntity = id => this.dataDictionaryService.getDataDictionary(id.id);
    this.config.saveEntity = dataDictionary => this.dataDictionaryService.saveDataDictionary(dataDictionary);
    this.config.deleteEntity = id => this.dataDictionaryService.deleteDataDictionary(id.id);

    this.config.columns.push(
      new EntityTableColumn<DataDictionary>('code', 'device-mng.code', '50%'),
      new EntityTableColumn<DataDictionary>('name', 'device-mng.name', '50%'),
      new EntityTableColumn<DataDictionary>('icon', 'device-mng.icon', '80px', ({icon}) => {
        return iconCell(icon)
      }),
      new EntityTableColumn<DataDictionary>('type', 'device-mng.data-type', '150px'),
      new EntityTableColumn<DataDictionary>('unit', 'device-mng.unit', '100px'),
      new DateEntityTableColumn<DataDictionary>('createdTime', 'common.created-time', this.datePipe, '150px')
    );
  }

  resolve(): EntityTableConfig<DataDictionary> {
    this.config.tableTitle = this.translate.instant('device-mng.data-dic');
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    return this.config;
  }

}
