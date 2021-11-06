import { EntityId } from '../entity-id';
import { EntityType } from '@shared/models/entity-type.models';

export class FactoryId implements EntityId {
  entityType = EntityType.MENU;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}

export class WorkShopId implements EntityId {
  entityType = EntityType.MENU;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}

export class ProdLineId implements EntityId {
  entityType = EntityType.MENU;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}

export class ProdDeviceId implements EntityId {
  entityType = EntityType.MENU;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}
