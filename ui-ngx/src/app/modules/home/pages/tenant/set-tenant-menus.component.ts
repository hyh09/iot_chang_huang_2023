import { Component, OnInit, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AppState } from '@app/core/core.state';
import { TenantMenuService } from '@app/core/http/custom/tenant-menu.service';
import { UtilsService } from '@app/core/services/utils.service';
import { MenuType } from '@app/shared/models/custom/menu-mng.models';
import { DialogComponent, TenantInfo, TenantMenus } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { NzFormatBeforeDropEvent, NzFormatEmitEvent, NzTreeComponent, NzTreeNode, NzTreeNodeOptions } from 'ng-zorro-antd/tree';
import { Observable, of } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { ActionNotificationShow } from '@app/core/notification/notification.actions';

@Component({
  selector: 'tb-set-tenant-menus',
  templateUrl: './set-tenant-menus.component.html',
  styleUrls: ['./set-tenant-menus.component.scss']
})
export class SetTenantMenusComponent extends DialogComponent<SetTenantMenusComponent, TenantMenus> implements OnInit {

  @ViewChild('pcSysTree') private pcSysTree: NzTreeComponent;
  @ViewChild('pcTenantTree') private pcTenantTree: NzTreeComponent;
  @ViewChild('appSysTree') private appSysTree: NzTreeComponent;
  @ViewChild('appTenantTree') private appTenantTree: NzTreeComponent;

  public menuType = MenuType;

  pcSysMenus: NzTreeNodeOptions[];
  sysDefaultCheckedKeys: string[];
  pcTenantMenus: NzTreeNodeOptions[];
  pcRightEnabled: boolean = false;
  pcLeftEnabled: boolean = false;
  appSysMenus: NzTreeNodeOptions[];
  appTenantMenus: NzTreeNodeOptions[];
  appRightEnabled: boolean = false;
  appLeftEnabled: boolean = false;

  pcSysSearch: string = '';
  pcTenantSearch: string = '';
  appSysSearch: string = '';
  appTenantSearch: string = '';

  pcTenantOriginMenuIds: string[] = [];
  appTenantOriginMenuIds: string[] = [];

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<SetTenantMenusComponent, TenantMenus>,
    protected tenantMenuService: TenantMenuService,
    private translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) protected tenantInfo: TenantInfo,
    protected utils: UtilsService
  ) {
    super(store, router, dialogRef);
  }

  ngOnInit() {
    this.pcTenantOriginMenuIds = [];
    this.appTenantOriginMenuIds = [];
    this.getMenuList(MenuType.PC);
    this.getMenuList(MenuType.APP);
  }

  getMenuList(menuType: MenuType) {
    this.sysDefaultCheckedKeys = [];
    this.tenantMenuService.getSysMenuList(menuType, this.tenantInfo.id.id).subscribe(menus => {
      if (menus) {
        menus.forEach(menu => {
          const checked = menu.level === 0 ? menu.checkAllFlag : menu.associatedTenant;
          if (checked) this.sysDefaultCheckedKeys.push(menu.id);
          menu.title = menuType === MenuType.PC ? (menu.langKey ? this.translate.instant(menu.langKey) : menu.name) : menu.name;
          menu.key = menu.id;
          menu.checked = checked;
          menu.disabled = checked;
          menu.selectable = false;
        });
        const sysMenus = this.utils.formatTree(menus);
        if (menuType === MenuType.PC) {
          this.pcSysMenus = sysMenus;
        } else {
          this.appSysMenus = sysMenus;
        }
      }
      this.sysDefaultCheckedKeys = [...this.sysDefaultCheckedKeys];
    });
    this.tenantMenuService.getTenantMenuList(menuType, this.tenantInfo.id.id).subscribe(menus => {
      if (menus) {
        const sysMenuIds: string[] = [];
        menus.forEach(menu => {
          menu.title = menuType === MenuType.PC ? (menu.langKey ? this.translate.instant(menu.langKey) : menu.tenantMenuName) : menu.tenantMenuName;
          menu.key = menu.id;
          menu.selectable = false;
          sysMenuIds.push(menu.sysMenuId);
        });
        if (menuType === MenuType.PC) {
          this.pcTenantOriginMenuIds = sysMenuIds;
          this.pcTenantMenus = this.utils.formatTree(menus);
        } else {
          this.appTenantOriginMenuIds = sysMenuIds;
          this.appTenantMenus = this.utils.formatTree(menus);
        }
      }
    });
  }

  sysCheckChange(event: NzFormatEmitEvent, menuType: MenuType) {
    const checkedNodes: NzTreeNode[] = [];
    event.checkedKeys.forEach(node => {
      checkedNodes.push(...this.utils.expandTreeNode(node));
    });
    this[menuType === MenuType.PC ? 'pcRightEnabled' : 'appRightEnabled'] = !!checkedNodes.find(node => (!node.isDisabled));
  }

  toTenant(menuType: MenuType) {
    let tree: string;
    let tenantMenus: string;
    let rightEnabled: string;
    if (menuType === MenuType.PC) {
      tree = 'pcSysTree';
      tenantMenus = 'pcTenantMenus';
      rightEnabled = 'pcRightEnabled';
    } else {
      tree = 'appSysTree';
      tenantMenus = 'appTenantMenus';
      rightEnabled = 'appRightEnabled';
    }
    const checkedNodes: NzTreeNode[] = (this[tree] as NzTreeComponent).getTreeNodes().filter(node => (
      !node.isDisabled && (node.isHalfChecked || node.isChecked)
    ));
    checkedNodes.forEach(node => {
      const allNodes = this.utils.expandTreeNode(node);
      const checkedNodeIds: string[] = allNodes.filter(_node => (_node.isHalfChecked || _node.isChecked)).map(_node => (_node.key));
      const nodeOptions = this.utils.deepClone(node.origin);
      const checkedNodeOptions = this.utils.expandTreeNodeOptions(nodeOptions).filter(options => (checkedNodeIds.includes(options.key)));
      checkedNodeOptions.forEach(options => {
        if (menuType === MenuType.PC) {
          options.isNew = !this.pcTenantOriginMenuIds.includes(options.id);
        } else {
          options.isNew = !this.appTenantOriginMenuIds.includes(options.id);
        }
        options.sysMenuId = options.id;
        options.sysMenuName = options.name;
        options.sysMenuCode = options.code;
        if (options.tenantMenuId) {
          options.id = options.tenantMenuId;
        }
        if (options.tenantMenuParentId) {
          options.parentId = options.tenantMenuParentId;
        }
        options.checked = false;
        options.disabled = false;
        options.tenantMenuIcon = options.menuIcon;
        options.tenantMenuImages = options.menuImages;
        options.tenantMenuName = options.name;
      });
      allNodes.forEach(_node => {
        if (_node.isChecked) {
          _node.isDisabled = true;
        }
      });
      const existMenuIndex = (this[tenantMenus] as NzTreeNodeOptions[]).findIndex(menu => (menu.sysMenuId === node.key));
      if (existMenuIndex >= 0) {
        this[tenantMenus][existMenuIndex] = this.utils.formatTree(checkedNodeOptions)[0];
      } else {
        this[tenantMenus].push(...this.utils.formatTree(checkedNodeOptions));
      }
    });
    this[rightEnabled] = false;
    this[tenantMenus] = [...this[tenantMenus]];
  }

  toSys(menuType: MenuType) {
    let tree: string;
    let sysTree: string;
    let leftEnabled: string;
    let tenantMenus: string;
    if (menuType === MenuType.PC) {
      tree = 'pcTenantTree';
      sysTree = 'pcSysTree';
      leftEnabled = 'pcLeftEnabled';
      tenantMenus = 'pcTenantMenus';
    } else {
      tree = 'appTenantTree';
      sysTree = 'appSysTree';
      leftEnabled = 'appLeftEnabled';
      tenantMenus = 'appTenantMenus';
    }
    const checkedNodes: NzTreeNode[] = (this[tree] as NzTreeComponent).getTreeNodes().filter(node => (node.isHalfChecked || node.isChecked));
    checkedNodes.forEach(node => {
      const _checkedNodes = this.utils.expandTreeNode(node).filter(_node => (_node.isHalfChecked || _node.isChecked));
      _checkedNodes.forEach(_node => {
        const sysNode = (this[sysTree] as NzTreeComponent).getTreeNodeByKey(_node.origin.sysMenuId);
        if (sysNode) {
          sysNode.isDisabled = false;
          sysNode.origin.tenantMenuId = _node.key;
          if (_node.parentNode) {
            sysNode.origin.tenantMenuParentId = _node.parentNode.key;
            sysNode.parentNode.children.forEach(child => {
              child.origin.tenantMenuId = _node.key;
              child.origin.tenantMenuParentId = _node.parentNode.key;
            });
          }
        }
      })
    });
    
    const checkedNodeList: NzTreeNode[] = (this[tree] as NzTreeComponent).getCheckedNodeList();
    checkedNodeList.forEach(node => {
      (this[sysTree] as NzTreeComponent).getTreeNodeByKey(node.origin.sysMenuId).setSyncChecked(false);
      if (node.level === 0) {
        this[tenantMenus] = (this[tenantMenus] as NzTreeNodeOptions[]).filter(options => (options.key !== node.key));
      } else {
        node.setSyncChecked(false);
        node.remove();
      }
    });
    this[leftEnabled] = false;
  }

  beforeDrop(event: NzFormatBeforeDropEvent): Observable<boolean> {
    if (event.pos !== 0) {
      return of(event.node.parentNode === event.dragNode.parentNode);
    }
    return of(false);
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {
    const pcList = this.pcTenantTree.getTreeNodes().map(node => (node.origin));
    const appList = this.appTenantTree.getTreeNodes().map(node => (node.origin));
    this.tenantMenuService.saveTenantMenus(pcList, appList, this.tenantInfo.id.id).subscribe(() => {
      this.dialogRef.close(null);
      this.store.dispatch(new ActionNotificationShow({
        message: this.translate.instant('tenant.set-tenant-menus-success'),
        type: 'success',
        duration: 3000
      }));
    });
  }

}
