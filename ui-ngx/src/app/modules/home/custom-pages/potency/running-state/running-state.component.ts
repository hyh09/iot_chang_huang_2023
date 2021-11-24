import { Component, OnInit } from '@angular/core';
import { FactoryTreeNodeIds } from '@app/shared/models/custom/factory-mng.models';
import { Timewindow, historyInterval, DAY } from '@app/shared/public-api';

@Component({
  selector: 'tb-running-state',
  templateUrl: './running-state.component.html',
  styleUrls: ['./running-state.component.scss']
})
export class RunningStateComponent implements OnInit {

  factoryInfo: FactoryTreeNodeIds = {
    factoryId: '',
    workshopId: '',
    productionLineId: '',
    deviceId: ''
  };
  timewindow: Timewindow = historyInterval(DAY);
  selectedPropIds: string[] = [];
  properties = [];

  constructor() { }

  ngOnInit() {
  }

  fetchData(factoryInfo?: FactoryTreeNodeIds) {
    if (factoryInfo) {
      this.factoryInfo = factoryInfo;
    }
  }

}
