import { BaseData, DeviceProfile } from "@app/shared/public-api";
import { AlarmRecordId } from "../id/custom/alarm-record-id.models";
import { DeviceComp } from "./device-mng.models";

export enum AlarmStatusType {
  ANY = 'ANY',
  ACTIVE_UNACK = 'ACTIVE_UNACK',
  ACTIVE_ACK = 'ACTIVE_ACK',
  CLEARED_ACK = 'CLEARED_ACK'
}

export enum AlarmLevelType {
  ANY = 'ANY',
  CRITICAL = 'CRITICAL',
  MAJOR = 'MAJOR',
  MINOR = 'MINOR',
  WARNING = 'WARNING',
  INDETERMINATE = 'INDETERMINATE'
}

export interface AlarmRecord extends BaseData<AlarmRecordId> {
  title: string;
  info: string;
  status: AlarmStatusType;
  statusStr: string;
  level: AlarmLevelType;
  levelStr: string;
  isCanBeClear: boolean;
  isCanBeConfirm: boolean;
}

export interface AlarmRuleInfo extends DeviceProfile {
  dictDeviceIdList?: string[];
}

export interface AlarmTimesListItem {
  time: string;
  num: number;
}

export interface DeviceItem {
  id: string;
  name: string;
  image: string;
  isOnLine: boolean;
}

export interface DevicePageData {
  data: DeviceItem[];
  hasNext: boolean;
  totalElements: number;
  totalPages: number;
}

export interface RealTimeData {
  alarmTimesList: AlarmTimesListItem[];
  allDeviceCount: number;
  devicePageData: DevicePageData;
  deviceIdList: string[];
  offLineDeviceCount: number;
  onLineDeviceCount: number;
}

export interface DeviceBaseInfo {
  picture?: string;
  name?: string;
  factoryName?: string;
  workShopName?: string;
  productionLineName?: string;
}

export interface DeviceProp {
  id: string;
  name: string;
  content: string;
  createdTime: number;
  dictDataId: number;
  title: string;
  unit: string;
}

export interface DevicePropHistory {
  isShowChart: boolean;
  propertyVOList: DeviceProp[];
}

export interface DevicePropGroup {
  id?: string;
  name?: string;
  groupPropertyList: DeviceProp[]
}

export interface DeviceDetails extends DeviceBaseInfo {
  alarmTimesList?: AlarmTimesListItem[];
  resultList?: DevicePropGroup[]; // 设备参数
  resultUngrouped?: DevicePropGroup; // 设备属性
  componentList?: DeviceComp[]; // 设备部件
}
