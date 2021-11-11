import { TableTreeNodeOptions, TreeNodeOptions } from "@app/core/public-api";

interface BaseData {
  id: {
    id: string
  };
  key: string;
  parentId?: string;
  code: string;
  name: string;
  createdTime?: number;
  createdUser?: string;
  updatedTime?: number;
  updatedUser?: string;
  remark?: string;
}

export interface Factory extends BaseData {
  mobile: string;
  email: string;
  address?: string;
  postalCode?: string;
  logoImages?: string;
}

export interface WorkShop extends BaseData {
  factoryId: string;
  factoryName: string;
  logoImages?: string;
  bgImages?: string;
}

export interface ProdLine extends BaseData {
  factoryId: string;
  factoryName: string;
  workshopId: string;
  workshopName: string;
  logoImages?: string;
}

export interface ProdDevice extends BaseData {
  factoryId: string;
  factoryName: string;
  workshopId: string;
  workshopName: string;
  productionLineId?: string;
  productionLineName?: string;
  deviceNo?: string;
  images?: string;
}

export interface FactoryMngList {
  factoryEntityList: Factory[];
  workshopEntityList: WorkShop[];
  productionLineEntityList: ProdLine[];
  deviceEntityList: ProdDevice[];
}

export interface FactoryTableOriginRow {
  key: string;
  code: string;
  name: string;
  logoImages?: string;
  images?: string;
  address?: string;
  createdTime?: number;
}

export interface FactoryTableTreeNode extends TableTreeNodeOptions {
  level?: number;
  expand?: boolean;
  parent?: FactoryTableTreeNode;
  children?: FactoryTableTreeNode[];
  code?: string;
  name?: string;
  image?: string;
  address?: string;
  createdTime?: number;
}
