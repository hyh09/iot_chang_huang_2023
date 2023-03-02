package org.thingsboard.server.dao.board.factoryBoard.svc;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.*;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.vo.CurrentOrdersInProduction07Vo;

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

    /**
     * 订单生产情况
     * 通过配置 OrderProductionVo 中的 @SqlOnFieldAnnotation 调用 executeSqlByObject ，反射赋值
     *
     * @return
     */
    OrderProductionVo getOrderProduction() throws Exception;

    /**
     * ③  订单的 完成率  和 成品率
     * 通过配置 OrderCompletionRateAndYieldRateVo 中的 @SqlOnFieldAnnotation 调用 executeSqlByObject ，反射赋值
     *
     * @return
     * @throws Exception
     */
    OrderCompletionRateAndYieldRateVo getOrderCompletionRateAndYieldRate() throws Exception;

    /**
     * ④ 订单完成情况
     *
     * @return
     */
    List<OrderFulfillmentVo> queryListOrderFulfillmentVo();

    /**
     * ⑤  工序实时产量
     *
     * @return
     */
    List<ProcessRealTimeOutputVo> queryListProcessRealTimeOutputVo();

    /**
     * ⑥当前在产订单
     *
     * @return
     */
    List<CurrentOrdersInProductionDto> queryCurrentOrdersInProductionDto();

    /**
     * ⑦当前在产订单-在产数 :libra:   已经提供了
     *
     * @return
     */
    List<CurrentOrdersInProduction07Vo> queryCurrentOrdersInProduction07Dto();


}
