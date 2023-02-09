import { BaseData } from "../base-data";
import { OrderFormId } from "../id/custom/order-form-id";
import { ProdCardId } from "../id/custom/prod-card-id";

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
  isDone: boolean;
}

export interface OrderDevice {
  id?: string;
  deviceId?: string;
  rename?: string;
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

export interface OrderProgress extends BaseData<any> {
  ddeliveryDate: string;
  nqty: string;
  scolorName: string;
  scustomerName: string;
  sfinishingMethod: string;
  smaterialName: string;
  sorderNo: string;
}

export interface processCardProgress extends BaseData<ProdCardId> {
  scardNo: string;
  ddeliveryDate: string;
  nqty: string;
  scolorName: string;
  scustomerName: string;
  sfinishingMethod: string;
  nplanOutputQty: string;
  smaterialName: string;
  sorderNo: string;
  sworkingProcedureName: string;
  sworkingProcedureNameNext: string;
}

export interface ProdProgress extends BaseData<any> {
  npercentValue: string;
  ntrackQty: string;
  sequipmentName: string;
  slocation: string;
  sworkerGroupName: string;
  sworkerNameList: string;
  sworkingProcedureName: string;
  sworkingProcedureNo: string;
  tfactEndTime: string;
  tfactStartTime: string;
}
