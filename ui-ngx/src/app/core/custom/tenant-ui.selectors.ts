import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { TenantUIState } from '@core/custom/tenant-ui.models';


export const selectTenantUIState = createFeatureSelector<AppState, TenantUIState>(
  'tenantUI'
);

export const selectTenantUI = createSelector(
  selectTenantUIState,
  (state: TenantUIState) => state
);

export const selectPlatformName = createSelector(
  selectTenantUIState,
  (state: TenantUIState) => state.platformName
);

export const selectPlatformVersion = createSelector(
  selectTenantUIState,
  (state: TenantUIState) => state.platformVersion
);

export const selectShowNameVersion = createSelector(
  selectTenantUIState,
  (state: TenantUIState) => state.showNameVersion
);
