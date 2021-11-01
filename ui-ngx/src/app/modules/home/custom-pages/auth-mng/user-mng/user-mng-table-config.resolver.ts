import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { CellActionDescriptor, DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations } from "@app/shared/public-api";
import { UserMngComponent } from "./user-mng.component";
import { UserMngFiltersComponent } from "./user-mng-filters.component";
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { UserInfo } from "@app/shared/models/custom/auth-mng.models";
import { UserMngService } from "@app/core/http/custom/user-mng.service";
import { map } from "rxjs/operators";
import { RoleMngService } from "@app/core/http/custom/role-mng.service";
import { EntityAction } from "@app/modules/home/models/entity/entity-component.models";
import { MatDialog } from "@angular/material/dialog";
import { UserMngChangePwdComponent } from "./user-mng-change-pwd.component";

@Injectable()
export class UserMngTableConfigResolver implements Resolve<EntityTableConfig<UserInfo>> {

  private readonly config: EntityTableConfig<UserInfo> = new EntityTableConfig<UserInfo>();

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private userMngService: UserMngService,
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
      availableCode: '',
      roleList: []
    }

    this.config.deleteEntityTitle = userInfo => this.translate.instant('menu-mng.delete-user-title', {userName: userInfo.userName});
    this.config.deleteEntityContent = () => this.translate.instant('menu-mng.delete-user-text');
    this.config.deleteEntitiesTitle = count => this.translate.instant('menu-mng.delete-users-title', {count});
    this.config.deleteEntitiesContent = () => this.translate.instant('menu-mng.delete-users-text');

    this.config.columns.push(
      new EntityTableColumn<UserInfo>('userCode', 'auth-mng.user-code', '33.333333%'),
      new EntityTableColumn<UserInfo>('userName', 'auth-mng.user-name', '33.333333%'),
      new EntityTableColumn<UserInfo>('phoneNumber', 'auth-mng.phone-number', '200px'),
      new EntityTableColumn<UserInfo>('email', 'auth-mng.email', '33.333333%'),
      new EntityTableColumn<UserInfo>('activeStatus', 'auth-mng.active-status', '100px', ({activeStatus}) => {
        return activeStatus === '0' ? this.translate.instant('auth-mng.off') : activeStatus === '1' ? this.translate.instant('auth-mng.on') : '';
      }),
      new DateEntityTableColumn<UserInfo>('createdTime', 'common.created-time', this.datePipe, '150px')
    );
    this.config.cellActionDescriptors = this.configureCellActions();
  }

  resolve(): EntityTableConfig<UserInfo> {
    this.roleMngService.getAllRoles().subscribe(res => {
      this.config.componentsData.roleList = res;
    });

    this.setAvailableCode();

    this.config.tableTitle = this.translate.instant('auth-mng.user-mng');
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;

    this.config.entitiesFetchFunction = pageLink => this.userMngService.getUsers(pageLink, this.config.componentsData);
    this.config.loadEntity = id => this.userMngService.getUser(id.id);
    this.config.saveEntity = userInfo => this.userMngService.saveUser(userInfo);
    this.config.entityAdded = () => {
      this.setAvailableCode();
    }
    this.config.deleteEntity = id => {
      return this.userMngService.deleteUser(id.id).pipe(map(result => {
        this.setAvailableCode();
        return result;
      }));
    }

    return this.config;
  }

  configureCellActions(): Array<CellActionDescriptor<UserInfo>> {
    const actions: Array<CellActionDescriptor<UserInfo>> = [];
    actions.push({
      name: this.translate.instant('auth-mng.change-pwd'),
      icon: 'lock',
      isEnabled: (entity) => (!!(entity && entity.id && entity.id.id)),
      onAction: ($event, entity) => this.changePwd($event, entity.id.id)
    });
    return actions;
  }

  setAvailableCode(): void {
    this.userMngService.getAvailableCode().subscribe(code => {
      this.config.componentsData.availableCode = code
    });
  }

  changePwd($event: Event, userId: string): void {
    if ($event) {
      $event.stopPropagation();
    }
    this.dialog.open<UserMngChangePwdComponent, string>(UserMngChangePwdComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: userId
    });
  }

}
