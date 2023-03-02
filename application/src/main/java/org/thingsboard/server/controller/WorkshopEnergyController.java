package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryEnergySvc;
import org.thingsboard.server.dao.board.workshopBoard.CapacitiesTop5Vo;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;

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
public class WorkshopEnergyController extends BaseController {

    @Autowired
    private FactoryEnergySvc factoryEnergySvc;


    /**
     * 创建人: wb04
     * 创建日期: 2023-03-02
     * 接口描述: 查询当天车间下的产量的top5排名
     *
     * @param queryTsKvVo
     * @return
     * @throws ThingsboardException
     */
    @GetMapping("/queryCapacitiesTop5")
    @ResponseBody
    public List<CapacitiesTop5Vo> queryCapacitiesTop5(QueryTsKvVo queryTsKvVo) throws ThingsboardException {
        TenantId tenantId = getTenantId();
        if (queryTsKvVo.getStartTime() == null) {
            queryTsKvVo.setStartTime(CommonUtils.getZero());
            queryTsKvVo.setEndTime(CommonUtils.getNowTime());
        }
        return factoryEnergySvc.queryCapacitiesTop5(queryTsKvVo);
    }
}
