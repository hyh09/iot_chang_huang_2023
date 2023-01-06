package org.thingsboard.server.dao.board.factoryBoard.svc;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.ChartResultVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request.ChartDateEnums;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.current.CurrentUtilitiesVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.top.FactoryEnergyTop;

import java.util.List;

/**
 * @Project Name: thingsboard
 * @File Name: FactoryEnergySvc
 * @Date: 2023/1/4 9:28
 * @author: wb04
 * 业务中文描述: 工厂看板——能耗信息
 * Copyright (c) 2023,All Rights Reserved.
 */
public interface FactoryEnergySvc {

    /**
     * 当日的耗电量-耗水量-耗气量
     *
     * @param queryTsKvVo
     * @return
     */
    CurrentUtilitiesVo queryCurrentEnergy(QueryTsKvVo queryTsKvVo, TenantId tenantId) throws JsonProcessingException;

    /**
     * top5
     * @param queryTsKvVo
     * @param tenantId
     * @return
     */
    List<FactoryEnergyTop> queryCurrentTop(QueryTsKvVo queryTsKvVo,TenantId tenantId);


    ChartResultVo queryTrendChart(QueryTsKvVo queryTsKvVo, ChartDateEnums dateEnums );


}
