import { BaseData } from "../base-data";
import { OrderFormId } from "../id/custom/order-form-id";

export interface OrderForm extends OrderCapacity {
  orderNo: string;
  factoryName: string;
  emergencyDegree: string;
  merchandiser: string;
  salesman: string;
  intendedTime: number;
  creator: string;
  additionalAmount: number;
  bizPractice: string;
  comment: string;
  contractNo: string;
  currency: string;
  customer: string;
  customerOrderNo: string;
  exchangeRate: string;
  factoryId: string;
  num: number;
  planDevices: OrderDevice[];
  overShipment: string;
  paymentMethod: string;
  productionLineId: string;
  productionLineName: string;
  refOrderNo: string;
  season: string;
  shortShipment: string;
  standardAvailableTime: number;
  takeTime: number;
  taxRate: string;
  taxes: string;
  technologicalRequirements: string;
  total: number;
  totalAmount: number;
  type: string;
  unit: string;
  unitPriceType: string;
  workshopId: string;
  workshopName: string;
}

export interface OrderDevice {
  id?: string;
  deviceId?: string;
  deviceName?: string;
  enabled?: boolean;
  intendedCapacity?: string;
  actualCapacity?: string;
  intendedStartTime?: number;
  intendedEndTime?: number;
  actualStartTime?: number;
  actualEndTime?: number;
  maintainStartTime?: number;
  maintainEndTime?: number;
  capacities?: number;
}

export interface OrderCapacity extends BaseData<OrderFormId> {
  capacities: number;
  completeness: number;
  creator: string;
  emergencyDegree: string;
  factoryName: string;
  intendedTime: number;
  merchandiser: string;
  orderNo: string;
  salesman: string;
  total: number;
  totalAmount: number;
}
