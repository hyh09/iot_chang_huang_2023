import { BaseData } from './../base-data';
import { DataDictionaryId } from '../id/custom/data-dictionary-id.models';

export interface DataDictionary extends BaseData<DataDictionaryId> {
  code: string;
  icon: string;
  type: string;
  unit: string;
  comment: string;
  picture: string;
}

export interface DeviceProperty {
  name: string;
  content: string;
}

export interface DeviceData {
  name: string;
  content: string;
  title: string;
}

export interface DeviceDataGroup {
  name: string;
  groupPropertyList: Array<DeviceData>;
}

export interface DeviceCompProp {
  id: string;
  name: string;
  content: string;
  dictDataId: string;
  createdTime: string;
  componentId: string;
  title: string;
  unit: string;
}

export interface DeviceComp {
  code: string;
  comment?: string;
  componentList?: Array<DeviceComp>;
  dictDeviceId?: string;
  icon?: string;
  id?: string;
  model?: string;
  name: string;
  parentId?: string;
  picture?: string;
  supplier?: string;
  type?: string;
  version?: string;
  warrantyPeriod?: string;
  propertyList?: DeviceCompProp[]
}

export interface DeviceCompTreeNode extends DeviceComp {
  level: number;
  expand: boolean;
  parent?: DeviceCompTreeNode;
}

export interface DeviceDictionary extends BaseData<DataDictionaryId> {
  code: string;
  type: string;
  supplier: string;
  model: string;
  version: string;
  warrantyPeriod: string;
  picture: string;
  comment: string;
  propertyList: Array<DeviceProperty>;
  groupList: Array<DeviceDataGroup>;
  componentList: Array<DeviceComp>;
}
