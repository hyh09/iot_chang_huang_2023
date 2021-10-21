import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AppState } from '@app/core/core.state';
import { DialogComponent, TentMenus } from '@app/shared/public-api';
import { Store } from '@ngrx/store';

export interface SetMenusDialogData {
  
}

@Component({
  selector: 'tb-set-tenant-menus',
  templateUrl: './set-tenant-menus.component.html',
  styleUrls: ['./set-tenant-menus.component.scss']
})
export class SetTenantMenusComponent extends DialogComponent<SetTenantMenusComponent, TentMenus> implements OnInit {

  tenantMenusForm: FormGroup;

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<SetTenantMenusComponent, TentMenus>,
  ) {
    super(store, router, dialogRef);
  }

  ngOnInit() {
    this.tenantMenusForm = new FormGroup({
      
    })
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {

  }

}
