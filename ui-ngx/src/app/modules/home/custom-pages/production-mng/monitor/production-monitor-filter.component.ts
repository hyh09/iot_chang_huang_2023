import { Router } from '@angular/router';
import { ChangeDetectorRef, Component } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { Store } from '@ngrx/store';
import { differenceInCalendarDays } from 'date-fns';
import { ProdMonitor } from '@app/shared/models/custom/production-mng.models';

@Component({
  selector: 'tb-prod-schedual-filter',
  templateUrl: './production-monitor-filter.component.html'
})
export class ProdMonitorFilterComponent extends EntityTableHeaderComponent<ProdMonitor> {

  today = new Date();

  constructor(
    protected store: Store<AppState>,
    public router: Router,
    protected cd: ChangeDetectorRef
  ) {
    super(store);
  }

  disabledDate = (current: Date): boolean => {
    return differenceInCalendarDays(current, this.today) > 0;
  };

  onClear(param: string): void {
    this.entitiesTableConfig.componentsData[param] = '';
    this.entitiesTableConfig.table.resetSortAndFilter(true);
  }

}
