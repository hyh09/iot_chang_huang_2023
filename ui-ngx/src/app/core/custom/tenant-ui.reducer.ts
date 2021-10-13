
import { TenantUIState } from '@core/custom/tenant-ui.models';
import { TenantUIActions, TenantUIActionTypes } from '@core/custom/tenant-ui.actions';
import {environment as env} from '@env/environment.prod';

export const initialState: TenantUIState = {
  applicationTitle: '环思物联',
  iconImageUrl: null,
  logoImageUrl: null,
  logoImageHeight: null,
  platformMainColor: null,
  platformTextMainColor: null,
  platformMenuColorActive: null,
  platformMenuColorHover: null,
  platformButtonColor: null,
  showNameVersion: false,
  platformName: env.appTitle,
  platformVersion: env.tbVersion
};

export function tenantUIReducer(
  state: TenantUIState = initialState,
  action: TenantUIActions
): TenantUIState {
  switch (action.type) {
    case TenantUIActionTypes.CHANGE:
      return { ...state, ...action.state  };
    default:
      return state;
  }
}
