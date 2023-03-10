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

import { ContactBased } from '@shared/models/contact-based.model';
import { TenantId } from './id/tenant-id';
import { TenantProfileId } from '@shared/models/id/tenant-profile-id';
import { BaseData, CustomBaseData } from '@shared/models/base-data';
import { TreeNodeOptions } from '@app/core/services/utils.service';
import { MenuType } from './custom/menu-mng.models';

export enum TenantProfileType {
  DEFAULT = 'DEFAULT'
}

export interface DefaultTenantProfileConfiguration {
  maxDevices: number;
  maxAssets: number;
  maxCustomers: number;
  maxUsers: number;
  maxDashboards: number;
  maxRuleChains: number;
  maxResourcesInBytes: number;
  maxOtaPackagesInBytes: number;

  transportTenantMsgRateLimit?: string;
  transportTenantTelemetryMsgRateLimit?: string;
  transportTenantTelemetryDataPointsRateLimit?: string;
  transportDeviceMsgRateLimit?: string;
  transportDeviceTelemetryMsgRateLimit?: string;
  transportDeviceTelemetryDataPointsRateLimit?: string;

  maxTransportMessages: number;
  maxTransportDataPoints: number;
  maxREExecutions: number;
  maxJSExecutions: number;
  maxDPStorageDays: number;
  maxRuleNodeExecutionsPerMessage: number;
  maxEmails: number;
  maxSms: number;
  maxCreatedAlarms: number;

  defaultStorageTtlDays: number;
  alarmsTtlDays: number;
  rpcTtlDays: number;
}

export type TenantProfileConfigurations = DefaultTenantProfileConfiguration;

export interface TenantProfileConfiguration extends TenantProfileConfigurations {
  type: TenantProfileType;
}

export function createTenantProfileConfiguration(type: TenantProfileType): TenantProfileConfiguration {
  let configuration: TenantProfileConfiguration = null;
  if (type) {
    switch (type) {
      case TenantProfileType.DEFAULT:
        const defaultConfiguration: DefaultTenantProfileConfiguration = {
          maxDevices: 0,
          maxAssets: 0,
          maxCustomers: 0,
          maxUsers: 0,
          maxDashboards: 0,
          maxRuleChains: 0,
          maxResourcesInBytes: 0,
          maxOtaPackagesInBytes: 0,
          maxTransportMessages: 0,
          maxTransportDataPoints: 0,
          maxREExecutions: 0,
          maxJSExecutions: 0,
          maxDPStorageDays: 0,
          maxRuleNodeExecutionsPerMessage: 0,
          maxEmails: 0,
          maxSms: 0,
          maxCreatedAlarms: 0,
          defaultStorageTtlDays: 0,
          alarmsTtlDays: 0,
          rpcTtlDays: 0
        };
        configuration = {...defaultConfiguration, type: TenantProfileType.DEFAULT};
        break;
    }
  }
  return configuration;
}

export interface TenantProfileData {
  configuration: TenantProfileConfiguration;
}

export interface TenantProfile extends BaseData<TenantProfileId> {
  name: string;
  description?: string;
  default?: boolean;
  isolatedTbCore?: boolean;
  isolatedTbRuleEngine?: boolean;
  profileData?: TenantProfileData;
}

export interface Tenant extends ContactBased<TenantId> {
  title: string;
  region: string;
  tenantProfileId: TenantProfileId;
  additionalInfo?: any;
}

export interface TenantInfo extends Tenant {
  tenantProfileName: string;
}

export interface TenantMenu extends CustomBaseData, TreeNodeOptions {
  additionalInfo: string;
  level: number;
  menuType: MenuType;
  parentId: string;
  region: string;
  sort: number;
  sysMenuCode: string;
  sysMenuId: string;
  sysMenuName: string;
  tenantId: string;
  tenantMenuCode: string;
  tenantMenuName: string;
  tenantMenuIcon: string;
  tenantMenuImages: string;
  url: string;
  hasChildren: boolean;
  path: string;
  isButton: boolean;
  langKey: string;
}

export class TenantMenus extends Array<TenantMenu> {}

export interface SysMenu extends CustomBaseData, TreeNodeOptions {
  associatedTenant: boolean;
  checkAllFlag: boolean;
  code: string;
  level: number;
  menuIcon: string;
  menuImages: string;
  menuType: MenuType;
  name: string;
  parentId: string;
  region: string;
  sort: number;
  sysMenuId: string;
  url: string
}

export class SysMenus extends Array<SysMenu> {}
