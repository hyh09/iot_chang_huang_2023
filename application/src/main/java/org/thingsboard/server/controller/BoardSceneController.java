package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.dao.hs.entity.enums.FileScopeEnum;
import org.thingsboard.server.dao.hs.entity.po.FileInfo;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.service.DeviceMonitorService;
import org.thingsboard.server.dao.hs.service.FileService;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 场景配置接口
 *
 * @author wwj
 * @apiNote 因前端限制，非restful设计。
 * @since 2021.11.22
 */
@Api(value = "场景配置接口", tags = {"场景配置接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api/board/setting")
public class BoardSceneController extends BaseController {

    @Autowired
    DeviceMonitorService deviceMonitorService;

    @Autowired
    FileService fileService;

    /**
     * 获得模型库列表
     */
    @ApiOperation(value = "获得模型库列表")
    @GetMapping("/model")
    public List<FileInfoVO> getBoardModelLocations() throws ThingsboardException, ExecutionException, InterruptedException, IOException {
        return this.fileService.listModels(getTenantId(), FileScopeEnum.DICT_DEVICE_MODEL);
    }

    /**
     * 保存车间场景
     */
    @ApiOperation(value = "保存车间场景", notes = "如果车间已存在场景，则覆盖")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "fileId", value = "文件id", paramType = "query", required = true)
    })
    @PostMapping("/workshop/scene")
    public void saveBoardSetting(
            @RequestParam("workshopId") String workshopId,
            @RequestParam("fileId") String fileId) throws ThingsboardException, ExecutionException, InterruptedException, IOException {
        checkParameter("workshopId", workshopId);
        checkParameter("fileId", fileId);
        var fileInfo = this.fileService.getFileInfoByScopeAndEntityId(getTenantId(), FileScopeEnum.WORKSHOP_SCENE, toUUID(workshopId));
        if (fileInfo != null)
            this.fileService.deleteFile(getTenantId(), fileInfo.getId());
        this.fileService.updateFileScope(getTenantId(), toUUID(fileId), FileScopeEnum.WORKSHOP_SCENE, toUUID(workshopId));
    }

    /**
     * 获得车间场景文件
     */
    @ApiOperation(value = "获得车间场景文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query", required = true),
    })
    @GetMapping("/workshop/scene/location")
    public FileInfo getBoardSettingFileId(
            @RequestParam("workshopId") String workshopId) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("workshopId", workshopId);
        return this.fileService.getFileInfoByScopeAndEntityId(getTenantId(), FileScopeEnum.WORKSHOP_SCENE, toUUID(workshopId));
    }

    /**
     * 删除车间场景
     */
    @ApiOperation(value = "删除车间场景")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query", required = true),
    })
    @DeleteMapping("/workshop/scene")
    public void deleteBoardSetting(
            @RequestParam("workshopId") String workshopId) throws ThingsboardException, IOException {
        checkParameter("workshopId", workshopId);
        var fileInfo = this.fileService.getFileInfoByScopeAndEntityId(getTenantId(), FileScopeEnum.WORKSHOP_SCENE, toUUID(workshopId));
        if (fileInfo != null)
            this.fileService.deleteFile(getTenantId(), fileInfo.getId());
    }

    /**
     * 保存设备场景
     */
    @ApiOperation(value = "保存设备场景", notes = "如果设备已存在场景，则覆盖")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "fileId", value = "文件id", paramType = "query", required = true)
    })
    @PostMapping("/device/scene")
    public void saveDeviceBoardSetting(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("fileId") String fileId) throws ThingsboardException, ExecutionException, InterruptedException, IOException {
        checkParameter("deviceId", deviceId);
        checkParameter("fileId", fileId);
        var fileInfo = this.fileService.getFileInfoByScopeAndEntityId(getTenantId(), FileScopeEnum.DEVICE_SCENE, toUUID(deviceId));
        if (fileInfo != null)
            this.fileService.deleteFile(getTenantId(), fileInfo.getId());
        this.fileService.updateFileScope(getTenantId(), toUUID(fileId), FileScopeEnum.DEVICE_SCENE, toUUID(deviceId));
    }

    /**
     * 获得设备场景文件
     */
    @ApiOperation(value = "获得设备场景文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
    })
    @GetMapping("/device/scene/location")
    public FileInfo getDeviceBoardLocation(
            @RequestParam("deviceId") String deviceId) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("deviceId", deviceId);
        return this.fileService.getFileInfoByScopeAndEntityId(getTenantId(), FileScopeEnum.DEVICE_SCENE, toUUID(deviceId));
    }

    /**
     * 删除设备场景
     */
    @ApiOperation(value = "删除设备场景")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
    })
    @DeleteMapping("/device/scene")
    public void deleteDeviceBoardSetting(
            @RequestParam("deviceId") String deviceId) throws ThingsboardException, IOException {
        checkParameter("deviceId", deviceId);
        var fileInfo = this.fileService.getFileInfoByScopeAndEntityId(getTenantId(), FileScopeEnum.DEVICE_SCENE, toUUID(deviceId));
        if (fileInfo != null)
            this.fileService.deleteFile(getTenantId(), fileInfo.getId());
    }

    /**
     * 查询设备详情
     *
     * @param id 设备id
     */
    @ApiOperation("查询设备详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备Id", paramType = "query", required = true)
    })
    @GetMapping("/rtMonitor/device")
    public DeviceDetailResult getRtMonitorDeviceDetail(@RequestParam("id") String id) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("id", id);
        return this.deviceMonitorService.getRTMonitorDeviceDetail(getTenantId(), id);
    }

    /**
     * 查看设备部件实时数据
     */
    @ApiOperation("查看设备部件实时数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "componentId", value = "设备字典部件Id", paramType = "query", required = true),
    })
    @GetMapping("/rtMonitor/component}")
    public List<DictDeviceComponentPropertyVO> getRtMonitorDeviceComponentDetail(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("componentId") String componentId) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("deviceId", deviceId);
        checkParameter("componentId", componentId);
        return this.deviceMonitorService.getRtMonitorDeviceComponentDetail(getTenantId(), toUUID(deviceId), toUUID(componentId));
    }
}
