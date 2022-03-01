import { Router } from '@angular/router';
import { Component } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { DeviceCapacity, PotencyTop10 } from '@app/shared/models/custom/potency.models';
import { Store } from '@ngrx/store';
import { differenceInCalendarDays } from 'date-fns';
import { BehaviorSubject } from 'rxjs';
import { PotencyService } from '@app/core/http/custom/potency.service';

@Component({
  selector: 'tb-production-capacity-overview',
  templateUrl: './production-capacity-overview.component.html',
  styleUrls: ['../energy-consumption/energy-consumption-overview.component.scss']
})
export class ProductionCapacityOverviewComponent extends EntityTableHeaderComponent<DeviceCapacity> {

  today = new Date();
  isHistory:boolean = false;
  factoryId = '';
  chartData: PotencyTop10 = []

  constructor(
    protected store: Store<AppState>,
    public router: Router,
    private potencyService: PotencyService
  ) {
    super(store);
    this.isHistory = this.router.url.indexOf('history') > -1;
  }

  ngAfterViewInit() {
    if (!this.isHistory) {
      (this.entitiesTableConfig.componentsData.factroryChange$ as BehaviorSubject<string>).subscribe(factoryId => {
        if (factoryId) {
          this.factoryId = factoryId;
          this.getTop10()
        }
      });
    }
  }

  disabledDate = (current: Date): boolean => {
    return differenceInCalendarDays(current, this.today) > 0;
  };

  getTop10() {
    if (this.factoryId) {
      this.potencyService.getTop10({
        factoryId: this.factoryId,
        keyNum: '',
        type: '0'
      }).subscribe(res => {
        this.chartData = res || [];
      })
    }
  }

}
