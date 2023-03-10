/**
 * Copyright © 2016-2021 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.MapUtils;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.cluster.TbClusterService;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.asset.AssetInfo;
import org.thingsboard.server.common.data.audit.ActionStatus;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.edge.EdgeEventType;
import org.thingsboard.server.common.data.edge.EdgeInfo;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.common.data.page.PageDataIterableByTenantIdEntityId;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.common.data.plugin.ComponentDescriptor;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.rpc.Rpc;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.common.data.rule.RuleChainType;
import org.thingsboard.server.common.data.rule.RuleNode;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.common.data.vo.enums.ErrorMessageEnums;
import org.thingsboard.server.common.data.vo.user.enums.UserLeveEnums;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.common.transport.service.DefaultTransportService;
import org.thingsboard.server.dao.asset.AssetService;
import org.thingsboard.server.dao.attributes.AttributesService;
import org.thingsboard.server.dao.audit.AuditLogService;
import org.thingsboard.server.dao.customer.CustomerService;
import org.thingsboard.server.dao.dashboard.DashboardService;
import org.thingsboard.server.dao.device.ClaimDevicesService;
import org.thingsboard.server.dao.device.DeviceCredentialsService;
import org.thingsboard.server.dao.device.DeviceProfileService;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.deviceoeeeveryhour.DeviceOeeEveryHourService;
import org.thingsboard.server.dao.edge.EdgeService;
import org.thingsboard.server.dao.entityview.EntityViewService;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
import org.thingsboard.server.dao.factory.FactoryService;
import org.thingsboard.server.dao.hs.service.DeviceDictPropertiesSvc;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.dao.menu.MenuService;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.sql.AuditLogEntity;
import org.thingsboard.server.dao.oauth2.OAuth2ConfigTemplateService;
import org.thingsboard.server.dao.oauth2.OAuth2Service;
import org.thingsboard.server.dao.ota.OtaPackageService;
import org.thingsboard.server.dao.productioncalender.ProductionCalenderService;
import org.thingsboard.server.dao.productionline.ProductionLineService;
import org.thingsboard.server.dao.relation.RelationService;
import org.thingsboard.server.dao.rpc.RpcService;
import org.thingsboard.server.dao.rule.RuleChainService;
import org.thingsboard.server.dao.settings.AdminSettingsService;
import org.thingsboard.server.dao.sql.audit.AuditLogRepository;
import org.thingsboard.server.dao.sql.role.dao.EffciencyAnalysisRepository;
import org.thingsboard.server.dao.sql.role.service.*;
import org.thingsboard.server.dao.sql.role.userrole.RoleMenuSvc;
import org.thingsboard.server.dao.sql.role.userrole.UserRoleMemuSvc;
import org.thingsboard.server.dao.sql.trendChart.service.EnergyChartService;
import org.thingsboard.server.dao.tenant.TbTenantProfileCache;
import org.thingsboard.server.dao.tenant.TenantProfileService;
import org.thingsboard.server.dao.tenant.TenantService;
import org.thingsboard.server.dao.tenantmenu.TenantMenuService;
import org.thingsboard.server.dao.tool.UserLanguageSvc;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.util.JsonUtils;
import org.thingsboard.server.dao.widget.WidgetTypeService;
import org.thingsboard.server.dao.widget.WidgetsBundleService;
import org.thingsboard.server.dao.workshop.WorkshopService;
import org.thingsboard.server.entity.menu.dto.AddMenuDto;
import org.thingsboard.server.entity.tenantmenu.dto.AddTenantMenuDto;
import org.thingsboard.server.excel.util.EasyExcelCustomCellWriteHandler;
import org.thingsboard.server.exception.ThingsboardErrorResponseHandler;
import org.thingsboard.server.queue.discovery.PartitionService;
import org.thingsboard.server.queue.provider.TbQueueProducerProvider;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.action.RuleEngineEntityActionService;
import org.thingsboard.server.service.component.ComponentDiscoveryService;
import org.thingsboard.server.service.edge.EdgeNotificationService;
import org.thingsboard.server.service.edge.rpc.EdgeRpcService;
import org.thingsboard.server.service.lwm2m.LwM2MServerSecurityInfoRepository;
import org.thingsboard.server.service.ota.OtaPackageStateService;
import org.thingsboard.server.service.profile.TbDeviceProfileCache;
import org.thingsboard.server.service.resource.TbResourceService;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.AccessControlService;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;
import org.thingsboard.server.service.state.DeviceStateService;
import org.thingsboard.server.service.telemetry.AlarmSubscriptionService;
import org.thingsboard.server.service.telemetry.TelemetrySubscriptionService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

import static org.thingsboard.server.dao.service.Validator.validateId;

//import org.thingsboard.server.service.userrole.RoleMenuSvc;

@Slf4j
@TbCoreComponent
public abstract class BaseController {

    protected static final Boolean IS_TEST = true; //本地自测方便使用

    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    protected static final String DEFAULT_DASHBOARD = "defaultDashboardId";
    protected static final String HOME_DASHBOARD = "homeDashboardId";

    protected static final String CREATE_TIME_COLUMN = "createdTime";

    private static final int DEFAULT_PAGE_SIZE = 1000;

    private static final ObjectMapper json = new ObjectMapper();

    @Autowired
    private ThingsboardErrorResponseHandler errorResponseHandler;

    @Autowired
    protected AccessControlService accessControlService;

    @Autowired
    protected TenantService tenantService;

    @Autowired
    protected TenantProfileService tenantProfileService;

    @Autowired
    protected CustomerService customerService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected DeviceService deviceService;

    @Autowired
    protected DeviceProfileService deviceProfileService;

    @Autowired
    protected AssetService assetService;

    @Autowired
    protected AlarmSubscriptionService alarmService;

    @Autowired
    protected DeviceCredentialsService deviceCredentialsService;

    @Autowired
    protected WidgetsBundleService widgetsBundleService;

    @Autowired
    protected WidgetTypeService widgetTypeService;

    @Autowired
    protected DashboardService dashboardService;

    @Autowired
    protected OAuth2Service oAuth2Service;

    @Autowired
    protected OAuth2ConfigTemplateService oAuth2ConfigTemplateService;

    @Autowired
    protected ComponentDiscoveryService componentDescriptorService;

    @Autowired
    protected RuleChainService ruleChainService;

    @Autowired
    protected TbClusterService tbClusterService;

    @Autowired
    protected RelationService relationService;

    @Autowired
    protected AuditLogService auditLogService;

    @Autowired
    protected DeviceStateService deviceStateService;

    @Autowired
    protected EntityViewService entityViewService;

    @Autowired
    protected TelemetrySubscriptionService tsSubService;

    @Autowired
    protected AttributesService attributesService;

    @Autowired
    protected ClaimDevicesService claimDevicesService;

    @Autowired
    protected PartitionService partitionService;

    @Autowired
    protected TbResourceService resourceService;

    @Autowired
    protected OtaPackageService otaPackageService;

    @Autowired
    protected OtaPackageStateService otaPackageStateService;

    @Autowired
    protected RpcService rpcService;

    @Autowired
    protected TbQueueProducerProvider producerProvider;

    @Autowired
    protected TbTenantProfileCache tenantProfileCache;

    @Autowired
    protected TbDeviceProfileCache deviceProfileCache;

    @Autowired
    protected LwM2MServerSecurityInfoRepository lwM2MServerSecurityInfoRepository;

    @Autowired(required = false)
    protected EdgeService edgeService;

    @Autowired(required = false)
    protected EdgeNotificationService edgeNotificationService;

    @Autowired(required = false)
    protected EdgeRpcService edgeGrpcService;

    @Autowired
    protected RuleEngineEntityActionService ruleEngineEntityActionService;

    @Autowired
    protected MenuService menuService;

    @Autowired
    protected TenantMenuService tenantMenuService;

    @Autowired
    protected FactoryService factoryService;

    @Autowired
    protected WorkshopService workshopService;

    @Autowired
    protected ProductionLineService productionLineService;

    @Autowired
    protected RoleMenuSvc roleMenuSvc;

    @Autowired
    protected DictDeviceService dictDeviceService;

    @Autowired
    protected DefaultTransportService transportService;

    @Autowired
    protected EnergyChartService energyChartService;

    @Autowired
    protected ProductionCalenderService productionCalenderService;

    @Autowired
    protected DeviceOeeEveryHourService deviceOeeEveryHourService;
    @Autowired
    protected AdminSettingsService adminSettingsService;

    @Autowired
    protected AuditLogRepository auditLogRepository;

/*

    @Autowired
    protected MqttClient mqttClient;
*/

    @Value("${server.log_controller_error_stack_trace}")
    @Getter
    private boolean logControllerErrorStackTrace;

    @Value("${edges.enabled}")
    @Getter
    protected boolean edgesEnabled;

    @Autowired
    protected CheckSvc checkSvc;
    @Autowired
    protected UserMenuRoleService userMenuRoleService;
    @Autowired
    protected TenantMenuRoleService tenantMenuRoleService;
    @Autowired
    protected EfficiencyStatisticsSvc efficiencyStatisticsSvc;
    @Autowired
    protected TenantSysRoleService tenantSysRoleService;
    @Autowired
    protected UserRoleMemuSvc userRoleMemuSvc;
    @Autowired
    protected UserLanguageSvc userLanguageSvc;
    @Autowired
    protected DeviceDictPropertiesSvc deviceDictPropertiesSvc;
    @Autowired
    protected UserRoleMenuSvc userRoleSvc;
    @Autowired
    protected EffciencyAnalysisRepository effciencyAnalysisRepository;


    @ExceptionHandler(ThingsboardException.class)
    public void handleThingsboardException(ThingsboardException ex, HttpServletResponse response) {
        errorResponseHandler.handle(ex, response);
    }

    ThingsboardException handleException(Exception exception) {
        return handleException(exception, true);
    }

    private ThingsboardException handleException(Exception exception, boolean logException) {
        if (logException && logControllerErrorStackTrace) {
            log.error("Error [{}]", exception.getMessage(), exception);
        }

        String cause = "";
        if (exception.getCause() != null) {
            cause = exception.getCause().getClass().getCanonicalName();
        }

        if (exception instanceof ThingsboardException) {
            return (ThingsboardException) exception;
        } else if (exception instanceof IllegalArgumentException || exception instanceof IncorrectParameterException
                || exception instanceof DataValidationException || cause.contains("IncorrectParameterException")) {
            return new ThingsboardException(exception.getMessage(), ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        } else if (exception instanceof MessagingException) {
            return new ThingsboardException("Unable to send mail: " + exception.getMessage(), ThingsboardErrorCode.GENERAL);
        } else {
            return new ThingsboardException(exception.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }

    <T> T checkNotNull(T reference) throws ThingsboardException {
        if (reference == null) {
            throw new ThingsboardException("Requested item wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        return reference;
    }

    <T> T checkNotNull(Optional<T> reference) throws ThingsboardException {
        if (reference.isPresent()) {
            return reference.get();
        } else {
            throw new ThingsboardException("Requested item wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
    }

    void checkParameter(String name, String param) throws ThingsboardException {
        if (StringUtils.isEmpty(param)) {
            throw new ThingsboardException("Parameter '" + name + "' can't be empty!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
    }

    void checkParameter(String name, Object param) throws ThingsboardException {
        if (param == null || StringUtils.isBlank(param.toString())) {
            throw new ThingsboardException("Parameter '" + name + "' can't be empty!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);

        }
    }

    void checkParameterChinees(String name, String param) throws ThingsboardException {
        if (StringUtils.isEmpty(param)) {
            throw new ThingsboardException("参数 '" + name + "' 不能为空!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
    }

    void checkParameterChinees(String name, Object param) throws ThingsboardException {
        if (param == null || StringUtils.isBlank(param.toString())) {
            throw new ThingsboardException("参数 '" + name + "' 不能为空!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);

        }
    }

    void checkArrayParameter(String name, String[] params) throws ThingsboardException {
        if (params == null || params.length == 0) {
            throw new ThingsboardException("Parameter '" + name + "' can't be empty!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        } else {
            for (String param : params) {
                checkParameter(name, param);
            }
        }
    }

    UUID toUUID(String id) {
        return UUID.fromString(id);
    }

    public PageLink createPageLink(int pageSize, int page, String textSearch, String sortProperty, String sortOrder) throws ThingsboardException {
        if (!StringUtils.isEmpty(sortProperty)) {
            SortOrder.Direction direction = SortOrder.Direction.ASC;
            if (!StringUtils.isEmpty(sortOrder)) {
                try {
                    direction = SortOrder.Direction.valueOf(sortOrder.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new ThingsboardException("Unsupported sort order '" + sortOrder + "'! Only 'ASC' or 'DESC' types are allowed.", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
                }
            }
            SortOrder sort = new SortOrder(sortProperty, direction);
            return new PageLink(pageSize, page, textSearch, sort);
        } else {
            return new PageLink(pageSize, page, textSearch);
        }
    }

    TimePageLink createTimePageLink(int pageSize, int page, String textSearch,
                                    String sortProperty, String sortOrder, Long startTime, Long endTime) throws ThingsboardException {
        PageLink pageLink = this.createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return new TimePageLink(pageLink, startTime, endTime);
    }

    protected SecurityUser getCurrentUser() throws ThingsboardException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            return (SecurityUser) authentication.getPrincipal();
        } else {
            throw new ThingsboardException("You aren't authorized to perform this operation!", ThingsboardErrorCode.AUTHENTICATION);
        }
    }

    Tenant checkTenantId(TenantId tenantId, Operation operation) throws ThingsboardException {
        try {
            validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
            Tenant tenant = tenantService.findTenantById(tenantId);
            checkNotNull(tenant);
            accessControlService.checkPermission(getCurrentUser(), Resource.TENANT, operation, tenantId, tenant);
            return tenant;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    TenantInfo checkTenantInfoId(TenantId tenantId, Operation operation) throws ThingsboardException {
        try {
            validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
            TenantInfo tenant = tenantService.findTenantInfoById(tenantId);
            checkNotNull(tenant);
            accessControlService.checkPermission(getCurrentUser(), Resource.TENANT, operation, tenantId, tenant);
            return tenant;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    TenantProfile checkTenantProfileId(TenantProfileId tenantProfileId, Operation operation) throws ThingsboardException {
        try {
            validateId(tenantProfileId, "Incorrect tenantProfileId " + tenantProfileId);
            TenantProfile tenantProfile = tenantProfileService.findTenantProfileById(getTenantId(), tenantProfileId);
            checkNotNull(tenantProfile);
            accessControlService.checkPermission(getCurrentUser(), Resource.TENANT_PROFILE, operation);
            return tenantProfile;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected TenantId getTenantId() throws ThingsboardException {
        return getCurrentUser().getTenantId();
    }

    Customer checkCustomerId(CustomerId customerId, Operation operation) throws ThingsboardException {
        try {
            validateId(customerId, "Incorrect customerId " + customerId);
            Customer customer = customerService.findCustomerById(getTenantId(), customerId);
            checkNotNull(customer);
            accessControlService.checkPermission(getCurrentUser(), Resource.CUSTOMER, operation, customerId, customer);
            return customer;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    User checkUserId(UserId userId, Operation operation) throws ThingsboardException {
        try {
            validateId(userId, "Incorrect userId " + userId);
            User user = userService.findUserById(getCurrentUser().getTenantId(), userId);
            checkNotNull(user);
//            accessControlService.checkPermission(getCurrentUser(), Resource.USER, operation, userId, user);
            return user;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    Menu checkAddMenuList(AddMenuDto addMenuDto) throws ThingsboardException {
        if (addMenuDto == null) {
            throw new ThingsboardException("Requested item wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        return checkMenu(addMenuDto.toMenu());
    }

    Menu checkMenu(Menu menu) throws ThingsboardException {
        checkNotNull(menu);
        checkParameter("level", menu.getLevel());
        checkParameter("menuType", menu.getMenuType());
        return menu;
    }

    void checkTenantMenuList(List<TenantMenu> tenantMenu) throws ThingsboardException {
        if (CollectionUtils.isEmpty(tenantMenu)) {
            throw new ThingsboardException("Requested item wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        tenantMenu.forEach(i -> {
            try {
                checkTenantMenu(i);
                if (i.getId() != null && i.getId() != null &&
                        tenantMenuService.findById(i.getId()) != null) {
                    throw new ThingsboardException("菜单已存在请勿重复添加！", ThingsboardErrorCode.ITEM_NOT_FOUND);
                }
            } catch (ThingsboardException e) {
                e.printStackTrace();
            }
        });
    }

    List<TenantMenu> checkAddTenantMenuList(List<AddTenantMenuDto> addTenantMenuDtos) throws ThingsboardException {
        if (CollectionUtils.isEmpty(addTenantMenuDtos)) {
            throw new ThingsboardException("Requested item wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        List<TenantMenu> tenantMenu = new ArrayList<>();
        addTenantMenuDtos.forEach(i -> {
            try {
                checkTenantMenu(i);
                TenantMenu tenantMenu1 = i.toTenantMenu();
                tenantMenu1.setCreatedUser(getCurrentUser().getUuidId());
                tenantMenu.add(tenantMenu1);
            } catch (ThingsboardException e) {
                e.printStackTrace();
            }
        });
        return tenantMenu;
    }

    void checkTenantMenu(TenantMenu tenantMenu) throws ThingsboardException {
        try {
            checkNotNull(tenantMenu);
            checkParameter("tenant", tenantMenu.getTenantId());
            checkParameter("sysMenuId", tenantMenu.getSysMenuId());
            checkParameter("sysMenuCode", tenantMenu.getSysMenuCode());
            checkParameter("sysMenuCode", tenantMenu.getSysMenuCode());
            checkParameter("tenantMenuName", tenantMenu.getTenantMenuName());
            checkParameter("level", tenantMenu.getLevel());
            checkParameter("menuType", tenantMenu.getMenuType());
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    void checkTenantMenu(AddTenantMenuDto tenantMenu) throws ThingsboardException {
        try {
            checkNotNull(tenantMenu);
            checkParameter("tenant", tenantMenu.getTenantId());
            checkParameter("sysMenuId", tenantMenu.getSysMenuId());
            checkParameter("sysMenuCode", tenantMenu.getSysMenuCode());
            checkParameter("sysMenuCode", tenantMenu.getSysMenuCode());
            checkParameter("tenantMenuName", tenantMenu.getTenantMenuName());
            checkParameter("level", tenantMenu.getLevel());
            checkParameter("menuType", tenantMenu.getMenuType());
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected <I extends EntityId, T extends HasTenantId> void checkEntity(I entityId, T entity, Resource resource) throws ThingsboardException {
        if (entityId == null) {
            accessControlService
                    .checkPermission(getCurrentUser(), resource, Operation.CREATE, null, entity);
        } else {
            checkEntityId(entityId, Operation.WRITE);
        }
    }

    protected void checkEntityId(EntityId entityId, Operation operation) throws ThingsboardException {
        try {
            checkNotNull(entityId);
            validateId(entityId.getId(), "Incorrect entityId " + entityId);
            switch (entityId.getEntityType()) {
                case ALARM:
                    checkAlarmId(new AlarmId(entityId.getId()), operation);
                    return;
                case DEVICE:
                    checkDeviceId(new DeviceId(entityId.getId()), operation);
                    return;
                case DEVICE_PROFILE:
                    checkDeviceProfileId(new DeviceProfileId(entityId.getId()), operation);
                    return;
                case CUSTOMER:
                    checkCustomerId(new CustomerId(entityId.getId()), operation);
                    return;
                case TENANT:
                    checkTenantId(new TenantId(entityId.getId()), operation);
                    return;
                case TENANT_PROFILE:
                    checkTenantProfileId(new TenantProfileId(entityId.getId()), operation);
                    return;
                case RULE_CHAIN:
                    checkRuleChain(new RuleChainId(entityId.getId()), operation);
                    return;
                case RULE_NODE:
                    checkRuleNode(new RuleNodeId(entityId.getId()), operation);
                    return;
                case ASSET:
                    checkAssetId(new AssetId(entityId.getId()), operation);
                    return;
                case DASHBOARD:
                    checkDashboardId(new DashboardId(entityId.getId()), operation);
                    return;
                case USER:
                    checkUserId(new UserId(entityId.getId()), operation);
                    return;
                case ENTITY_VIEW:
                    checkEntityViewId(new EntityViewId(entityId.getId()), operation);
                    return;
                case EDGE:
                    checkEdgeId(new EdgeId(entityId.getId()), operation);
                    return;
                case WIDGETS_BUNDLE:
                    checkWidgetsBundleId(new WidgetsBundleId(entityId.getId()), operation);
                    return;
                case WIDGET_TYPE:
                    checkWidgetTypeId(new WidgetTypeId(entityId.getId()), operation);
                    return;
                case TB_RESOURCE:
                    checkResourceId(new TbResourceId(entityId.getId()), operation);
                    return;
                case OTA_PACKAGE:
                    checkOtaPackageId(new OtaPackageId(entityId.getId()), operation);
                    return;
                case FACTORY:  //忽略权限校验
                    return;
                case WORKSHOP://忽略权限校验
                    return;
                case PRODUCTION_LINE://忽略权限校验
                    return;
                default:
                    throw new IllegalArgumentException("Unsupported entity type: " + entityId.getEntityType());
            }
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    Device checkDeviceId(DeviceId deviceId, Operation operation) throws ThingsboardException {
        try {
            validateId(deviceId, "Incorrect deviceId " + deviceId);
            Device device = deviceService.findDeviceById(getCurrentUser().getTenantId(), deviceId);
            checkNotNull(device);
            accessControlService.checkPermission(getCurrentUser(), Resource.DEVICE, operation, deviceId, device);
            return device;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    DeviceInfo checkDeviceInfoId(DeviceId deviceId, Operation operation) throws ThingsboardException {
        try {
            validateId(deviceId, "Incorrect deviceId " + deviceId);
            DeviceInfo device = deviceService.findDeviceInfoById(getCurrentUser().getTenantId(), deviceId);
            checkNotNull(device);
            accessControlService.checkPermission(getCurrentUser(), Resource.DEVICE, operation, deviceId, device);
            return device;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    DeviceProfile checkDeviceProfileId(DeviceProfileId deviceProfileId, Operation operation) throws ThingsboardException {
        try {
            validateId(deviceProfileId, "Incorrect deviceProfileId " + deviceProfileId);
            DeviceProfile deviceProfile = deviceProfileService.findDeviceProfileById(getCurrentUser().getTenantId(), deviceProfileId);
            checkNotNull(deviceProfile);
            accessControlService.checkPermission(getCurrentUser(), Resource.DEVICE_PROFILE, operation, deviceProfileId, deviceProfile);
            return deviceProfile;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected EntityView checkEntityViewId(EntityViewId entityViewId, Operation operation) throws ThingsboardException {
        try {
            validateId(entityViewId, "Incorrect entityViewId " + entityViewId);
            EntityView entityView = entityViewService.findEntityViewById(getCurrentUser().getTenantId(), entityViewId);
            checkNotNull(entityView);
            accessControlService.checkPermission(getCurrentUser(), Resource.ENTITY_VIEW, operation, entityViewId, entityView);
            return entityView;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    EntityViewInfo checkEntityViewInfoId(EntityViewId entityViewId, Operation operation) throws ThingsboardException {
        try {
            validateId(entityViewId, "Incorrect entityViewId " + entityViewId);
            EntityViewInfo entityView = entityViewService.findEntityViewInfoById(getCurrentUser().getTenantId(), entityViewId);
            checkNotNull(entityView);
            accessControlService.checkPermission(getCurrentUser(), Resource.ENTITY_VIEW, operation, entityViewId, entityView);
            return entityView;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    Asset checkAssetId(AssetId assetId, Operation operation) throws ThingsboardException {
        try {
            validateId(assetId, "Incorrect assetId " + assetId);
            Asset asset = assetService.findAssetById(getCurrentUser().getTenantId(), assetId);
            checkNotNull(asset);
            accessControlService.checkPermission(getCurrentUser(), Resource.ASSET, operation, assetId, asset);
            return asset;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    AssetInfo checkAssetInfoId(AssetId assetId, Operation operation) throws ThingsboardException {
        try {
            validateId(assetId, "Incorrect assetId " + assetId);
            AssetInfo asset = assetService.findAssetInfoById(getCurrentUser().getTenantId(), assetId);
            checkNotNull(asset);
            accessControlService.checkPermission(getCurrentUser(), Resource.ASSET, operation, assetId, asset);
            return asset;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    Alarm checkAlarmId(AlarmId alarmId, Operation operation) throws ThingsboardException {
        try {
            validateId(alarmId, "Incorrect alarmId " + alarmId);
            Alarm alarm = alarmService.findAlarmByIdAsync(getCurrentUser().getTenantId(), alarmId).get();
            checkNotNull(alarm);
            accessControlService.checkPermission(getCurrentUser(), Resource.ALARM, operation, alarmId, alarm);
            return alarm;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    AlarmInfo checkAlarmInfoId(AlarmId alarmId, Operation operation) throws ThingsboardException {
        try {
            validateId(alarmId, "Incorrect alarmId " + alarmId);
            AlarmInfo alarmInfo = alarmService.findAlarmInfoByIdAsync(getCurrentUser().getTenantId(), alarmId).get();
            checkNotNull(alarmInfo);
            accessControlService.checkPermission(getCurrentUser(), Resource.ALARM, operation, alarmId, alarmInfo);
            return alarmInfo;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    WidgetsBundle checkWidgetsBundleId(WidgetsBundleId widgetsBundleId, Operation operation) throws ThingsboardException {
        try {
            validateId(widgetsBundleId, "Incorrect widgetsBundleId " + widgetsBundleId);
            WidgetsBundle widgetsBundle = widgetsBundleService.findWidgetsBundleById(getCurrentUser().getTenantId(), widgetsBundleId);
            checkNotNull(widgetsBundle);
            accessControlService.checkPermission(getCurrentUser(), Resource.WIDGETS_BUNDLE, operation, widgetsBundleId, widgetsBundle);
            return widgetsBundle;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    WidgetTypeDetails checkWidgetTypeId(WidgetTypeId widgetTypeId, Operation operation) throws ThingsboardException {
        try {
            validateId(widgetTypeId, "Incorrect widgetTypeId " + widgetTypeId);
            WidgetTypeDetails widgetTypeDetails = widgetTypeService.findWidgetTypeDetailsById(getCurrentUser().getTenantId(), widgetTypeId);
            checkNotNull(widgetTypeDetails);
            accessControlService.checkPermission(getCurrentUser(), Resource.WIDGET_TYPE, operation, widgetTypeId, widgetTypeDetails);
            return widgetTypeDetails;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    Dashboard checkDashboardId(DashboardId dashboardId, Operation operation) throws ThingsboardException {
        try {
            validateId(dashboardId, "Incorrect dashboardId " + dashboardId);
            Dashboard dashboard = dashboardService.findDashboardById(getCurrentUser().getTenantId(), dashboardId);
            checkNotNull(dashboard);
            accessControlService.checkPermission(getCurrentUser(), Resource.DASHBOARD, operation, dashboardId, dashboard);
            return dashboard;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    Edge checkEdgeId(EdgeId edgeId, Operation operation) throws ThingsboardException {
        try {
            validateId(edgeId, "Incorrect edgeId " + edgeId);
            Edge edge = edgeService.findEdgeById(getTenantId(), edgeId);
            checkNotNull(edge);
            accessControlService.checkPermission(getCurrentUser(), Resource.EDGE, operation, edgeId, edge);
            return edge;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    EdgeInfo checkEdgeInfoId(EdgeId edgeId, Operation operation) throws ThingsboardException {
        try {
            validateId(edgeId, "Incorrect edgeId " + edgeId);
            EdgeInfo edge = edgeService.findEdgeInfoById(getCurrentUser().getTenantId(), edgeId);
            checkNotNull(edge);
            accessControlService.checkPermission(getCurrentUser(), Resource.EDGE, operation, edgeId, edge);
            return edge;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    DashboardInfo checkDashboardInfoId(DashboardId dashboardId, Operation operation) throws ThingsboardException {
        try {
            validateId(dashboardId, "Incorrect dashboardId " + dashboardId);
            DashboardInfo dashboardInfo = dashboardService.findDashboardInfoById(getCurrentUser().getTenantId(), dashboardId);
            checkNotNull(dashboardInfo);
            accessControlService.checkPermission(getCurrentUser(), Resource.DASHBOARD, operation, dashboardId, dashboardInfo);
            return dashboardInfo;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    ComponentDescriptor checkComponentDescriptorByClazz(String clazz) throws ThingsboardException {
        try {
            log.debug("[{}] Lookup component descriptor", clazz);
            return checkNotNull(componentDescriptorService.getComponent(clazz));
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    List<ComponentDescriptor> checkComponentDescriptorsByType(ComponentType type, RuleChainType ruleChainType) throws ThingsboardException {
        try {
            log.debug("[{}] Lookup component descriptors", type);
            return componentDescriptorService.getComponents(type, ruleChainType);
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    List<ComponentDescriptor> checkComponentDescriptorsByTypes(Set<ComponentType> types, RuleChainType ruleChainType) throws ThingsboardException {
        try {
            log.debug("[{}] Lookup component descriptors", types);
            return componentDescriptorService.getComponents(types, ruleChainType);
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected RuleChain checkRuleChain(RuleChainId ruleChainId, Operation operation) throws ThingsboardException {
        validateId(ruleChainId, "Incorrect ruleChainId " + ruleChainId);
        RuleChain ruleChain = ruleChainService.findRuleChainById(getCurrentUser().getTenantId(), ruleChainId);
        checkNotNull(ruleChain);
        accessControlService.checkPermission(getCurrentUser(), Resource.RULE_CHAIN, operation, ruleChainId, ruleChain);
        return ruleChain;
    }

    protected RuleNode checkRuleNode(RuleNodeId ruleNodeId, Operation operation) throws ThingsboardException {
        validateId(ruleNodeId, "Incorrect ruleNodeId " + ruleNodeId);
        RuleNode ruleNode = ruleChainService.findRuleNodeById(getTenantId(), ruleNodeId);
        checkNotNull(ruleNode);
        checkRuleChain(ruleNode.getRuleChainId(), operation);
        return ruleNode;
    }

    TbResource checkResourceId(TbResourceId resourceId, Operation operation) throws ThingsboardException {
        try {
            validateId(resourceId, "Incorrect resourceId " + resourceId);
            TbResource resource = resourceService.findResourceById(getCurrentUser().getTenantId(), resourceId);
            checkNotNull(resource);
            accessControlService.checkPermission(getCurrentUser(), Resource.TB_RESOURCE, operation, resourceId, resource);
            return resource;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    TbResourceInfo checkResourceInfoId(TbResourceId resourceId, Operation operation) throws ThingsboardException {
        try {
            validateId(resourceId, "Incorrect resourceId " + resourceId);
            TbResourceInfo resourceInfo = resourceService.findResourceInfoById(getCurrentUser().getTenantId(), resourceId);
            checkNotNull(resourceInfo);
            accessControlService.checkPermission(getCurrentUser(), Resource.TB_RESOURCE, operation, resourceId, resourceInfo);
            return resourceInfo;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    OtaPackage checkOtaPackageId(OtaPackageId otaPackageId, Operation operation) throws ThingsboardException {
        try {
            validateId(otaPackageId, "Incorrect otaPackageId " + otaPackageId);
            OtaPackage otaPackage = otaPackageService.findOtaPackageById(getCurrentUser().getTenantId(), otaPackageId);
            checkNotNull(otaPackage);
            accessControlService.checkPermission(getCurrentUser(), Resource.OTA_PACKAGE, operation, otaPackageId, otaPackage);
            return otaPackage;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    OtaPackageInfo checkOtaPackageInfoId(OtaPackageId otaPackageId, Operation operation) throws ThingsboardException {
        try {
            validateId(otaPackageId, "Incorrect otaPackageId " + otaPackageId);
            OtaPackageInfo otaPackageIn = otaPackageService.findOtaPackageInfoById(getCurrentUser().getTenantId(), otaPackageId);
            checkNotNull(otaPackageIn);
            accessControlService.checkPermission(getCurrentUser(), Resource.OTA_PACKAGE, operation, otaPackageId, otaPackageIn);
            return otaPackageIn;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    Rpc checkRpcId(RpcId rpcId, Operation operation) throws ThingsboardException {
        try {
            validateId(rpcId, "Incorrect rpcId " + rpcId);
            Rpc rpc = rpcService.findById(getCurrentUser().getTenantId(), rpcId);
            checkNotNull(rpc);
            accessControlService.checkPermission(getCurrentUser(), Resource.RPC, operation, rpcId, rpc);
            return rpc;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    @SuppressWarnings("unchecked")
    protected <I extends EntityId> I emptyId(EntityType entityType) {
        return (I) EntityIdFactory.getByTypeAndUuid(entityType, ModelConstants.NULL_UUID);
    }

    protected <E extends HasName, I extends EntityId> void logEntityAction(I entityId, E entity, CustomerId customerId,
                                                                           ActionType actionType, Exception e, Object... additionalInfo) throws ThingsboardException {
        logEntityAction(getCurrentUser(), entityId, entity, customerId, actionType, e, additionalInfo);
    }

    protected <E extends HasName, I extends EntityId> void logEntityAction(User user, I entityId, E entity, CustomerId customerId,
                                                                           ActionType actionType, Exception e, Object... additionalInfo) throws ThingsboardException {
        if (customerId == null || customerId.isNullUid()) {
            customerId = user.getCustomerId();
        }
        if (e == null) {
            ruleEngineEntityActionService.pushEntityActionToRuleEngine(entityId, entity, user.getTenantId(), customerId, actionType, user, additionalInfo);
        }
        auditLogService.logEntityAction(user.getTenantId(), customerId, user.getId(), user.getName(), entityId, entity, actionType, e, additionalInfo);
    }


    public static Exception toException(Throwable error) {
        return error != null ? (Exception.class.isInstance(error) ? (Exception) error : new Exception(error)) : null;
    }

    protected <E extends HasName> String entityToStr(E entity) {
        try {
            return json.writeValueAsString(json.valueToTree(entity));
        } catch (JsonProcessingException e) {
            log.warn("[{}] Failed to convert entity to string!", entity, e);
        }
        return null;
    }

    protected void sendRelationNotificationMsg(TenantId tenantId, EntityRelation relation, EdgeEventActionType action) {
        try {
            if (!relation.getFrom().getEntityType().equals(EntityType.EDGE) &&
                    !relation.getTo().getEntityType().equals(EntityType.EDGE)) {
                sendNotificationMsgToEdgeService(tenantId, null, null, json.writeValueAsString(relation), EdgeEventType.RELATION, action);
            }
        } catch (Exception e) {
            log.warn("Failed to push relation to core: {}", relation, e);
        }
    }

    protected void sendDeleteNotificationMsg(TenantId tenantId, EntityId entityId, List<EdgeId> edgeIds) {
        sendDeleteNotificationMsg(tenantId, entityId, edgeIds, null);
    }

    protected void sendDeleteNotificationMsg(TenantId tenantId, EntityId entityId, List<EdgeId> edgeIds, String body) {
        if (edgeIds != null && !edgeIds.isEmpty()) {
            for (EdgeId edgeId : edgeIds) {
                sendNotificationMsgToEdgeService(tenantId, edgeId, entityId, body, null, EdgeEventActionType.DELETED);
            }
        }
    }

    protected void sendAlarmDeleteNotificationMsg(TenantId tenantId, EntityId entityId, List<EdgeId> edgeIds, Alarm alarm) {
        try {
            sendDeleteNotificationMsg(tenantId, entityId, edgeIds, json.writeValueAsString(alarm));
        } catch (Exception e) {
            log.warn("Failed to push delete alarm msg to core: {}", alarm, e);
        }
    }

    protected void sendEntityAssignToCustomerNotificationMsg(TenantId tenantId, EntityId entityId, CustomerId customerId, EdgeEventActionType action) {
        try {
            sendNotificationMsgToEdgeService(tenantId, null, entityId, json.writeValueAsString(customerId), null, action);
        } catch (Exception e) {
            log.warn("Failed to push assign/unassign to/from customer to core: {}", customerId, e);
        }
    }

    protected void sendEntityNotificationMsg(TenantId tenantId, EntityId entityId, EdgeEventActionType action) {
        sendNotificationMsgToEdgeService(tenantId, null, entityId, null, null, action);
    }

    protected void sendEntityAssignToEdgeNotificationMsg(TenantId tenantId, EdgeId edgeId, EntityId entityId, EdgeEventActionType action) {
        sendNotificationMsgToEdgeService(tenantId, edgeId, entityId, null, null, action);
    }

    private void sendNotificationMsgToEdgeService(TenantId tenantId, EdgeId edgeId, EntityId entityId, String body, EdgeEventType type, EdgeEventActionType action) {
        tbClusterService.sendNotificationMsgToEdgeService(tenantId, edgeId, entityId, body, type, action);
    }

    protected List<EdgeId> findRelatedEdgeIds(TenantId tenantId, EntityId entityId) {
        if (!edgesEnabled) {
            return null;
        }
        if (EntityType.EDGE.equals(entityId.getEntityType())) {
            return Collections.singletonList(new EdgeId(entityId.getId()));
        }
        PageDataIterableByTenantIdEntityId<EdgeId> relatedEdgeIdsIterator =
                new PageDataIterableByTenantIdEntityId<>(edgeService::findRelatedEdgeIdsByEntityId, tenantId, entityId, DEFAULT_PAGE_SIZE);
        List<EdgeId> result = new ArrayList<>();
        for (EdgeId edgeId : relatedEdgeIdsIterator) {
            result.add(edgeId);
        }
        return result;
    }

    protected void processDashboardIdFromAdditionalInfo(ObjectNode additionalInfo, String requiredFields) throws ThingsboardException {
        String dashboardId = additionalInfo.has(requiredFields) ? additionalInfo.get(requiredFields).asText() : null;
        if (dashboardId != null && !dashboardId.equals("null")) {
            if (dashboardService.findDashboardById(getTenantId(), new DashboardId(UUID.fromString(dashboardId))) == null) {
                additionalInfo.remove(requiredFields);
            }
        }
    }

    protected MediaType parseMediaType(String contentType) {
        try {
            return MediaType.parseMediaType(contentType);
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    /**
     * 获得当前登录用户的语言环境，默认zh_cn
     */
    public String getUserLanguage() {
        try {
            return Optional.ofNullable(getCurrentUser().getAdditionalInfo()).map(v -> v.get("lang")).map(JsonNode::asText).orElse("zh_cn");
        } catch (Exception ignore) {
        }
        return "zh_cn";
    }

    /**
     * 校验同层级下系统菜单/按钮名称是否重复
     *
     * @return
     */
    public void checkSameLevelNameRepetition(AddMenuDto addMenuDto) throws ThingsboardException {
        Boolean sameLevelNameRepetition = menuService.findSameLevelNameRepetition(addMenuDto.getId(), addMenuDto.getParentId(), addMenuDto.getName());
        if (sameLevelNameRepetition) {
            log.warn("名称重复");
            throw new ThingsboardException("名称重复", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
    }

    /**
     * 校验工厂名称是否重复
     *
     * @param name
     */
    public void checkFactoryName(UUID id, String name) throws ThingsboardException {
        List<Factory> byName = factoryService.findByName(name, this.getCurrentUser().getTenantId().getId());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(byName)) {
            if (id == null) {
                log.warn("工厂名称重复");
                throw new ThingsboardException("工厂名称重复", ThingsboardErrorCode.FAIL_VIOLATION);
            } else {
                if (!byName.get(0).getId().toString().equals(id.toString())) {
                    log.warn("工厂名称重复");
                    throw new ThingsboardException("工厂名称重复", ThingsboardErrorCode.FAIL_VIOLATION);
                }
            }
        }
    }


    /**
     * 获取当前登录人的提示信息
     *
     * @param enums
     * @return
     * @throws ThingsboardException
     */
    public String getMessageByUserId(ErrorMessageEnums enums) throws ThingsboardException {
        String message = userLanguageSvc.getLanguageByUserLang(enums, getCurrentUser().getTenantId(), new UserId(getCurrentUser().getUuidId()));
        return message;
    }

    /***
     * 设置参数
     * @param queryParam
     * @throws ThingsboardException
     */
    public void setParametersByUserLevel(Map<String, Object> queryParam) throws ThingsboardException {
        SecurityUser securityUser = getCurrentUser();
        if (securityUser.getUserLevel() == UserLeveEnums.TENANT_ADMIN.getCode()
                || securityUser.getUserLevel() == UserLeveEnums.USER_SYSTEM_ADMIN.getCode()
                || securityUser.getUserLevel() == UserLeveEnums.DEFAULT_VALUE.getCode()
        ) {   //租户管理员：
            List<Integer> userLeve = new ArrayList<>();
            userLeve.add(UserLeveEnums.DEFAULT_VALUE.getCode());//普通用户
            userLeve.add(UserLeveEnums.USER_SYSTEM_ADMIN.getCode());//用户系统管理员
            queryParam.put("userLevelIn", userLeve);
        }
    }


    /***
     * 设置参数 普通用户看不到
     *   租户管理员创建的角色
     * @param queryParam
     * @throws ThingsboardException
     */
    public void setParametersByRoleLevel(Map<String, Object> queryParam) throws ThingsboardException {
        SecurityUser securityUser = getCurrentUser();
        if (securityUser.getUserLevel() == UserLeveEnums.DEFAULT_VALUE.getCode()) {   //租户管理员：
            List<Integer> userLeve = new ArrayList<>();
            userLeve.add(UserLeveEnums.DEFAULT_VALUE.getCode());//普通用户
            userLeve.add(UserLeveEnums.USER_SYSTEM_ADMIN.getCode());//用户系统管理员
            queryParam.put("userLevelList", userLeve);
        }
    }

    /***
     * 设置参数 普通用户看不到
     *   租户管理员创建的角色
     * @throws ThingsboardException
     */
    public List<Integer> setParametersByRoleLevel() throws ThingsboardException {
        SecurityUser securityUser = getCurrentUser();
        if (securityUser.getUserLevel() == UserLeveEnums.DEFAULT_VALUE.getCode()) {   //租户管理员：
            List<Integer> userLeve = new ArrayList<>();
            userLeve.add(UserLeveEnums.DEFAULT_VALUE.getCode());//普通用户
            userLeve.add(UserLeveEnums.USER_SYSTEM_ADMIN.getCode());//用户系统管理员
            return userLeve;
        }
        return null;
    }


    public void saveAuditLog(SecurityUser securityUser, UUID entityId, EntityType entityType, String entityName,
                             ActionType actionType, Object actionData) {
        try {
            AuditLogEntity auditLogEntity = new AuditLogEntity();
            auditLogEntity.setId(Uuids.timeBased());
            auditLogEntity.setTenantId(securityUser.getTenantId().getId());
            auditLogEntity.setCustomerId(CustomerId.NULL_UUID);
            auditLogEntity.setEntityId(entityId == null ? EntityId.NULL_UUID : entityId);
            auditLogEntity.setEntityType(entityType);
            auditLogEntity.setEntityName(entityName);
            auditLogEntity.setUserId(securityUser.getId().getId());
            auditLogEntity.setUserName(securityUser.getUserName());
            auditLogEntity.setActionType(actionType);
            auditLogEntity.setActionData(actionData != null ? JacksonUtil.toJsonNode(new Gson().toJson(actionData)) : null);
            auditLogEntity.setActionFailureDetails(null);
            auditLogEntity.setActionStatus(ActionStatus.SUCCESS);
            auditLogEntity.setCreatedTime(System.currentTimeMillis());
            this.auditLogRepository.save(auditLogEntity);
        } catch (Exception ex) {
            log.error("Error 插入日志失败 [{}]", ex.getMessage(), ex);
        }
    }


    protected void easyExcel(HttpServletResponse response, String excelName, String sheetName, Collection<?> data, Class cls) throws IOException {
        try {
            if (StringUtils.isEmpty(sheetName)) {
                sheetName = "sheet";
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(excelName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream(), cls)
                    .autoCloseStream(Boolean.FALSE).sheet(sheetName)
                    .registerWriteHandler(new EasyExcelCustomCellWriteHandler())
                    .doWrite(data);
        } catch (Exception e) {
            log.error("打印下载excel错误的日志:{}", e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = MapUtils.newHashMap();
            map.put("status", "failure");
            map.put("message", "下载文件失败");
            response.getWriter().println(JsonUtils.objectToJson(map));
        }
    }


    protected void easyExcelAndHeadWrite(HttpServletResponse response, String excelName, String sheetName, List<List<String>> heads, List<List<String>> data) throws IOException {
        try {
            if (StringUtils.isEmpty(sheetName)) {
                sheetName = "sheet";
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(excelName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream())
                    .head(heads)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet(sheetName)
                    .registerWriteHandler(new EasyExcelCustomCellWriteHandler())
                    .doWrite(data);
        } catch (Exception e) {
            log.error("打印下载excel错误的日志:{}", e);
            excelMsg(response, "下载文件失败！");
        }
    }


    private void excelMsg(HttpServletResponse response, String msgs) throws IOException {
        response.reset();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        Map<String, String> map = MapUtils.newHashMap();
        map.put("status", "failure");
        map.put("message", msgs);
        response.getWriter().println(JsonUtils.objectToJson(map));

    }


    /**
     * 作者：wwj
     * 创建日期: 2023-1-03
     * 判断是否是工厂用户
     * ################
     * 修改人: wb04
     * 修改日期: 2023-03-02
     * 修改内容: tb_user 中区分工厂维度和租户（云端）用户的逻辑是 由factory_id;如果工厂id不为空则是工厂用户
     *
     * @return 是否是工厂用户
     */
    public boolean isFactoryUser() throws ThingsboardException {
//        return getCurrentUser().getUserLevel() != UserLeveEnums.TENANT_ADMIN.getCode();  //这样可以吗？？
        UUID factoryId = getCurrentUser().getFactoryId();
        return factoryId != null ? true : false;
    }
}
