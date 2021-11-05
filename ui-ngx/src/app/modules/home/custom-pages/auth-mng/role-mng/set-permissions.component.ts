import { TranslateService } from '@ngx-translate/core';
import { Component, Inject, OnInit } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { AppState } from "@app/core/core.state";
import { RoleMngService } from '@app/core/http/custom/role-mng.service';
import { ActionNotificationShow } from "@app/core/notification/notification.actions";
import { DialogComponent } from "@app/shared/public-api";
import { Store } from "@ngrx/store";
import { rolePermissions } from '@app/shared/models/custom/auth-mng.models';

@Component({
  selector: 'tb-set-permissions',
  templateUrl: './set-permissions.component.html'
})
export class SetPermissionsComponent extends DialogComponent<SetPermissionsComponent, rolePermissions> implements OnInit {

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<SetPermissionsComponent, rolePermissions>,
    protected roleMngService: RoleMngService,
    protected translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) protected roleId: string
  ) {
    super(store, router, dialogRef);
  }

  ngOnInit() {
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {
    // this.roleMngService.setPermissions({
    //   userId: this.userId
    // }).subscribe(res => {
    //   if (res === 'success') {
    //     this.dialogRef.close(null);
    //     this.store.dispatch(new ActionNotificationShow({
    //       message: this.translate.instant('auth-mng.set-permissions-success'),
    //       type: 'success',
    //       duration: 3000
    //     }));
    //   }
    // });
  }

}
