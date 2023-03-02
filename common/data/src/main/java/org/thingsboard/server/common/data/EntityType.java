/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.common.data;

/**
 * @author Andrew Shvayka
 */
public enum EntityType {
    TENANT, CUSTOMER, DASHBOARD, ASSET, DEVICE, ALARM, RULE_CHAIN, RULE_NODE, ENTITY_VIEW, WIDGETS_BUNDLE,
    WIDGET_TYPE, TENANT_PROFILE, DEVICE_PROFILE, API_USAGE_STATE, TB_RESOURCE, OTA_PACKAGE, EDGE, RPC,

    // v1
    MENU,TENANT_MENU,FACTORY,WORKSHOP,PRODUCTION_LINE, USER_ROLE,

    // hs v2
    ORDER, ORDER_PLAN, TENANT_MENU_ROLE, USER, USER_MENU_ROLE, USER_CREDENTIALS, PRODUCTION_CALENDAR,

    // hs v3
    DICT_DATA, DICT_DEVICE, DICT_DEVICE_COMPONENT, DICT_DEVICE_COMPONENT_PROPERTY,
    DICT_DEVICE_GRAPH, DICT_DEVICE_GRAPH_ITEM, DICT_DEVICE_GROUP, DICT_DEVICE_GROUP_PROPERTY,
    DICT_DEVICE_PROPERTY, DICT_DEVICE_STANDARD_PROPERTY
}
