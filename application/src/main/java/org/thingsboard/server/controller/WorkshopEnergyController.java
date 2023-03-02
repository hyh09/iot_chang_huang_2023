package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryEnergySvc;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.current.CurrentUtilitiesVo;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.queue.util.TbCoreComponent;

/**
 * @Project Name: thingsboard
 * @File Name: WorkshopEnergyController
 * @Date: 2023/3/2 13:46
 * @author: wb04
 * 业务中文描述: 车间看板接口
 * Copyright (c) 2023,All Rights Reserved.
 */
@Slf4j
@Api(value = "车间看板接口-能耗信息", tags = {"车间看板接口"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/workshop")
public class WorkshopEnergyController extends BaseController{

    @Autowired
    private FactoryEnergySvc factoryEnergySvc;


    @GetMapping("/queryCurrentEnergy11111")
    @ResponseBody
    public CurrentUtilitiesVo queryCurrentEnergy(QueryTsKvVo queryTsKvVo) throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            if (queryTsKvVo.getStartTime() == null) {
                queryTsKvVo.setStartTime(CommonUtils.getZero());
                queryTsKvVo.setEndTime(CommonUtils.getNowTime());
            }
            CurrentUtilitiesVo currentUtilitiesVo = factoryEnergySvc.queryCurrentEnergy(queryTsKvVo, tenantId);
            return currentUtilitiesVo;
        } catch (Exception e) {
            log.error("[工厂看板-能耗信息].queryCurrentEnergy方法异常入参:{}", queryTsKvVo);
            log.error("[工厂看板-能耗信息].queryCurrentEnergy方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }
}
