package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.board.factoryBoard.impl.FiledNamePostfixMethodUtil;

/**
 * @Project Name: thingsboard
 * @File Name: OrderCompletionRateAndYieldRateVo
 * @Date: 2023/2/17 11:13
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
@ApiModel(value = "订单的 完成率  和 成品率")
public class OrderCompletionRateAndYieldRateVo {

    @ApiModelProperty("成品率")
    @SqlOnFieldAnnotation(value = "SELECT CAST(SUM(ISNULL(D.nInQty,0))*100.0/SUM(C.nQty) AS NUMERIC(18,2))\n" +
            "FROM dbo.sdOrderHdr A(NOLOCK)\n" +
            "JOIN dbo.sdOrderDtl B(NOLOCK) ON B.usdOrderHdrGUID=A.uGUID\n" +
            "JOIN dbo.sdOrderLot C(NOLOCK) ON C.usdOrderDtlGUID = B.uGUID\n" +
            "LEFT JOIN (\n" +
            "SELECT B1.usdOrderLotGUID,nInQty=SUM(B1.nInQty)\n" +
            "FROM dbo.mmSTInHdr A1(NOLOCK) \n" +
            "JOIN dbo.mmSTInDtl B1(NOLOCK) ON B1.ummInHdrGUID = A1.uGUID\n" +
            "GROUP BY B1.usdOrderLotGUID\n" +
            ") D ON D.usdOrderLotGUID = C.uGUID"
            , postfixFlg = true,postTargetClass = FiledNamePostfixMethodUtil.class,postTargetMethod = "formatPercentage")
    private String orderCompletionRate;

    @ApiModelProperty("订单完成率")
    @SqlOnFieldAnnotation(value = "SELECT CAST((B.iCount-A.iCount)*100.0/B.iCount AS NUMERIC(18,2))\n" +
            "FROM (\n" +
            "SELECT iCount=COUNT(1)\n" +
            "FROM (\n" +
            "SELECT DISTINCT A2.sOrderNo\n" +
            "FROM dbo.sdOrderHdr A2(NOLOCK)\n" +
            "JOIN dbo.sdOrderDtl B2(NOLOCK) ON B2.usdOrderHdrGUID=A2.uGUID\n" +
            "JOIN dbo.sdOrderLot C2(NOLOCK) ON C2.usdOrderDtlGUID = B2.uGUID AND C2.sLotStatus='生产' \n" +
            ") A1\n" +
            ") A\n" +
            "JOIN (\n" +
            "SELECT iCount=COUNT(1) FROM dbo.sdOrderHdr A3(NOLOCK)\n" +
            ") B ON 1=1", postfixFlg = true,postTargetClass = FiledNamePostfixMethodUtil.class,postTargetMethod = "formatPercentage")
    private String yieldRate;


}
