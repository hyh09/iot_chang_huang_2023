import { map } from 'rxjs/operators';
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
      dictDataType: '',
      dataTypeList: [],
      dataTypeMap: {},
      availableCode: ''
    }

    this.config.deleteEntityTitle = dataDic => this.translate.instant('device-mng.delete-data-dic-title', {dataDicName: dataDic.name});
    this.config.deleteEntityContent = () => this.translate.instant('device-mng.delete-data-dic-text');
    this.config.deleteEntitiesTitle = count => this.translate.instant('device-mng.delete-data-dics-title', {count});
    this.config.deleteEntitiesContent = () => this.translate.instant('device-mng.delete-data-dics-text');

    this.config.columns.push(
      new EntityTableColumn<DataDictionary>('code', 'device-mng.code', '50%'),
      new EntityTableColumn<DataDictionary>('name', 'device-mng.name', '50%'),
      new EntityTableColumn<DataDictionary>('icon', 'device-mng.icon', '80px', ({icon}) => {
        return iconCell(icon);
      }),
      new EntityTableColumn<DataDictionary>('type', 'device-mng.data-type', '150px', ({type}) => {
        if (this.config.componentsData.dataTypeMap[type]) {
          return this.translate.instant(this.config.componentsData.dataTypeMap[type]);
        }
        return '';
      }),
      new EntityTableColumn<DataDictionary>('unit', 'device-mng.unit', '100px'),
      new DateEntityTableColumn<DataDictionary>('createdTime', 'common.created-time', this.datePipe, '150px')
    );
  }

  resolve(): EntityTableConfig<DataDictionary> {
    this.dataDictionaryService.getDataType().subscribe(res => {
      this.config.componentsData.dataTypeList = res;
      res.forEach(({name, code}) => {
        this.config.componentsData.dataTypeMap[code] = `device-mng.${name}`;
      });
    });

    this.setAvailableCode();

    this.config.tableTitle = this.translate.instant('device-mng.data-dic');
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;

    this.config.entitiesFetchFunction = pageLink => this.dataDictionaryService.getDataDictionaries(pageLink, this.config.componentsData);
    this.config.loadEntity = id => this.dataDictionaryService.getDataDictionary(id);
    this.config.saveEntity = dataDictionary => this.dataDictionaryService.saveDataDictionary(dataDictionary);
    this.config.entityAdded = () => {
      this.setAvailableCode();
    }
    this.config.deleteEntity = id => {
      return this.dataDictionaryService.deleteDataDictionary(id).pipe(map(result => {
        this.setAvailableCode();
        return result;
      }));
    }

    return this.config;
  }

  setAvailableCode(): void {
    this.dataDictionaryService.getAvailableCode().subscribe(code => {
      this.config.componentsData.availableCode = code
    });
  }

}
