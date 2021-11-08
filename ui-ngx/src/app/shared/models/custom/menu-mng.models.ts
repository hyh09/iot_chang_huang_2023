import { BaseData, CustomBaseData } from './../base-data';
import { MenuId } from '../id/custom/menu-mng-id';
import { TreeNodeOptions } from '@app/core/public-api';

export enum MenuType {
  PC = 'PC',
  APP = 'APP'
}

export enum MenuLevel {
  FIRST = 0,
  SECOND = 1
}

export interface Menu extends BaseData<MenuId> {
  langKey: string,
  level: MenuLevel,
  parentId: string,
  parentName: string,
  menuType: MenuType,
  path: string,
  menuIcon: string,
  menuImages: string,
  url: string,
  isButton: boolean
}

export interface MenuTreeNodeOptions extends CustomBaseData, TreeNodeOptions {
  name?: string,
  langKey?: string,
  level?: MenuLevel,
  parentId: string,
  parentName?: string,
  menuType?: MenuType,
  path?: string,
  menuIcon?: string,
  menuImages?: string,
  url?: string,
  isButton?: boolean
}
