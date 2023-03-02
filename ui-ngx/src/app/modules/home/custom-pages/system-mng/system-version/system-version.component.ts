import { Component, OnInit } from '@angular/core';
import { SystemMngService, SystemVersion } from '@app/core/http/custom/system-mng.service';
import { AppState } from '@app/core/public-api';
import { PageComponent } from '@app/shared/public-api';
import { Store } from '@ngrx/store';

@Component({
  selector: 'tb-system-version',
  templateUrl: './system-version.component.html',
  styleUrls: ['./system-version.component.scss']
})
export class SystemVersionComponent extends PageComponent implements OnInit {

  tableData: SystemVersion[] = [];

  constructor(
    protected store: Store<AppState>,
    private systemMngService: SystemMngService
  ) {
    super(store);
  }

  ngOnInit() {
    this.systemMngService.getSystemVersion().subscribe(res => {
      const { version, publishTime } = res || {};
      this.tableData = [{ version, publishTime }];
    });
  }

}
