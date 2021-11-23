import { TranslateService } from '@ngx-translate/core';
import { Component, Inject, OnInit, ViewChild } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { AppState } from "@app/core/core.state";
import { RoleMngService } from '@app/core/http/custom/role-mng.service';
import { DialogComponent, HasUUID } from "@app/shared/public-api";
import { Store } from "@ngrx/store";
import { MenuTreeNodeOptions, MenuType } from '@app/shared/models/custom/menu-mng.models';
import { UtilsService } from '@app/core/public-api';
import { NzTreeComponent, NzTreeNode } from 'ng-zorro-antd/tree';
import { ActionNotificationShow } from '@app/core/notification/notification.actions';

@Component({
  selector: 'tb-set-permissions',
  templateUrl: './set-permissions.component.html',
  styleUrls: ['./set-permissions.component.scss']
})
export class SetPermissionsComponent extends DialogComponent<SetPermissionsComponent, HasUUID> implements OnInit {

  public pcMenus: MenuTreeNodeOptions[] = [];
  public appMenus: MenuTreeNodeOptions[] = [];
  public pcCheckedKeys: string[];
  public appCheckedKeys: string[];
  public pcExpandedKeys: string[];
  public appExpandedKeys: string[];

  @ViewChild('pcTree') private pcTree: NzTreeComponent;
  @ViewChild('appTree') private appTree: NzTreeComponent;

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<SetPermissionsComponent, HasUUID>,
    protected roleMngService: RoleMngService,
    protected translate: TranslateService,
    protected utils: UtilsService,
    @Inject(MAT_DIALOG_DATA) protected roleId: string
  ) {
    super(store, router, dialogRef);
  }

  ngOnInit() {
    this.getMenuList(MenuType.PC);
    this.getMenuList(MenuType.APP);
  }

  getMenuList(menuType: MenuType) {
    this.pcCheckedKeys = [];
    this.appCheckedKeys = [];
    this.roleMngService.getMenusByRole(menuType, this.roleId).subscribe(menus => {
      if (menus) {
        const rootNodeOptions: MenuTreeNodeOptions = {
          title: this.translate.instant('common.select-all'),
          key: '-1',
          id: '',
          parentId: '',
          children: []
        };
        const checkedKeys: string[] = [];
        menus.forEach(menu => {
          menu.title = menuType === MenuType.PC ? (menu.langKey ? this.translate.instant(menu.langKey) : menu.name) : menu.name;
          menu.key = menu.id;
          menu.checked && checkedKeys.push(menu.key);
        });
        if (menuType === MenuType.PC) {
          rootNodeOptions.children = this.utils.formatTree(menus) as MenuTreeNodeOptions[];
          this.pcMenus = rootNodeOptions.children.length > 0 ? [rootNodeOptions] : [];
          this.pcCheckedKeys = checkedKeys;
          this.pcExpandedKeys = ['-1'];
        } else if (menuType === MenuType.APP) {
          rootNodeOptions.children = this.utils.formatTree(menus) as MenuTreeNodeOptions[];
          this.appMenus = rootNodeOptions.children.length > 0 ? [rootNodeOptions] : [];
          this.appCheckedKeys = checkedKeys;
          this.appExpandedKeys = ['-1'];
        }
      }
    });
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {
    // 获取选中的节点id
    const pcCheckedNodes: NzTreeNode[] = [];
    this.pcTree.getCheckedNodeList().forEach(node => {
      pcCheckedNodes.push(...(this.utils.expandTreeNode(node).filter(_node => (_node.key !== '-1'))));
    });
    const appCheckedNodes: NzTreeNode[] = [];
    this.appTree.getCheckedNodeList().forEach(node => {
      appCheckedNodes.push(...(this.utils.expandTreeNode(node).filter(_node => (_node.key !== '-1'))));
    });
    const pcCheckedIds: string[] = pcCheckedNodes.map(node => (node.key));
    const appCheckedIds: string[] = appCheckedNodes.map(node => (node.key));

    // 获取半选中的节点id
    const pcHalfCheckedNodes: NzTreeNode[] = [];
    this.pcTree.getHalfCheckedNodeList().forEach(node => {
      pcHalfCheckedNodes.push(...(this.utils.expandTreeNode(node).filter(_node => (_node.key !== '-1' && _node.isHalfChecked))));
    });
    const appHalfCheckedNodes: NzTreeNode[] = [];
    this.appTree.getHalfCheckedNodeList().forEach(node => {
      appHalfCheckedNodes.push(...(this.utils.expandTreeNode(node).filter(_node => (_node.key !== '-1' && _node.isHalfChecked))));
    });
    const pcHalfCheckedIds: string[] = pcHalfCheckedNodes.map(node => (node.key));
    const appHalfCheckedIds: string[] = appHalfCheckedNodes.map(node => (node.key));

    this.roleMngService.setRolePermissions(
      [...pcCheckedIds, ...appCheckedIds],
      [...pcHalfCheckedIds, ...appHalfCheckedIds],
      this.roleId
    ).subscribe(() => {
      this.dialogRef.close(null);
      this.store.dispatch(new ActionNotificationShow({
        message: this.translate.instant('auth-mng.set-permissions-success'),
        type: 'success',
        duration: 3000
      }));
    });
  }

}
