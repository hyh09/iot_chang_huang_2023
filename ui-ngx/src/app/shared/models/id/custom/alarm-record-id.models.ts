import { EntityId } from '../entity-id';
import { EntityType } from '@shared/models/entity-type.models';

export class AlarmRecordId implements EntityId {
  entityType = EntityType.ALARM_RECORD;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}