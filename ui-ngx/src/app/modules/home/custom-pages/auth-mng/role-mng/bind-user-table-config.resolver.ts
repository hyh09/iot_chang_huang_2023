import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations } from "@app/shared/public-api";
import { UserMngComponent } from "../user-mng/user-mng.component";
import { UserMngFiltersComponent } from "../user-mng/user-mng-filters.component";
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { UserInfo } from "@app/shared/models/custom/auth-mng.models";
import { RoleMngService } from "@app/core/http/custom/role-mng.service";
import { MatDialog } from "@angular/material/dialog";
import { Observable } from "rxjs";
import { SelectUsersComponent } from "../user-mng/select-users.component";

@Injectable()
export class BindUserTableConfigResolver implements Resolve<EntityTableConfig<UserInfo>>  {

  private readonly config: EntityTableConfig<UserInfo> = new EntityTableConfig<UserInfo>();
  private roleId: string;

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private roleMngService: RoleMngService,
    public dialog: MatDialog
  ) {
    this.config.entityType = EntityType.USER_MNG;
    this.config.entityComponent = UserMngComponent;
    this.config.filterComponent = UserMngFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.USER_MNG);
    this.config.entityResources = entityTypeResources.get(EntityType.USER_MNG);

    this.config.componentsData = {
      userCode: '',
      userName: '',
      roleId: ''
    }

    this.config.columns.push(
      new EntityTableColumn<UserInfo>('userCode', 'auth-mng.user-code', '33.333333%'),
      new EntityTableColumn<UserInfo>('userName', 'auth-mng.user-name', '33.333333%'),
      new EntityTableColumn<UserInfo>('phoneNumber', 'auth-mng.phone-number', '200px'),
      new EntityTableColumn<UserInfo>('email', 'auth-mng.email', '33.333333%'),
      new DateEntityTableColumn<UserInfo>('createdTime', 'common.created-time', this.datePipe, '150px')
    );
  }

  resolve(route: ActivatedRouteSnapshot): EntityTableConfig<UserInfo> {
    this.roleId = route.params.roleId;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.componentsData.roleId = this.roleId;
    this.config.tableTitle = this.translate.instant('auth-mng.bind-users');
    this.config.deleteEntityTitle = userInfo => this.translate.instant('auth-mng.unbind-user-title', {userName: userInfo.userName});
    this.config.deleteEntitiesTitle = count => this.translate.instant('auth-mng.unbind-users-title', {count});

    this.config.entitiesFetchFunction = pageLink => this.roleMngService.getBindingUsers(pageLink, this.config.componentsData);
    this.config.addEntity = () => this.bindUsers();
    this.config.deleteEntity = id => {
      return this.roleMngService.unbindUsers({
        userIds: [id],
        tenantSysRoleId: this.roleId
      });
    }

    return this.config;
  }

  bindUsers(): Observable<UserInfo[]> {
    return this.dialog.open<SelectUsersComponent, string, UserInfo[]>(SelectUsersComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: this.roleId
    }).afterClosed();
  }

}
