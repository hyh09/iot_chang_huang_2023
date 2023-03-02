import { BaseData } from "../base-data";
import { ChartId } from "../id/custom/chart-id.models";

export interface ChartProp {
  id: string;
  name: string;
  propertyType: 'DEVICE' | 'COMPONENT';
  title?: string;
  suffix?: string;
  unit: string;
}

export interface Chart extends BaseData<ChartId> {
  name: string;
  enable: boolean;
  properties: ChartProp[]
}