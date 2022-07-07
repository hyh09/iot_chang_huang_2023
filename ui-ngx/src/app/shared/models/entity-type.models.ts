///
/// Copyright © 2016-2021 The Thingsboard Authors
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

import { TenantId } from './id/tenant-id';
import { BaseData, HasId } from '@shared/models/base-data';

export enum EntityType {
  TENANT = 'TENANT',
  TENANT_PROFILE = 'TENANT_PROFILE',
  CUSTOMER = 'CUSTOMER',
  USER = 'USER',
  DASHBOARD = 'DASHBOARD',
  ASSET = 'ASSET',
  DEVICE = 'DEVICE',
  DEVICE_PROFILE = 'DEVICE_PROFILE',
  ALARM = 'ALARM',
  RULE_CHAIN = 'RULE_CHAIN',
  RULE_NODE = 'RULE_NODE',
  EDGE = 'EDGE',
  ENTITY_VIEW = 'ENTITY_VIEW',
  WIDGETS_BUNDLE = 'WIDGETS_BUNDLE',
  WIDGET_TYPE = 'WIDGET_TYPE',
  API_USAGE_STATE = 'API_USAGE_STATE',
  TB_RESOURCE = 'TB_RESOURCE',
  OTA_PACKAGE = 'OTA_PACKAGE',
  RPC = 'RPC',
  DATA_DICTIONARY = 'DATA_DICTIONARY',
  DEVICE_DICTIONARY = 'DEVICE_DICTIONARY',
  FACTORY = 'FACTORY',
  WORKSHOP = 'WORKSHOP',
  PRODUCTION_LINE = 'PRODUCTION_LINE',
  MENU = 'MENU',
  USER_MNG = 'USER_MNG',
  ROLE_MNG = 'ROLE_MNG',
  ALARM_RECORD = 'ALARM_RECORD',
  ALARM_RULES = 'ALARM_RULES',
  DEVICE_HISTORY = 'DEVICE_HISTORY',
  POTENCY = 'POTENCY',
  POTENCY_HISTORY = 'POTENCY_HISTORY',
  FACTORY_VERSION = 'FACTORY_VERSION',
  PROD_CAPACITY_SETTINGS = 'PROD_CAPACITY_SETTINGS',
  ORDER_FORM = 'ORDER_FORM',
  CHART_SETTINGS = 'CHART_SETTINGS',
  CHART = 'CHART',
  PROD_MNG = 'PROD_MNG',
  MNG_CALENDAR = 'MNG_CALENDAR',
  ORDER = 'ORDER',
  ORDER_PLAN = 'ORDER_PLAN',
  TENANT_MENU_ROLE = 'TENANT_MENU_ROLE',
  USER_MENU_ROLE = 'USER_MENU_ROLE',
  USER_CREDENTIALS = 'USER_CREDENTIALS',
  PRODUCTION_CALENDAR = 'PRODUCTION_CALENDAR'
}

export enum AliasEntityType {
  CURRENT_CUSTOMER = 'CURRENT_CUSTOMER',
  CURRENT_TENANT = 'CURRENT_TENANT',
  CURRENT_USER = 'CURRENT_USER',
  CURRENT_USER_OWNER = 'CURRENT_USER_OWNER'
}

export interface EntityTypeTranslation {
  type?: string;
  typePlural?: string;
  list?: string;
  nameStartsWith?: string;
  details?: string;
  add?: string;
  noEntities?: string;
  selectedEntities?: string;
  search?: string;
}

export interface EntityTypeResource<T> {
  helpLinkId: string;
  helpLinkIdForEntity?(entity: T): string;
}

export const entityTypeTranslations = new Map<EntityType | AliasEntityType, EntityTypeTranslation>(
  [
    [
      EntityType.TENANT,
      {
        type: 'entity.type-tenant',
        typePlural: 'entity.type-tenants',
        list: 'entity.list-of-tenants',
        nameStartsWith: 'entity.tenant-name-starts-with',
        details: 'tenant.tenant-details',
        add: 'tenant.add',
        noEntities: 'tenant.no-tenants-text',
        search: 'tenant.search',
        selectedEntities: 'tenant.selected-tenants'
      }
    ],
    [
      EntityType.TENANT_PROFILE,
      {
        type: 'entity.type-tenant-profile',
        typePlural: 'entity.type-tenant-profiles',
        list: 'entity.list-of-tenant-profiles',
        nameStartsWith: 'entity.tenant-profile-name-starts-with',
        details: 'tenant-profile.tenant-profile-details',
        add: 'tenant-profile.add',
        noEntities: 'tenant-profile.no-tenant-profiles-text',
        search: 'tenant-profile.search',
        selectedEntities: 'tenant-profile.selected-tenant-profiles'
      }
    ],
    [
      EntityType.CUSTOMER,
      {
        type: 'entity.type-customer',
        typePlural: 'entity.type-customers',
        list: 'entity.list-of-customers',
        nameStartsWith: 'entity.customer-name-starts-with',
        details: 'customer.customer-details',
        add: 'customer.add',
        noEntities: 'customer.no-customers-text',
        search: 'customer.search',
        selectedEntities: 'customer.selected-customers'
      }
    ],
    [
      EntityType.USER,
      {
        type: 'entity.type-user',
        typePlural: 'entity.type-users',
        list: 'entity.list-of-users',
        nameStartsWith: 'entity.user-name-starts-with',
        details: 'user.user-details',
        add: 'user.add',
        noEntities: 'user.no-users-text',
        search: 'user.search',
        selectedEntities: 'user.selected-users'
      }
    ],
    [
      EntityType.DEVICE,
      {
        type: 'entity.type-device',
        typePlural: 'entity.type-devices',
        list: 'entity.list-of-devices',
        nameStartsWith: 'entity.device-name-starts-with',
        details: 'device.device-details',
        add: 'device.add',
        noEntities: 'device.no-devices-text',
        search: 'device.search',
        selectedEntities: 'device.selected-devices'
      }
    ],
    [
      EntityType.DEVICE_PROFILE,
      {
        type: 'entity.type-device-profile',
        typePlural: 'entity.type-device-profiles',
        list: 'entity.list-of-device-profiles',
        nameStartsWith: 'entity.device-profile-name-starts-with',
        details: 'device-profile.device-profile-details',
        add: 'device-profile.add',
        noEntities: 'device-profile.no-device-profiles-text',
        search: 'device-profile.search',
        selectedEntities: 'device-profile.selected-device-profiles'
      }
    ],
    [
      EntityType.ASSET,
      {
        type: 'entity.type-asset',
        typePlural: 'entity.type-assets',
        list: 'entity.list-of-assets',
        nameStartsWith: 'entity.asset-name-starts-with',
        details: 'asset.asset-details',
        add: 'asset.add',
        noEntities: 'asset.no-assets-text',
        search: 'asset.search',
        selectedEntities: 'asset.selected-assets'
      }
    ],
    [
      EntityType.EDGE,
      {
        type: 'entity.type-edge',
        typePlural: 'entity.type-edges',
        list: 'entity.list-of-edges',
        nameStartsWith: 'entity.edge-name-starts-with',
        details: 'edge.edge-details',
        add: 'edge.add',
        noEntities: 'edge.no-edges-text',
        search: 'edge.search',
        selectedEntities: 'edge.selected-edges'
      }
    ],
    [
      EntityType.ENTITY_VIEW,
      {
        type: 'entity.type-entity-view',
        typePlural: 'entity.type-entity-views',
        list: 'entity.list-of-entity-views',
        nameStartsWith: 'entity.entity-view-name-starts-with',
        details: 'entity-view.entity-view-details',
        add: 'entity-view.add',
        noEntities: 'entity-view.no-entity-views-text',
        search: 'entity-view.search',
        selectedEntities: 'entity-view.selected-entity-views'
      }
    ],
    [
      EntityType.RULE_CHAIN,
      {
        type: 'entity.type-rulechain',
        typePlural: 'entity.type-rulechains',
        list: 'entity.list-of-rulechains',
        nameStartsWith: 'entity.rulechain-name-starts-with',
        details: 'rulechain.rulechain-details',
        add: 'rulechain.add',
        noEntities: 'rulechain.no-rulechains-text',
        search: 'rulechain.search',
        selectedEntities: 'rulechain.selected-rulechains'
      }
    ],
    [
      EntityType.RULE_NODE,
      {
        type: 'entity.type-rulenode',
        typePlural: 'entity.type-rulenodes',
        list: 'entity.list-of-rulenodes',
        nameStartsWith: 'entity.rulenode-name-starts-with'
      }
    ],
    [
      EntityType.DASHBOARD,
      {
        type: 'entity.type-dashboard',
        typePlural: 'entity.type-dashboards',
        list: 'entity.list-of-dashboards',
        nameStartsWith: 'entity.dashboard-name-starts-with',
        details: 'dashboard.dashboard-details',
        add: 'dashboard.add',
        noEntities: 'dashboard.no-dashboards-text',
        search: 'dashboard.search',
        selectedEntities: 'dashboard.selected-dashboards'
      }
    ],
    [
      EntityType.ALARM,
      {
        type: 'entity.type-alarm',
        typePlural: 'entity.type-alarms',
        list: 'entity.list-of-alarms',
        nameStartsWith: 'entity.alarm-name-starts-with',
        details: 'dashboard.dashboard-details',
        noEntities: 'alarm.no-alarms-prompt',
        search: 'alarm.search',
        selectedEntities: 'alarm.selected-alarms'
      }
    ],
    [
      EntityType.API_USAGE_STATE,
      {
        type: 'entity.type-api-usage-state'
      }
    ],
    [
      EntityType.WIDGETS_BUNDLE,
      {
        details: 'widgets-bundle.widgets-bundle-details',
        add: 'widgets-bundle.add',
        noEntities: 'widgets-bundle.no-widgets-bundles-text',
        search: 'widgets-bundle.search',
        selectedEntities: 'widgets-bundle.selected-widgets-bundles'
      }
    ],
    [
      AliasEntityType.CURRENT_CUSTOMER,
      {
        type: 'entity.type-current-customer',
        list: 'entity.type-current-customer'
      }
    ],
    [
      AliasEntityType.CURRENT_TENANT,
      {
        type: 'entity.type-current-tenant',
        list: 'entity.type-current-tenant'
      }
    ],
    [
      AliasEntityType.CURRENT_USER,
      {
        type: 'entity.type-current-user',
        list: 'entity.type-current-user'
      }
    ],
    [
      AliasEntityType.CURRENT_USER_OWNER,
      {
        type: 'entity.type-current-user-owner',
        list: 'entity.type-current-user-owner'
      }
    ],
    [
      EntityType.TB_RESOURCE,
      {
        type: 'entity.type-tb-resource',
        details: 'resource.resource-library-details',
        add: 'resource.add',
        noEntities: 'resource.no-resource-text',
        search: 'resource.search',
        selectedEntities: 'resource.selected-resources'
      }
    ],
    [
      EntityType.OTA_PACKAGE,
      {
        type: 'entity.type-ota-package',
        details: 'ota-update.ota-update-details',
        add: 'ota-update.add',
        noEntities: 'ota-update.no-packages-text',
        search: 'ota-update.search',
        selectedEntities: 'ota-update.selected-package'
      }
    ],
    [
      EntityType.DATA_DICTIONARY,
      {
        type: 'entity.type-data-dic',
        typePlural: 'entity.type-data-dics',
        list: 'entity.list-of-data-dics',
        nameStartsWith: 'entity.data-dic-name-starts-with',
        details: 'device-mng.data-dic-details',
        add: 'device-mng.add-data-dic',
        noEntities: 'device-mng.no-data-dics-text',
        selectedEntities: 'device-mng.selected-data-dics'
      }
    ],
    [
      EntityType.DEVICE_DICTIONARY,
      {
        type: 'entity.type-device-dic',
        typePlural: 'entity.type-device-dics',
        list: 'entity.list-of-device-dics',
        nameStartsWith: 'entity.device-dic-name-starts-with',
        details: 'device-mng.device-dic-details',
        add: 'device-mng.add-device-dic',
        noEntities: 'device-mng.no-device-dics-text',
        selectedEntities: 'device-mng.selected-device-dics'
      }
    ],
    [
      EntityType.FACTORY,
      {
        type: 'device-mng.factory',
        add: 'device-mng.add-factory',
        details: 'device-mng.factory-details'
      }
    ],
    [
      EntityType.WORKSHOP,
      {
        type: 'device-mng.work-shop',
        add: 'device-mng.add-work-shop',
        details: 'device-mng.work-shop-details'
      }
    ],
    [
      EntityType.PRODUCTION_LINE,
      {
        type: 'device-mng.prod-line',
        add: 'device-mng.add-prod-line',
        details: 'device-mng.prod-line-details'
      }
    ],
    [
      EntityType.MENU,
      {
        type: 'entity.type-menu',
        typePlural: 'entity.type-menus',
        list: 'entity.list-of-menus',
        nameStartsWith: 'entity.menu-name-starts-with',
        details: 'menu-mng.menu-details',
        add: 'menu-mng.add-menu',
        noEntities: 'menu-mng.no-menus-text',
        selectedEntities: 'menu-mng.selected-menus'
      }
    ],
    [
      EntityType.USER_MNG,
      {
        type: 'entity.type-user',
        typePlural: 'entity.type-user',
        list: 'entity.list-of-user',
        nameStartsWith: 'entity.user-name-starts-with',
        details: 'user.user-details',
        add: 'user.add',
        noEntities: 'user.no-users-text',
        selectedEntities: 'user.selected-users'
      }
    ],
    [
      EntityType.ROLE_MNG,
      {
        type: 'entity.type-role',
        typePlural: 'entity.type-roles',
        list: 'entity.list-of-role',
        nameStartsWith: 'entity.role-name-starts-with',
        details: 'auth-mng.role-details',
        add: 'auth-mng.add-role',
        noEntities: 'auth-mng.no-roles-text',
        selectedEntities: 'auth-mng.selected-roles'
      }
    ],
    [
      EntityType.ALARM_RECORD,
      {
        type: 'device-monitor.alarm-record',
        noEntities: 'device-monitor.no-alarm-records'
      }
    ],
    [
      EntityType.ALARM_RULES,
      {
        type: 'device-monitor.alarm-rules',
        add: "device-monitor.add-alarm-rule",
        noEntities: 'device-monitor.no-alarm-rules',
        details: "device-monitor.alarm-rule-details",
        selectedEntities: "device-monitor.selected-alarm-rules"
      }
    ],
    [
      EntityType.DEVICE_HISTORY,
      {
        noEntities: 'device-monitor.no-device-history'
      }
    ],
    [
      EntityType.POTENCY,
      {
        noEntities: 'device.no-devices-text'
      }
    ],
    [
      EntityType.POTENCY_HISTORY,
      {
        noEntities: 'potency.no-device-capacity-history'
      }
    ],
    [
      EntityType.FACTORY_VERSION,
      {
        noEntities: 'system-mng.no-factory-version'
      }
    ],
    [
      EntityType.PROD_CAPACITY_SETTINGS,
      {
        noEntities: 'device.no-devices-text'
      }
    ],
    [
      EntityType.ORDER_FORM,
      {
        type: 'entity.type-order',
        typePlural: 'entity.type-orders',
        list: 'entity.list-of-orders',
        nameStartsWith: 'entity.order-name-starts-with',
        details: 'order.order-details',
        add: 'order.add-order',
        noEntities: 'order.no-orders-text',
        selectedEntities: 'order.selected-orders'
      }
    ],
    [
      EntityType.CHART_SETTINGS,
      {
        type: 'entity.type-device-dic',
        typePlural: 'entity.type-device-dics',
        list: 'entity.list-of-device-dics'
      }
    ],
    [
      EntityType.CHART,
      {
        type: 'entity.type-chart',
        typePlural: 'entity.type-charts',
        list: 'entity.list-of-charts',
        nameStartsWith: 'entity.chart-name-starts-with',
        details: 'device-mng.chart-details',
        add: 'device-mng.add-chart',
        noEntities: 'device-mng.no-charts-text',
        selectedEntities: 'device.selected-charts'
      }
    ],
    [
      EntityType.PROD_MNG,
      {
        noEntities: 'device.no-devices-text'
      }
    ],
    [
      EntityType.MNG_CALENDAR,
      {
        type: 'entity.type-prod-calendar',
        typePlural: 'entity.type-prod-calendars',
        list: 'entity.list-of-prod-calendars',
        nameStartsWith: 'entity.prod-calendar-name-starts-with',
        details: 'device-mng.prod-calendar-details',
        add: 'device-mng.add-prod-calendar',
        noEntities: 'device-mng.no-prod-calendars-text',
        selectedEntities: 'device-mng.selected-prod-calendars'
      }
    ],
    [
      EntityType.ORDER,
      {
        type: 'entity.type-order'
      }
    ],
    [
      EntityType.ORDER_PLAN,
      {
        type: 'entity.type-order-plan'
      }
    ],
    [
      EntityType.TENANT_MENU_ROLE,
      {
        type: 'entity.type-tenant-menu-role'
      }
    ],
    [
      EntityType.USER_MENU_ROLE,
      {
        type: 'entity.type-user-menu-role'
      }
    ],
    [
      EntityType.USER_CREDENTIALS,
      {
        type: 'entity.type-user-credentials'
      }
    ],
    [
      EntityType.PRODUCTION_CALENDAR,
      {
        type: 'entity.type-prod-calendar'
      }
    ]
  ]
);

export const entityTypeResources = new Map<EntityType, EntityTypeResource<BaseData<HasId>>>(
  [
    [
      EntityType.TENANT,
      {
        helpLinkId: 'tenants'
      }
    ],
    [
      EntityType.TENANT_PROFILE,
      {
        helpLinkId: 'tenantProfiles'
      }
    ],
    [
      EntityType.CUSTOMER,
      {
        helpLinkId: 'customers'
      }
    ],
    [
      EntityType.USER,
      {
        helpLinkId: 'users'
      }
    ],
    [
      EntityType.DEVICE,
      {
        helpLinkId: 'devices'
      }
    ],
    [
      EntityType.DEVICE_PROFILE,
      {
        helpLinkId: 'deviceProfiles'
      }
    ],
    [
      EntityType.ASSET,
      {
        helpLinkId: 'assets'
      }
    ],
    [
      EntityType.EDGE,
      {
        helpLinkId: 'edges'
      }
    ],
    [
      EntityType.ENTITY_VIEW,
      {
        helpLinkId: 'entityViews'
      }
    ],
    [
      EntityType.RULE_CHAIN,
      {
        helpLinkId: 'rulechains'
      }
    ],
    [
      EntityType.DASHBOARD,
      {
        helpLinkId: 'dashboards'
      }
    ],
    [
      EntityType.WIDGETS_BUNDLE,
      {
        helpLinkId: 'widgetsBundles'
      }
    ],
    [
      EntityType.TB_RESOURCE,
      {
        helpLinkId: 'resources'
      }
    ],
    [
      EntityType.OTA_PACKAGE,
      {
        helpLinkId: 'otaUpdates'
      }
    ],
    [
      EntityType.DATA_DICTIONARY,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.DEVICE_DICTIONARY,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.FACTORY,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.WORKSHOP,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.PRODUCTION_LINE,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.MENU,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.USER_MNG,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.ROLE_MNG,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.ALARM_RECORD,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.ALARM_RULES,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.DEVICE_HISTORY,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.POTENCY,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.POTENCY_HISTORY,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.FACTORY_VERSION,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.PROD_CAPACITY_SETTINGS,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.ORDER_FORM,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.CHART_SETTINGS,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.CHART,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.PROD_MNG,
      {
        helpLinkId: ''
      }
    ],
    [
      EntityType.MNG_CALENDAR,
      {
        helpLinkId: ''
      }
    ]
  ]
);

export interface EntitySubtype {
  tenantId: TenantId;
  entityType: EntityType;
  type: string;
}
