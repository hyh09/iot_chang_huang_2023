import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { EntityType } from '@shared/models/entity-type.models';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { DictDevice } from '@app/shared/models/custom/device-mng.models';

@Component({
  selector: 'tb-dict-device-table-filter',
  templateUrl: './dict-device-table-filter.component.html'
})
export class DictDeviceTableFilterComponent extends EntityTableHeaderComponent<DictDevice> {

  entityType = EntityType;

  constructor(protected store: Store<AppState>) {
    super(store);
  }

  onClear(param: string): void {
    this.entitiesTableConfig.componentsData[param] = '';
    this.refresh();
  }

  refresh() {
    this.entitiesTableConfig.table.resetSortAndFilter(true, true, true);
  }

}
