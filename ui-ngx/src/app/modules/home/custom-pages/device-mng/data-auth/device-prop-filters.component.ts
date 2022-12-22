import { Component } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { DeviceAuthProp } from '@app/shared/models/custom/device-mng.models';
import { Store } from '@ngrx/store';

@Component({
  selector: 'tb-device-prop-filters',
  templateUrl: './device-prop-filters.component.html'
})
export class DevicePropFiltersComponent extends EntityTableHeaderComponent<DeviceAuthProp> {

  constructor(
    protected store: Store<AppState>
  ) {
    super(store);
  }

  onClear(param: string): void {
    this.entitiesTableConfig.componentsData[param] = '';
    this.entitiesTableConfig.table.resetSortAndFilter(true);
  }

}
