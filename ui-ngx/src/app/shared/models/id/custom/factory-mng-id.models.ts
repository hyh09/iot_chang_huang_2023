import { EntityId } from '../entity-id';
import { EntityType } from '@shared/models/entity-type.models';

export class FactoryId implements EntityId {
  entityType = EntityType.FACTORY;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}

export class WorkShopId implements EntityId {
  entityType = EntityType.WORK_SHOP;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}

export class ProdLineId implements EntityId {
  entityType = EntityType.PROD_LINE;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}

export class DeviceId implements EntityId {
  entityType = EntityType.DEVICE;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}