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
  id?: string;
  name: string;
  content: string;
  title: string;
  dictDataId: string;
}

export interface DeviceDataGroup {
  id?: string;
  name: string;
  groupPropertyList: Array<DeviceData>;
  editable?: boolean;
}

export interface DeviceCompProp {
  id: string;
  name: string;
  content: string;
  dictDataId: string;
  createdTime: number;
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

export enum ProtocolType {
  MODBUS = 'modbus'
}

export enum Operator {
  PLUS = '+',
  MINUS = '-',
  MULTIPLY = '*',
  DIVIDE = '/'
}

export enum ReadWrite {
  READ = 'READ',
  WRITE = 'WRITE',
  READ_WRITE = 'READ_WRITE'
}

export enum PointDataType {
  STRING = 'string',
  BYTES = 'bytes',
  BITS = 'bits',
  _16INT = '16int',
  _16UINT = '16uint',
  _16FLOAT = '16float',
  _32INT = '32int',
  _32UINT = '32uint',
  _32FLOAT = '32float',
  _64INT = '64int',
  _64UINT = '64uint',
  _64FLOAT = '64float'
}

export enum RegisterType {
  FIRST = '01',
  SECOND = '02',
  THIRD = '03',
  FOURTH = '04'
}

export enum LittleBig {
  LITTLE = 'little',
  BIG = 'big'
}

export interface DriverConfig {
  pointName?: string;
  description?: string;
  category?: string;
  dataType?: PointDataType;
  registerType?: RegisterType;
  registerAddress?: string;
  length?: string;
  operator?: Operator;
  operationValue?: string;
  readWrite?: ReadWrite;
  reverse?: LittleBig;
  littleEndian?: LittleBig;
}

export interface DeviceDictProp {
  id: string;
  name: string;
  title: string;
  type: string;
}

export interface DistributeConfigParams {
  deviceList: {
    deviceName: string;
    gatewayId: string;
  }[];
  type: string;
  driverVersion: string;
  driverConfigList: DriverConfig[];
}

export interface ProdCapacitySettings extends BaseData<any> {
  deviceId: string;
  deviceName: string;
  deviceFileName: string;
  dictName: string;
  deviceNo: string;
  flg: boolean;
}

export interface DictDevice extends BaseData<any> {
  deviceName?: string;
  gatewayId?: string;
  gatewayName?: string;
  factoryName?: string;
  workshopName?: string;
  productionLineName?: string;
}
