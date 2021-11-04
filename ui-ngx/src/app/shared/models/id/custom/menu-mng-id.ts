import { EntityId } from '../entity-id';
import { EntityType } from '@shared/models/entity-type.models';

export class MenuId implements EntityId {
  entityType = EntityType.MENU;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}