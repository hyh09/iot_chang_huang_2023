import { Component } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { ProdMng } from '@app/shared/models/custom/device-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'tb-prod-mng-filters',
  templateUrl: './prod-mng-filters.component.html'
})
export class ProdMngFiltersComponent extends EntityTableHeaderComponent<ProdMng> {

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
