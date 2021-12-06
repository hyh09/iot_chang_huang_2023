import { BaseData } from "@app/shared/public-api";

export interface FactoryVersion extends BaseData<any> {
  factoryName: string;
  factoryVersion: string;
  gatewayName: string;
  active: boolean;
  publishTime: number;
}