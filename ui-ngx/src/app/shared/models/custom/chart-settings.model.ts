import { BaseData } from "../base-data";
import { ChartId } from "../id/custom/chart-id.models";

export interface ChartProp {
  id: string;
  name: string;
  propertyType: string;
  title: string;
}

export interface Chart extends BaseData<ChartId> {
  name: string;
  enable: boolean;
  properties: ChartProp[]
}