import { BaseData } from "@app/shared/public-api";
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
