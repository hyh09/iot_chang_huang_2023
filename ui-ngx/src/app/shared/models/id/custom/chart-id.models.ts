import { EntityId } from '../entity-id';
import { EntityType } from '@shared/models/entity-type.models';

export class ChartId implements EntityId {
  entityType = EntityType.CHART;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}