import { Injectable } from "@angular/core";
import { Resolve, Router } from '@angular/router';
import { CellActionDescriptor, DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations, HasUUID } from "@app/shared/public-api";
import { RoleMngComponent } from "./role-mng.component";
import { RoleMngFiltersComponent } from "./role-mng-filters.component";
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { Role } from "@app/shared/models/custom/auth-mng.models";
import { map } from "rxjs/operators";
import { RoleMngService } from "@app/core/http/custom/role-mng.service";
import { MatDialog } from "@angular/material/dialog";
import { EntityAction } from "@app/modules/home/models/entity/entity-component.models";
import { SetPermissionsComponent, SetPermissionsDialogData } from "./set-permissions.component";
import { UtilsService } from "@app/core/public-api";
import { select, Store } from "@ngrx/store";
import { AppState } from '@core/core.state';
import { selectUserDetails } from "@app/core/auth/auth.selectors";

@Injectable()
export class RoleMngTableConfigResolver implements Resolve<EntityTableConfig<Role>> {

  private readonly config: EntityTableConfig<Role> = new EntityTableConfig<Role>();

  userLevel$ = this.store.pipe(
    select(selectUserDetails),
    map((user) => (user.userLevel))
  );
  userLevel: number;

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private roleMngService: RoleMngService,
    public dialog: MatDialog,
    private router: Router,
    private utils: UtilsService,
    private store: Store<AppState>
  ) {
    this.config.entityType = EntityType.ROLE_MNG;
    this.config.entityComponent = RoleMngComponent;
    this.config.filterComponent = RoleMngFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.ROLE_MNG);
    this.config.entityResources = entityTypeResources.get(EntityType.ROLE_MNG);

    this.config.componentsData = {
      roleCode: '',
      roleName: '',
      availableCode: ''
    }

    this.config.deleteEntityTitle = role => this.translate.instant('auth-mng.delete-role-title', {roleName: role.roleName});
    this.config.deleteEntityContent = () => this.translate.instant('auth-mng.delete-role-text');
    this.config.deleteEntitiesTitle = count => this.translate.instant('auth-mng.delete-roles-title', {count});
    this.config.deleteEntitiesContent = () => this.translate.instant('auth-mng.delete-roles-text');
  }

  resolve(): EntityTableConfig<Role> {
    this.config.columns = [
      new EntityTableColumn<Role>('roleCode', 'auth-mng.role-code', '33.333333%'),
      new EntityTableColumn<Role>('roleName', 'auth-mng.role-name', '33.333333%'),
      new DateEntityTableColumn<Role>('createdTime', 'common.created-time', this.datePipe, '150px')
    ];
    this.userLevel$.subscribe(userLevel => {
      this.userLevel = userLevel
      if (userLevel === 3) {
        this.config.columns.splice(5, 0, new EntityTableColumn<Role>('operationType', this.translate.instant('auth-mng.whether-sys-role'), '80px', () => (''),
        () => ({ textAlign: 'center' }), false, () => ({ textAlign: 'center' }), () => undefined, false, null, () => (false), true, (entity, flag) => {
          this.roleMngService.switchSysRole({id: entity.id + '', operationType: flag ? 1 : 0}).subscribe();
        }));
      }
    });

    this.config.componentsData = {
      roleCode: '',
      roleName: '',
      availableCode: ''
    }
    
    this.roleMngService.getAllRoles().subscribe(res => {
      this.config.componentsData.roleList = res;
    });

    this.setAvailableCode();

    this.config.tableTitle = this.translate.instant('auth-mng.role-mng');
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.deleteEnabled = entity => this.userLevel === 3 || this.userLevel === 1 || ((this.userLevel === 4 || this.userLevel === 0) && entity && entity.operationType === 0);
    this.config.afterResolved = () => {
      this.config.addEnabled = this.utils.hasPermission('auth-mng.add-role');
      this.config.entitiesDeleteEnabled = this.utils.hasPermission('action.delete');
      this.config.detailsReadonly = entity => (!this.utils.hasPermission('action.edit') || ((this.userLevel === 4 || this.userLevel === 0) && entity && entity.operationType === 1));
      this.config.cellActionDescriptors = this.configureCellActions();
    }

    this.config.entitiesFetchFunction = pageLink => this.roleMngService.getRoles(pageLink, this.config.componentsData);
    this.config.loadEntity = id => this.roleMngService.getRole(id);
    this.config.saveEntity = role => this.roleMngService.saveRole(role);
    this.config.entityAdded = () => {
      this.setAvailableCode();
    }
    this.config.deleteEntity = id => {
      return this.roleMngService.deleteRole(id).pipe(map(result => {
        this.setAvailableCode();
        return result;
      }));
    }
    this.config.onEntityAction = action => this.onRoleAction(action);

    return this.config;
  }

  configureCellActions(): Array<CellActionDescriptor<Role>> {
    const actions: Array<CellActionDescriptor<Role>> = [];
    if (this.utils.hasPermission('auth-mng.bind-users')) {
      actions.push({
        name: this.translate.instant('auth-mng.bind-users'),
        icon: 'account_circle',
        isEnabled: (entity) => (!!(entity && entity.id)),
        onAction: ($event, entity) => this.bindUsers($event, entity.id)
      });
    }
    if (this.utils.hasPermission('auth-mng.set-permissions')) {
      actions.push({
        name: this.translate.instant('auth-mng.set-permissions'),
        mdiIcon: 'mdi:config',
        isEnabled: (entity) => (!!(entity && entity.id) && (this.userLevel === 3 || this.userLevel === 1 || ((this.userLevel === 4 || this.userLevel === 0) && entity.operationType === 0))),
        onAction: ($event, entity) => this.setPermissions($event, entity.id)
      });
    }
    return actions;
  }

  onRoleAction(action: EntityAction<Role>): boolean {
    switch (action.action) {
      case 'bindUsers':
        this.bindUsers(action.event, action.entity.id);
        return true;
      case 'setPermissions':
        this.setPermissions(action.event, action.entity.id);
        return true;
    }
    return false;
  }

  setAvailableCode(): void {
    this.roleMngService.getAvailableCode().subscribe(code => {
      this.config.componentsData.availableCode = code;
    });
  }

  bindUsers($event: Event, roleId: HasUUID): void {
    if ($event) {
      $event.stopPropagation();
    }
    this.router.navigateByUrl(`authManagement/roleManagemnet/${roleId}/users`);
  }

  setPermissions($event: Event, roleId: HasUUID): void {
    if ($event) {
      $event.stopPropagation();
    }
    this.dialog.open<SetPermissionsComponent, SetPermissionsDialogData>(SetPermissionsComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: { roleId: roleId + '' }
    });
  }

}
