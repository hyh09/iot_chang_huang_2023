import { TableTreeNodeOptions, TreeNodeOptions } from "@app/core/public-api";
import { BaseData } from "@app/shared/public-api";
import { DeviceId, FactoryId, ProdLineId, WorkShopId } from "../id/custom/factory-mng-id.models";
import { DeviceProperty, DeviceDataGroup, DeviceComp, DeviceData } from "./device-mng.models";

interface CommonData {
  key?: string;
  title?: string;
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
  key?: string;
  title?: string;
  country?: string;
  city?: string;
  address?: string;
  postalCode?: string;
  logoImages?: string;
  rowType?: FactoryRowType;
  latitude?: string | number;
  longitude?: string | number;
}

export interface WorkShop extends BaseData<WorkShopId>, CommonData {
  key?: string;
  title?: string;
  factoryId?: string;
  factoryName?: string;
  logoImages?: string;
  bgImages?: string;
  rowType: FactoryRowType;
}

export interface ProdLine extends BaseData<ProdLineId>, CommonData {
  key?: string;
  title?: string;
  factoryId?: string;
  factoryName?: string;
  workshopId?: string;
  workshopName?: string;
  rowType: FactoryRowType;
  logoImages?: string;
}

export interface ProdDevice extends BaseData<DeviceId>, CommonData {
  key?: string;
  title?: string;
  factoryId?: string;
  factoryName?: string;
  workshopId?: string;
  workshopName?: string;
  productionLineId?: string;
  productionLineName?: string;
  dictDeviceId?: string;
  deviceNo?: string;
  rowType?: FactoryRowType;
  type?: string,
  supplier?: string,
  model?: string,
  version?: string,
  warrantyPeriod?: string,
  picture?: string,
  fileName?: string,
  logoImages?: string,
  comment?: string,
  standardPropertyList: Array<DeviceData>;
  propertyList?: Array<DeviceProperty>,
  groupList?: Array<DeviceDataGroup>,
  componentList?: Array<DeviceComp>
}

export interface NotDistributedDevice extends CommonData {
  id: string;
  name: string;
  deviceProfileId: string;
  picture: string;
  rowType: FactoryRowType;
}

export interface FactoryMngList {
  factoryList: Factory[];
  workshopList: WorkShop[];
  productionLineList: ProdLine[];
  deviceVoList: ProdDevice[];
  notDistributionList: NotDistributedDevice[];
}

export interface FactoryTableOriginRow {
  key?: string;
  title?: string;
  parentId?: string;
  code?: string;
  name?: string;
  logoImages?: string;
  images?: string;
  country?: string;
  city?: string;
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

export interface FactoryTableTreeNode extends TableTreeNodeOptions {
  level?: number;
  expand?: boolean;
  parent?: FactoryTableTreeNode;
  children?: FactoryTableTreeNode[];
  code?: string;
  name?: string;
  logoImages?: string;
  country?: string;
  city?: string;
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

export interface FactoryTreeNodeOptions extends TreeNodeOptions {
  rowType?: FactoryRowType;
  factoryId?: string;
  factoryName?: string;
  workshopId?: string;
  workshopName?: string;
  productionLineId?: string;
  productionLineName?: string;
}

export interface FactoryTreeNodeIds {
  factoryId?: string;
  workshopId?: string;
  productionLineId?: string;
  deviceId?: string;
}

export interface FactoryTreeList {
  factories: Factory[];
  workshops: WorkShop[];
  productionLines: ProdLine[];
  devices: ProdDevice[];
  undistributedDevices: ProdDevice[];
}
