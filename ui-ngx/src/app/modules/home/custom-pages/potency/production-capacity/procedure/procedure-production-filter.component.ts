import { Router } from '@angular/router';
import { ChangeDetectorRef, Component } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { ProcessProduction } from '@app/shared/models/custom/potency.models';
import { Store } from '@ngrx/store';
import { differenceInCalendarDays } from 'date-fns';

@Component({
  selector: 'tb-procedure-production-filter',
  templateUrl: './procedure-production-filter.component.html',
  styleUrls: [
    '../../energy-consumption/energy-consumption-overview.component.scss'
  ]
})
export class ProcedureProductionFilterComponent extends EntityTableHeaderComponent<ProcessProduction> {

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

  onTabClick(name: string) {
    if (name) {
      this.router.navigateByUrl(`/potency/outputAnalysis/${name}`);
    }
  }

  onClear(param: string): void {
    this.entitiesTableConfig.componentsData[param] = '';
    this.entitiesTableConfig.table.resetSortAndFilter(true);
  }

}
