import { Action } from '@ngrx/store';
import { TenantUIState } from '@core/custom/tenant-ui.models';

export enum TenantUIActionTypes {
  CHANGE = '[TenantUI] Change',
}

export class ActionTenantUIChangeAll implements Action {
  readonly type = TenantUIActionTypes.CHANGE;

  constructor(readonly state: TenantUIState) {
  }
}

export type TenantUIActions =
  | ActionTenantUIChangeAll;
