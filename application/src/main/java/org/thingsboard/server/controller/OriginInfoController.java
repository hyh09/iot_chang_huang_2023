package org.thingsboard.server.controller;

import com.datastax.oss.driver.shaded.guava.common.collect.Maps;
import com.google.api.client.util.Lists;
import io.jsonwebtoken.lang.Collections;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;
import org.thingsboard.server.dao.hs.entity.vo.OrderCustomCapacityResult;
import org.thingsboard.server.dao.hs.entity.vo.OrderVO;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.entity.productioncalender.dto.ProductionMonitorListQry;
import org.thingsboard.server.entity.productioncalender.vo.ProductionMonitorListVo;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * 原生信息接口
 *
 * @author wwj
 * @since 2021.10.18
 */
@Api(value = "原生信息接口", tags = {"原生信息接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class OriginInfoController extends BaseController {

    @Autowired
    ClientService clientService;

    /**
     * 版本信息
     */
    @ApiOperation(value = "版本信息")
    @ApiImplicitParams({
    })
    @GetMapping("/origin/info/version")
    public String getVersion() throws ThingsboardException {
        return HSConstants.VERSION;
    }
}
