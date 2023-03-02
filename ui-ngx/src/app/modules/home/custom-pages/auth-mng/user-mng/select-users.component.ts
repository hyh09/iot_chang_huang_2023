import { TranslateService } from '@ngx-translate/core';
import { Component, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { AppState } from "@app/core/core.state";
import { RoleMngService } from '@app/core/http/custom/role-mng.service';
import { BaseData, DialogComponent, EntityType, entityTypeResources, entityTypeTranslations, HasId } from "@app/shared/public-api";
import { Store } from "@ngrx/store";
import { UserInfo } from '@app/shared/models/custom/auth-mng.models';
import { EntityTableColumn, EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { UserMngFiltersComponent } from './user-mng-filters.component';

@Component({
  selector: 'tb-select-users',
  templateUrl: './select-users.component.html'
})
export class SelectUsersComponent extends DialogComponent<SelectUsersComponent, UserInfo[]> {

  public readonly config: EntityTableConfig<UserInfo> = new EntityTableConfig<UserInfo>();

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<SelectUsersComponent, UserInfo[]>,
    protected roleMngService: RoleMngService,
    protected translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) protected roleId: string
  ) {
    super(store, router, dialogRef);

    this.config.entityType = EntityType.USER_MNG;
    this.config.filterComponent = UserMngFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.USER_MNG);
    this.config.entityResources = entityTypeResources.get(EntityType.USER_MNG);

    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.padding = '0';
    this.config.titleVisible = false;
    this.config.groupActionEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionAlwaysEnabled = true;

    this.config.componentsData = {
      userCode: '',
      userName: ''
    }

    this.config.columns.push(
      new EntityTableColumn<UserInfo>('userCode', 'auth-mng.user-code', '100px'),
      new EntityTableColumn<UserInfo>('userName', 'auth-mng.user-name', '100px'),
      new EntityTableColumn<UserInfo>('phoneNumber', 'auth-mng.phone-number', '50%'),
      new EntityTableColumn<UserInfo>('email', 'auth-mng.email', '50%')
    );

    this.config.entitiesFetchFunction = pageLink => this.roleMngService.getNotBindingUsers(pageLink, {
      ...this.config.componentsData,
      roleId: this.roleId
    });
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save(selectedUsers: BaseData<HasId>[]) {
    if (selectedUsers && selectedUsers.length > 0) {
      this.roleMngService.bindUsers({
        userIds: selectedUsers.map(user => user.id),
        tenantSysRoleId: this.roleId
      }).subscribe(() => {
        this.dialogRef.close(selectedUsers as UserInfo[]);
      });
    }
  }

}
