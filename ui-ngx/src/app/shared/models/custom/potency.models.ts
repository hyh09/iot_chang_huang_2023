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

export interface ProcedureProduction {
  cardNo: string;
  orderNo: string;
  customerName: string;
  materialName: string;
  colorName: string;
  numberOfCards: string;
  sremark: string;
  workingProcedureName: string;
  ntrackQty: string;
  theoreticalTime: string;
  actualTime: string;
  timeoutMinutes: string;
  overTimeRatio: string;
  workerGroupName: string;
  createdTime: number;
  updatedTime: number;
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

export interface OrderConsumption extends BaseData<any> {
  orderNo: string;
  customerName: string;
  materialName: string;
  colorName: string;
  numberOfOrder: string;
  numberOfCards: string;
  sremark: string;
  duration: string;
  water: string;
  electricity: string;
  gas: string;
  uguid: string;
}

export interface OrderProcessCard extends BaseData<any> {
  deviceName: string;
  scardNo: string;
  materialName: string;
  colorName: string;
  workerGroupName: string;
  workerName: string;
  nTrackQty: string;
  sRemark: string;
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
