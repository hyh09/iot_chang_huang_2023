import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';
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
import { MatDialog } from "@angular/material/dialog";
import { UserMngChangePwdComponent } from "./user-mng-change-pwd.component";
import { UtilsService } from "@app/core/public-api";

@Injectable()
export class UserMngTableConfigResolver implements Resolve<EntityTableConfig<UserInfo>>  {

  private readonly config: EntityTableConfig<UserInfo> = new EntityTableConfig<UserInfo>();

  factoryId: string;

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private userMngService: UserMngService,
    private roleMngService: RoleMngService,
    public dialog: MatDialog,
    private utils: UtilsService
  ) {
    this.config.entityType = EntityType.USER_MNG;
    this.config.entityComponent = UserMngComponent;
    this.config.filterComponent = UserMngFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.USER_MNG);
    this.config.entityResources = entityTypeResources.get(EntityType.USER_MNG);

    this.config.componentsData = {
      factoryId: '',
      userCode: '',
      userName: '',
      availableCode: '',
      roleList: []
    }

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
  }

  resolve(route: ActivatedRouteSnapshot): EntityTableConfig<UserInfo> {
    this.config.componentsData = {
      factoryId: '',
      userCode: '',
      userName: '',
      availableCode: '',
      roleList: []
    }

    if (route.params.factoryId) {
      this.factoryId = route.params.factoryId;
      this.config.componentsData.factoryId = this.factoryId;
      this.config.tableTitle = `${route.queryParams.factoryName}: ${this.translate.instant('device-mng.factory-manager')}`;
    } else {
      this.factoryId = '';
      this.config.tableTitle = this.translate.instant('auth-mng.user-mng');
      this.roleMngService.getAllRoles().subscribe(res => {
        this.config.componentsData.roleList = res;
      });
    }
    
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.deleteEnabled = entity => (entity && entity.operationType !== 1);
    this.config.afterResolved = () => {
      this.config.addEnabled = this.utils.hasPermission('user.add');
      this.config.entitiesDeleteEnabled = this.utils.hasPermission('action.delete');
      this.config.detailsReadonly = entity => (!this.utils.hasPermission('action.edit') || (entity && entity.operationType === 1));
      this.config.cellActionDescriptors = this.configureCellActions();
    }

    this.setAvailableCode();

    this.config.deleteEntityTitle = userInfo => this.translate.instant('auth-mng.delete-user-title', {userName: userInfo.userName});
    this.config.deleteEntityContent = () => this.translate.instant('auth-mng.delete-user-text');
    this.config.deleteEntitiesTitle = count => this.translate.instant('auth-mng.delete-users-title', {count});
    this.config.deleteEntitiesContent = () => this.translate.instant('auth-mng.delete-users-text');

    if (this.factoryId) {
      this.config.entitiesFetchFunction = pageLink => this.userMngService.getFactoryManagers(pageLink, this.config.componentsData);
    } else {
      this.config.entitiesFetchFunction = pageLink => this.userMngService.getUsers(pageLink, this.config.componentsData);
    }
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
    if (this.utils.hasPermission('auth-mng.change-pwd')) {
      actions.push({
        name: this.translate.instant('auth-mng.change-pwd'),
        mdiIcon: 'mdi:pwd-key',
        isEnabled: (entity) => (!!(entity && entity.id && entity.id.id)),
        onAction: ($event, entity) => this.changePwd($event, entity.id.id)
      });
    }
    return actions;
  }

  setAvailableCode(): void {
    this.userMngService.getAvailableCode().subscribe(code => {
      this.config.componentsData.availableCode = code;
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
