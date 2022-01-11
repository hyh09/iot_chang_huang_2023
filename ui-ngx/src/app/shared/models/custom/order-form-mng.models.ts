import { BaseData } from "../base-data";
import { OrderFormId } from "../id/custom/order-form-id";

export interface OrderForm extends BaseData<OrderFormId> {
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
  standardTimeCosting: number;
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
  intendedStartTime?: number;
  intendedEndTime?: number;
  actualStartTime?: number;
  actualEndTime?: number;
  capacities?: number;
}
