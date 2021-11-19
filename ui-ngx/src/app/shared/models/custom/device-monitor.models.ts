import { BaseData, DeviceProfile } from "@app/shared/public-api";
import { AlarmRecordId } from "../id/custom/alarm-record-id.models";

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
  offLineDeviceCount: number;
  onLineDeviceCount: number;
}
