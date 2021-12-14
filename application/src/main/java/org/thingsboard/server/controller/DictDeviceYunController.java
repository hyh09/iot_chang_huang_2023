package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;


/**
 * 设备字典Yun接口
 *
 * @author wwj
 * @since 2021.10.21
 */
@Api(value = "设备字典Yun接口", tags = {"设备字典Yun接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api/yun")
public class DictDeviceYunController extends BaseController {

    @Autowired
    DictDeviceService dictDeviceService;

    /**
     * 获得设备字典详情
     */
    @ApiOperation(value = "获得设备字典详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备字典id", paramType = "path", required = true),
            @ApiImplicitParam(name = "tenantId", value = "租户Id", paramType = "query", required = true),
    })
    @GetMapping("/dict/device/{id}")
    public DictDeviceVO getDictDeviceDetail(@PathVariable("id") String id, @RequestParam("tenantId") String tenantId) throws ThingsboardException {
        checkParameter("id", id);
        checkParameter("tenantId", tenantId);
        return this.dictDeviceService.getDictDeviceDetail(id, new TenantId(toUUID(tenantId)));
    }

    /**
     * 获得指定时间后变更的设备字典列表
     */
    @ApiOperation(value = "获得指定时间后变更的设备字典列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", required = true),
    })
    @GetMapping("/dict/devices")
    public List<DictDevice> getDictDeviceList(@RequestParam("tenantId") String tenantId, @RequestParam("startTime") Long startTime) throws ThingsboardException {
        checkParameter("id", tenantId);
        checkParameter("startTime", startTime);
        return this.dictDeviceService.listDictDevicesByStartTime(new TenantId(toUUID(tenantId)), startTime);
    }
}
