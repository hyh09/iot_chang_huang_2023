package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.dao.hs.service.DeviceMonitorService;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.queue.util.TbCoreComponent;

/**
 * 实时监控app接口
 *
 * @author wwj
 * @since 2021.10.27
 */
@Api(value = "实时监控app接口", tags = {"实时监控app接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api/deviceMonitor/app")
public class RTMonitorAppController extends BaseController {

    @Autowired
    DeviceMonitorService deviceMonitorService;

    @Autowired
    DictDeviceService dictDeviceService;


}
