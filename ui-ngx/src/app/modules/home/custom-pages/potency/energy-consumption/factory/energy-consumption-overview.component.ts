import { Component, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { AppState } from '@app/core/core.state';
import { PotencyService } from '@app/core/http/custom/potency.service';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { DeviceEnergyConsumption, PotencyTop10 } from '@app/shared/models/custom/potency.models';
import { Store } from '@ngrx/store';
import { differenceInCalendarDays } from 'date-fns';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'tb-energy-consumption-overview',
  templateUrl: './energy-consumption-overview.component.html',
  styleUrls: ['./energy-consumption-overview.component.scss']
})
export class EnergyConsumptionOverviewComponent extends EntityTableHeaderComponent<DeviceEnergyConsumption> implements AfterViewInit {

  today = new Date();
  keyNum: '1' | '2' | '3' = '1';
  factoryId = '';
  top10: PotencyTop10 = []

  constructor(
    protected store: Store<AppState>,
    private potencyService: PotencyService,
    protected cd: ChangeDetectorRef,
    private router: Router
  ) {
    super(store);
  }

  ngAfterViewInit() {
    (this.entitiesTableConfig.componentsData.factroryChange$ as BehaviorSubject<string>).subscribe(factoryId => {
      if (factoryId) {
        this.factoryId = factoryId;
        this.getTop10()
      }
    });
  }

  disabledDate = (current: Date): boolean => {
    return differenceInCalendarDays(current, this.today) > 0;
  };

  getTop10(keyNum: '1' | '2' | '3' = this.keyNum) {
    if (this.factoryId && keyNum) {
      this.potencyService.getTop10({
        factoryId: this.factoryId,
        keyNum,
        type: '1'
      }).subscribe(res => {
        this.top10 = res || [];
        this.cd.markForCheck();
        this.cd.detectChanges();
      })
    }
  }

  onTimeChange() {
    this.entitiesTableConfig.table.resetSortAndFilter(true);
    this.getTop10();
  }

  onTabClick(name: string) {
    if (name) {
      this.router.navigateByUrl(`/potency/energyConsumption/${name}`);
    }
  }

}
