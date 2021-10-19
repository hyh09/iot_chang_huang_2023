import { Component } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { DataDictionary } from '@app/shared/models/custom/device-mng.models';
import { EntityType } from '@app/shared/models/entity-type.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'tb-data-dictionary-filters',
  templateUrl: './data-dictionary-filters.component.html'
})
export class DataDictionaryFiltersComponent extends EntityTableHeaderComponent<DataDictionary> {

  entityType = EntityType;

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService
  ) {
    super(store);
  }

  dataTypeChanged(dataType: string) {
    this.entitiesTableConfig.componentsData.dataType = dataType;
    this.entitiesTableConfig.table.resetSortAndFilter(true);
  }

}
