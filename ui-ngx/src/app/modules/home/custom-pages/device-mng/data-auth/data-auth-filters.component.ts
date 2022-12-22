import { Component } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { FactoryMngService } from '@app/core/public-api';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { DeviceDataAuth } from '@app/shared/models/custom/device-mng.models';
import { Factory } from '@app/shared/models/custom/factory-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'tb-data-auth-filters',
  templateUrl: './data-auth-filters.component.html'
})
export class DataAuthFiltersComponent extends EntityTableHeaderComponent<DeviceDataAuth> {

  factoryList: Factory[] = [];

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    private factoryMngService: FactoryMngService
  ) {
    super(store);
    this.factoryMngService.getAllFactories().subscribe(res => this.factoryList = res || []);
  }

  onClear(param: string): void {
    this.entitiesTableConfig.componentsData[param] = '';
    this.entitiesTableConfig.table.resetSortAndFilter(true);
  }

}
