import { Component } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { AlarmLevelType, AlarmRecord, AlarmStatusType } from '@app/shared/models/custom/device-monitor.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'tb-alarm-record-filters',
  templateUrl: './alarm-record-filters.component.html'
})
export class AlarmRecordFiltersComponent extends EntityTableHeaderComponent<AlarmRecord> {

  statusType = AlarmStatusType;
  levelType = AlarmLevelType;

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService
  ) {
    super(store);
  }

  onClear(param: string): void {
    this.entitiesTableConfig.componentsData[param] = '';
    this.entitiesTableConfig.table.resetSortAndFilter(true);
  }

}
