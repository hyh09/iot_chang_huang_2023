import { Component, OnInit } from '@angular/core';
import { AppState } from '@app/core/public-api';
import { PageComponent } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { FILTERS } from './filters-config';

@Component({
  selector: 'tb-factory-mng',
  templateUrl: './factory-mng.component.html',
  styleUrls: ['./factory-mng.component.scss']
})
export class FactoryMngComponent extends PageComponent implements OnInit {

  public isDetailsOpen: boolean = false;
  public filters = FILTERS;
  public filterParams = {
    name: '',
    workshopName: '',
    productionlineName: '',
    deviceName: ''
  }
  public tableData = [];

  constructor(
    protected store: Store<AppState>,
    public translate: TranslateService
  ) {
    super(store);
  }

  ngOnInit() {
  }

  fetchData() {

  }

  onClear(param: string): void {
    this.filterParams[param] = '';
    this.fetchData();
  }

  addFactory() {

  }

}
