package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.plugin.ComponentLifecycleEvent;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;
import org.thingsboard.server.dao.hs.entity.vo.DeviceProfileVO;
import org.thingsboard.server.dao.hs.service.DeviceMonitorService;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import java.util.List;
import java.util.Objects;

import static org.thingsboard.server.dao.service.Validator.validatePageLink;

/**
 * 报警规则管理接口
 *
 * @author wwj
 * @since 2021.10.25
 */
@Api(value = "报警规则管理接口", tags = {"报警规则管理接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api/deviceMonitor/alarmRule")
public class AlarmRuleController extends BaseController {

    @Autowired
    DictDeviceService dictDeviceService;

    @Autowired
    DeviceMonitorService deviceMonitorService;

    /**
     * 获得未配置设备配置的设备字典列表，默认按创建时间倒排
     */
    @ApiOperation(value = "获得未配置设备配置的设备字典列表")
    @GetMapping(value = "/dictDevice/unused")
    public List<DictDevice> listDictDeviceUnused() throws ThingsboardException {
        return this.dictDeviceService.listDictDeviceUnused(getTenantId());
    }

    /**
     * 获得设备配置列表
     *
     * @see DeviceProfileController#getDeviceProfiles
     */
    @ApiOperation(value = "获得设备配置列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query", defaultValue = "createdTime"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query", defaultValue = "desc"),
            @ApiImplicitParam(name = "name", value = "名称", paramType = "query")})
    @GetMapping(value = "/device/profile")
    public PageData<DeviceProfile> listDeviceProfile(@RequestParam int pageSize,
                                                     @RequestParam int page,
                                                     @RequestParam(required = false) String name,
                                                     @RequestParam(required = false, defaultValue = "createdTime") String sortProperty,
                                                     @RequestParam(required = false, defaultValue = "desc") String sortOrder) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
        validatePageLink(pageLink);
        return deviceMonitorService.listDeviceProfile(getTenantId(), name, pageLink);
    }

    /**
     * 获得设备配置详情
     *
     * @param id 设备配置id
     * @see DeviceProfileController#getDeviceProfileById(String)
     */
    @ApiOperation(value = "获得设备配置详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备配置id", dataType = "string", paramType = "path", required = true)})
    @GetMapping(value = "/device/profile/{id}")
    public DeviceProfileVO getDeviceProfileDetail(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("id", id);
        DeviceProfileId deviceProfileId = new DeviceProfileId(toUUID(id));
        return this.deviceMonitorService.getDeviceProfileDetail(getTenantId(), deviceProfileId);
    }

    /**
     * 新增或修改设备配置,包括报警规则
     * <p>
     * 修改项：增加了设备字典的保存
     *
     * @param deviceProfileVO 设备配置
     * @see DeviceProfileController#saveDeviceProfile(DeviceProfile)
     */
    @ApiOperation(value = "新增或修改设备配置,包括报警规则")
    @PostMapping(value = "/device/profile")
    @SuppressWarnings("Duplicates")
    public DeviceProfileVO saveDeviceProfile(@RequestBody DeviceProfileVO deviceProfileVO) throws ThingsboardException {
        DeviceProfile deviceProfile = new DeviceProfile();
        BeanUtils.copyProperties(deviceProfileVO, deviceProfile);
        try {
            boolean created = deviceProfile.getId() == null;
            deviceProfile.setTenantId(getTenantId());

            checkEntity(deviceProfile.getId(), deviceProfile, Resource.DEVICE_PROFILE);

            boolean isFirmwareChanged = false;
            boolean isSoftwareChanged = false;

            if (!created) {
                DeviceProfile oldDeviceProfile = deviceProfileService.findDeviceProfileById(getTenantId(), deviceProfile.getId());
                if (!Objects.equals(deviceProfile.getFirmwareId(), oldDeviceProfile.getFirmwareId())) {
                    isFirmwareChanged = true;
                }
                if (!Objects.equals(deviceProfile.getSoftwareId(), oldDeviceProfile.getSoftwareId())) {
                    isSoftwareChanged = true;
                }
            }

            DeviceProfile savedDeviceProfile = checkNotNull(deviceProfileService.saveDeviceProfile(deviceProfile));

            tbClusterService.onDeviceProfileChange(savedDeviceProfile, null);
            tbClusterService.broadcastEntityStateChangeEvent(deviceProfile.getTenantId(), savedDeviceProfile.getId(),
                    created ? ComponentLifecycleEvent.CREATED : ComponentLifecycleEvent.UPDATED);

            logEntityAction(savedDeviceProfile.getId(), savedDeviceProfile,
                    null,
                    created ? ActionType.ADDED : ActionType.UPDATED, null);

            otaPackageStateService.update(savedDeviceProfile, isFirmwareChanged, isSoftwareChanged);

            // 增加设备字典的绑定
            this.deviceMonitorService.bindDictDeviceToDeviceProfile(deviceProfileVO.getDictDeviceIdList(), savedDeviceProfile.getId());

            sendEntityNotificationMsg(getTenantId(), savedDeviceProfile.getId(),
                    deviceProfile.getId() == null ? EdgeEventActionType.ADDED : EdgeEventActionType.UPDATED);
            return deviceProfileVO;
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.DEVICE_PROFILE), deviceProfile,
                    null, deviceProfile.getId() == null ? ActionType.ADDED : ActionType.UPDATED, e);
            throw handleException(e);
        }
    }

    /**
     * 删除设备配置
     * <p>
     * 修改项：移除绑定的设备字典
     *
     * @param id 设备配置id
     * @see DeviceProfileController#deleteDeviceProfile(String)
     */
    @ApiOperation(value = "删除设备配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备配置id", dataType = "string", paramType = "path")})
    @DeleteMapping(value = "/device/profile/{id}")
    public void deleteDeviceProfile(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("id", id);
        try {
            DeviceProfileId deviceProfileId = new DeviceProfileId(toUUID(id));
            DeviceProfile deviceProfile = checkDeviceProfileId(deviceProfileId, Operation.DELETE);
            deviceProfileService.deleteDeviceProfile(getTenantId(), deviceProfileId);

            tbClusterService.onDeviceProfileDelete(deviceProfile, null);
            tbClusterService.broadcastEntityStateChangeEvent(deviceProfile.getTenantId(), deviceProfile.getId(), ComponentLifecycleEvent.DELETED);

            // 删除绑定的设备字典
            this.deviceMonitorService.deleteBindDictDevice(deviceProfileId);

            logEntityAction(deviceProfileId, deviceProfile,
                    null,
                    ActionType.DELETED, null, id);

            sendEntityNotificationMsg(getTenantId(), deviceProfile.getId(), EdgeEventActionType.DELETED);
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.DEVICE_PROFILE),
                    null,
                    null,
                    ActionType.DELETED, e, id);
            throw handleException(e);
        }
    }

    /**
     * 设备配置设为默认
     *
     * @param id 设备配置id
     * @see DeviceProfileController#setDefaultDeviceProfile(String)
     */
    @ApiOperation(value = "设备配置设为默认")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备配置id", dataType = "string", paramType = "path")})
    @PostMapping(value = "/device/profile/{id}/default")
    @SuppressWarnings("Duplicates")
    public DeviceProfile updateDeviceProfileDefault(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("id", id);
        try {
            DeviceProfileId deviceProfileId = new DeviceProfileId(toUUID(id));
            DeviceProfile deviceProfile = checkDeviceProfileId(deviceProfileId, Operation.WRITE);
            DeviceProfile previousDefaultDeviceProfile = deviceProfileService.findDefaultDeviceProfile(getTenantId());
            if (deviceProfileService.setDefaultDeviceProfile(getTenantId(), deviceProfileId)) {
                if (previousDefaultDeviceProfile != null) {
                    previousDefaultDeviceProfile = deviceProfileService.findDeviceProfileById(getTenantId(), previousDefaultDeviceProfile.getId());

                    logEntityAction(previousDefaultDeviceProfile.getId(), previousDefaultDeviceProfile,
                            null, ActionType.UPDATED, null);
                }
                deviceProfile = deviceProfileService.findDeviceProfileById(getTenantId(), deviceProfileId);

                logEntityAction(deviceProfile.getId(), deviceProfile,
                        null, ActionType.UPDATED, null);
            }
            return deviceProfile;
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.DEVICE_PROFILE),
                    null,
                    null,
                    ActionType.UPDATED, e, id);
            throw handleException(e);
        }
    }
}
