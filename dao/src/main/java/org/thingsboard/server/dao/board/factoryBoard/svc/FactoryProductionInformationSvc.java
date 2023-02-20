package org.thingsboard.server.dao.board.factoryBoard.svc;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.OrderCompletionRateAndYieldRateVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.OrderProductionVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.WorkshopAndRunRateVo;

import java.util.List;
import java.util.UUID;

/**
 * @Project Name: thingsboard
 * @File Name: FactoryProductionInformationSvc
 * @Date: 2023/1/4 9:29
 * @author: wb04
 * 业务中文描述: 工厂看板——生产信息相关接口
 * Copyright (c) 2023,All Rights Reserved.
 */
public interface FactoryProductionInformationSvc {

    /**
     * 查询车间的信息
     *
     * @param tenantId  租户id
     * @param factoryId 工厂id
     * @return
     */
    List<WorkshopAndRunRateVo> queryWorkshopAndRunRate(TenantId tenantId, UUID factoryId);


    OrderProductionVo getOrderProduction() throws Exception;


    OrderCompletionRateAndYieldRateVo getOrderCompletionRateAndYieldRate() throws Exception;

}
