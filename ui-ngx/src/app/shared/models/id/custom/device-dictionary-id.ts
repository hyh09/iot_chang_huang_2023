import { EntityId } from './../entity-id';
import { EntityType } from '@shared/models/entity-type.models';

export class DeviceDictionaryId implements EntityId {
  entityType = EntityType.DEVICE_DICTIONARY;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}