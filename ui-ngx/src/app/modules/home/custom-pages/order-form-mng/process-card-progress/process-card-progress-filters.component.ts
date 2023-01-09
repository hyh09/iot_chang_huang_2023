import { Component } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { processCardProgress } from '@app/shared/models/custom/order-form-mng.models';
import { environment } from '@env/environment';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-process-card-progress-filters',
  templateUrl: './process-card-progress-filters.component.html',
})
export class ProcessCardProgressFiltersComponent extends EntityTableHeaderComponent<processCardProgress> {

  env = environment;

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

