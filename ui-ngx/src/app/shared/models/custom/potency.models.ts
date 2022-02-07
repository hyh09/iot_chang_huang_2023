import { BaseData, PageData } from "@app/shared/public-api";

export interface DeviceCapacity extends BaseData<any> {
  deviceId: string;
  value: string;
  deviceName: string;
  productionName?: string;
  workshopName?: string;
}

export interface DeviceCapacityList {
  data: DeviceCapacity[];
  totalValue: number;
  hasNext: boolean;
  totalElements: number;
  totalPages: number;
}

export interface DeviceEnergyConsumptionList extends PageData<object> {
  totalValue: string[];
}

export interface RunningState {
  [key: string]: { time: number; value: string; }[];
}
