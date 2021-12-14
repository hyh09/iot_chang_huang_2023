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
  entityType = EntityType.WORKSHOP;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}

export class ProdLineId implements EntityId {
  entityType = EntityType.PRODUCTION_LINE;
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