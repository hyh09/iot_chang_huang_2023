import { EntityId } from './../entity-id';
import { EntityType } from '@shared/models/entity-type.models';

export class DataDictionaryId implements EntityId {
  entityType = EntityType.DATA_DICTIONARY;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}