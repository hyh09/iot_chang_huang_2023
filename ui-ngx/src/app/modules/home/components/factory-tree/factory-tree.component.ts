import { Component, OnInit } from '@angular/core';
import { AppState } from '@app/core/public-api';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { NzFormatEmitEvent, NzTreeNodeOptions } from 'ng-zorro-antd/tree';
import { EntityTableHeaderComponent } from '../entity/entity-table-header.component';

@Component({
  selector: 'tb-factory-tree',
  templateUrl: './factory-tree.component.html',
  styleUrls: ['./factory-tree.component.scss']
})
export class FactoryTreeComponent extends EntityTableHeaderComponent<any> implements OnInit {

  public searchValue: string = '';
  public factories: NzTreeNodeOptions[] = [];

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService
  ) {
    super(store);
  }

  ngOnInit() {
  }

  onClickNode(event: NzFormatEmitEvent) {

  }

}
