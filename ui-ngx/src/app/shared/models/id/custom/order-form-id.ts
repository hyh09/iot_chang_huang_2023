import { EntityId } from '../entity-id';
import { EntityType } from '@shared/models/entity-type.models';

export class OrderFormId implements EntityId {
  entityType = EntityType.ORDER_FORM;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}