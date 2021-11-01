import { EntityId } from '../entity-id';
import { EntityType } from '@shared/models/entity-type.models';

export class RoleId implements EntityId {
  entityType = EntityType.ROLE_MNG;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}