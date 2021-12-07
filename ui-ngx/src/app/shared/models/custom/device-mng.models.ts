import { DeviceDictionaryId } from '../id/custom/device-dictionary-id.models';
import { BaseData } from './../base-data';

export interface DataDictionary extends BaseData<DeviceDictionaryId> {
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
  dictDataId: string;
}

export interface DeviceDataGroup {
  name: string;
  groupPropertyList: Array<DeviceData>;
  editable?: boolean;
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

export interface DeviceDictionary extends BaseData<DeviceDictionaryId> {
  code: string;
  type: string;
  supplier: string;
  model: string;
  version: string;
  warrantyPeriod: string;
  picture: string;
  deviceModel: File;
  fileId: string;
  fileName: string;
  comment: string;
  isDefault: boolean;
  standardPropertyList: Array<DeviceData>;
  propertyList: Array<DeviceProperty>;
  groupList: Array<DeviceDataGroup>;
  componentList: Array<DeviceComp>;
}

export interface ProdCapacitySettings extends BaseData<any> {
  deviceId: string;
  deviceName: string;
  deviceFileName: string;
  dictName: string;
  deviceNo: string;
  flg: boolean;
}
