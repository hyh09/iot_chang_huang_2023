import { BaseData, DeviceProfile } from "@app/shared/public-api";
import { AlarmRecordId } from "../id/custom/alarm-record-id.models";
import { ChartProp } from "./chart-settings.model";
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
  id?: string;
  name: string;
  content?: string;
  createdTime?: number;
  dictDataId?: string;
  title?: string;
  unit: string;
  chartId?: string;
  attributeNames?: string[];
  associatedId?: string;
}

export interface DeviceHistoryProp {
  isShowChart: false;
  name: string;
  title: string;
  tsKvs: {
    ts: number;
    value: string;
  }[];
  unit: string;
}

export interface DevicePropHistory {
  enable?: boolean;
  name?: boolean;
  properties?: DeviceHistoryProp[];
}

export interface DevicePropGroup {
  id?: string;
  name?: string;
  groupPropertyList: DeviceProp[]
}

export interface AssociatedPropItem extends ChartProp {
  content?: string;
  associatedId?: string;
}

export interface AssociatedProp {
  id?: string;
  name: string;
  enable: boolean;
  properties: AssociatedPropItem[];
  createdTime: number;
  firstPropName: string;
}

export interface DeviceDetails extends DeviceBaseInfo {
  alarmTimesList?: AlarmTimesListItem[];
  resultList?: DevicePropGroup[]; // 设备参数
  resultUngrouped?: DevicePropGroup; // 设备属性
  componentList?: DeviceComp[]; // 设备部件
  dictDeviceGraphs?: AssociatedProp[]; // 关联的属性
}
