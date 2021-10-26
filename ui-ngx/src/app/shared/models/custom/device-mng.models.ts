import { BaseData } from './../base-data';
import { DataDictionaryId } from '../id/custom/data-dictionary-id';

export interface DataDictionary extends BaseData<DataDictionaryId> {
  code: string,
  icon: string,
  type: string,
  unit: string,
  comment: string,
  picture: string
}

export interface DeviceProperty {
  name: string,
  content: string
}

export interface DeviceData {
  name: string,
  content: string
}

export interface DeviceDataGroup {
  name: string,
  groupPropertyList: Array<DeviceData>
}

export interface DeviceDictionary extends BaseData<DataDictionaryId> {
  code: string,
  type: string,
  supplier: string,
  model: string,
  version: string,
  warrantyPeriod: string,
  picture: string,
  propertyList: Array<DeviceProperty>,
  groupList: Array<DeviceDataGroup>
}
