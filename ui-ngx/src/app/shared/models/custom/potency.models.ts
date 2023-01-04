import { BaseData, PageData } from "@app/shared/public-api";

export interface DeviceCapacity extends BaseData<any> {
  deviceId: string;
  value: string;
  rename: string;
  productionName?: string;
  workshopName?: string;
}

export interface DeviceCapacityList {
  data: DeviceCapacity[];
  totalValue: number;
  hasNext: boolean;
  nextData: DeviceCapacity;
  totalElements: number;
  totalPages: number;
}

export interface GroupProduction {
  workOrderNumber: string;
  workingProcedureName: string;
  workerGroupName: string;
  workerNameList: string;
  ntrackQty: string;
  unit: string;
  cardNo: string;
  materialNo: string;
  colorName: string;
  createdTime: number;
  updatedTime: number;
  duration: string;
}

export interface DeviceEnergyConsumption extends BaseData<any> {
  deviceId?: string;
  rename: string;
  waterConsumption: string;
  electricConsumption: string;
  gasConsumption: string;
  capacityConsumption?: string;
  unitWaterConsumption?: string;
  unitElectricConsumption?: string;
  unitGasConsumption?: string;
}

export interface DeviceEnergyConsumptionList extends PageData<DeviceEnergyConsumption> {
  totalValue: {
    totalWaterConsumption: string;
    totalElectricConsumption: string;
    totalGasConsumption: string;
  }
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

export interface PotencyTop10Item {
  deviceId: string;
  rename: string;
  value: string;
  percent?: number;
}

export type PotencyTop10 = PotencyTop10Item[];

export interface PotencyIntervalItem {
  capacityOrEnergy: number;
  dateTime: string;
}

export type PotencyInterval = PotencyIntervalItem[];
