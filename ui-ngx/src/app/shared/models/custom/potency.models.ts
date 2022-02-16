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
  tableName: string;
  keyName?: string;
  chartId?: string;
  properties: {
    title: string;
    name?: string;
    unit?: string;
    tsKvs: {
      ts: number;
      value: string;
    }[];
  }[];
}
