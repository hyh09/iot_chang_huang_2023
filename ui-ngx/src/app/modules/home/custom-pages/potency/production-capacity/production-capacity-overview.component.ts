import { Router } from '@angular/router';
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
  isHistory:boolean = false;

  constructor(
    protected store: Store<AppState>,
    public router: Router
  ) {
    super(store);
    this.isHistory = this.router.url.indexOf('history') > -1;
  }

  disabledDate = (current: Date): boolean => {
    return differenceInCalendarDays(current, this.today) > 0;
  };

}
