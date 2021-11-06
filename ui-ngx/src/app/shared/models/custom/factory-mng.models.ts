import { BaseData } from "@app/shared/public-api";
import { FactoryId, ProdDeviceId, ProdLineId, WorkShopId } from "../id/custom/factory-mng-id";

export interface Factory extends BaseData<FactoryId> {

}

export interface WorkShop extends BaseData<WorkShopId> {

}

export interface ProdLine extends BaseData<ProdLineId> {

}

export interface ProdDevice extends BaseData<ProdDeviceId> {

}

export interface FactoryTreeNode {
  id: {
    id: string
  },
  code: string,
  logoImages: string
}
