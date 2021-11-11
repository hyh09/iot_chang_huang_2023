import { TableTreeNodeOptions } from "@app/core/public-api";
import { BaseData } from "@app/shared/public-api";
import { DeviceId, FactoryId, ProdLineId, WorkShopId } from "../id/custom/factory-mng-id.models";

interface CommonData {
  key?: string;
  parentId?: string;
  code?: string;
  createdTime?: number;
  createdUser?: string;
  updatedTime?: number;
  updatedUser?: string;
  remark?: string;
}

export declare type FactoryRowType = 'factory' | 'workShop' | 'prodLine' | 'device';

export interface Factory extends BaseData<FactoryId>, CommonData {
  mobile?: string;
  email?: string;
  address?: string;
  postalCode?: string;
  logoImages?: string;
  rowType?: FactoryRowType;
}

export interface WorkShop extends BaseData<WorkShopId>, CommonData {
  factoryId?: string;
  factoryName?: string;
  logoImages?: string;
  bgImages?: string;
  rowType: FactoryRowType;
}

export interface ProdLine extends BaseData<ProdLineId>, CommonData {
  factoryId?: string;
  factoryName?: string;
  workshopId?: string;
  workshopName?: string;
  logoImages?: string;
  rowType: FactoryRowType;
}

export interface ProdDevice extends BaseData<DeviceId>, CommonData {
  factoryId?: string;
  factoryName?: string;
  workshopId?: string;
  workshopName?: string;
  productionLineId?: string;
  productionLineName?: string;
  deviceNo?: string;
  images?: string;
  rowType?: FactoryRowType;
}

export interface FactoryMngList {
  factoryEntityList: Factory[];
  workshopEntityList: WorkShop[];
  productionLineEntityList: ProdLine[];
  deviceEntityList: ProdDevice[];
}

export interface FactoryTableOriginRow {
  key?: string;
  parentId?: string;
  code?: string;
  name?: string;
  logoImages?: string;
  images?: string;
  address?: string;
  createdTime?: number;
  rowType?: FactoryRowType;
}

export interface FactoryTableTreeNode extends TableTreeNodeOptions {
  level?: number;
  expand?: boolean;
  parent?: FactoryTableTreeNode;
  children?: FactoryTableTreeNode[];
  code?: string;
  name?: string;
  logoImages?: string;
  address?: string;
  createdTime?: number;
  rowType?: FactoryRowType;
  factoryId?: string;
  factoryName?: string;
  workshopId?: string;
  workshopName?: string;
  productionLineId?: string;
  productionLineName?: string;
}
