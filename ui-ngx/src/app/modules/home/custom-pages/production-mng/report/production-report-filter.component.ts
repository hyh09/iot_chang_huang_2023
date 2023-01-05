import { Router } from '@angular/router';
import { ChangeDetectorRef, Component } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { Store } from '@ngrx/store';
import { differenceInCalendarDays } from 'date-fns';
import { ProdReport } from '@app/shared/models/custom/production-mng.models';

@Component({
  selector: 'tb-prod-schedual-filter',
  templateUrl: './production-report-filter.component.html'
})
export class ProductionReportFilterComponent extends EntityTableHeaderComponent<ProdReport> {

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

  onTimeChange() {
    this.entitiesTableConfig.table.resetSortAndFilter(true);
  }

  onClear(param: string): void {
    this.entitiesTableConfig.componentsData[param] = '';
    this.entitiesTableConfig.table.resetSortAndFilter(true);
  }

}
