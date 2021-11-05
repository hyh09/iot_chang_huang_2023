import { EntityId } from '../entity-id';
import { EntityType } from '@shared/models/entity-type.models';

export class UserInfoId implements EntityId {
  entityType = EntityType.USER_MNG;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}