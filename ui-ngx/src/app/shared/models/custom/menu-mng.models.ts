import { BaseData } from './../base-data';
import { MenuId } from '../id/custom/menu-mng-id';

export enum MenuType {
  PC = 'PC',
  APP = 'APP'
}

export enum MenuLevel {
  FIRST = 0,
  SECOND = 1
}

export interface Menu extends BaseData<MenuId> {
  level: MenuLevel,
  parentId: string,
  parentName: string,
  menuType: MenuType,
  path: string,
  menuIcon: string,
  menuImages: string,
  url: string
}
