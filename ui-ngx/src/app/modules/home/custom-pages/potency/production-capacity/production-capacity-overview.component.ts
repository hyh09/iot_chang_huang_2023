import { Component } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { DeviceCapacity } from '@app/shared/models/custom/potency.models';
import { Store } from '@ngrx/store';
import { differenceInCalendarDays } from 'date-fns';

@Component({
  selector: 'tb-production-capacity-overview',
  templateUrl: './production-capacity-overview.component.html'
})
export class ProductionCapacityOverviewComponent extends EntityTableHeaderComponent<DeviceCapacity> {

  today = new Date();

  constructor(
    protected store: Store<AppState>
  ) {
    super(store);
  }

  disabledDate = (current: Date): boolean => {
    return differenceInCalendarDays(current, this.today) > 0;
  };

}
