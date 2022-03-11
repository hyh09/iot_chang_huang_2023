import { Component } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { Store } from '@ngrx/store';
import { differenceInCalendarDays } from 'date-fns';

@Component({
  selector: 'tb-device-history-filter',
  templateUrl: './device-history-filter.component.html'
})
export class DeviceHistoryFilterComponent extends EntityTableHeaderComponent<object> {

  today = new Date();

  constructor(
    protected store: Store<AppState>
  ) {
    super(store);
  }

  disabledDate = (current: Date): boolean => {
    return differenceInCalendarDays(current, this.today) > 0;
  };

  onTimeChange() {
    this.entitiesTableConfig.table.resetSortAndFilter(true);
  }

}
