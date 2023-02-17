package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop;

import lombok.Data;
import lombok.ToString;

/**
 * @Project Name: long-win-iot
 * @File Name: OrderProductionVo
 * @Date: 2023/2/16 17:26
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
public class OrderProductionVo {


    @SqlOnFieldAnnotation("SELECT COUNT(1)\n" +
            "FROM (\n" +
            "SELECT DISTINCT A.sOrderNo\n" +
            "FROM dbo.sdOrderHdr A(NOLOCK)\n" +
            "JOIN dbo.sdOrderDtl B(NOLOCK) ON B.usdOrderHdrGUID=A.uGUID\n" +
            "JOIN dbo.sdOrderLot C(NOLOCK) ON C.usdOrderDtlGUID = B.uGUID AND C.sLotStatus='生产' \n" +
            ") A1")
    private String inProductionOrder;

    @SqlOnFieldAnnotation("SELECT COUNT(1)\n" +
            "FROM (\n" +
            "SELECT A.sOrderNo,tTime=MAX(C.tUpdateTime)\n" +
            "FROM dbo.sdOrderHdr A(NOLOCK)\n" +
            "JOIN dbo.sdOrderDtl B(NOLOCK) ON B.usdOrderHdrGUID=A.uGUID\n" +
            "JOIN dbo.sdOrderLot C(NOLOCK) ON C.usdOrderDtlGUID = B.uGUID AND C.sLotStatus='完成' \n" +
            "LEFT JOIN dbo.sdOrderLot D(NOLOCK) ON D.usdOrderDtlGUID = B.uGUID AND D.sLotStatus='生产' \n" +
            "WHERE D.uGUID IS NULL\n" +
            "GROUP BY A.sOrderNo\n" +
            "HAVING MAX(C.tUpdateTime)>=DATEADD(WEEK,DATEDIFF(WEEK,0,GETDATE()),0) \n" +
            ") A1")
    private String orderCompleterThisWeek;


    @SqlOnFieldAnnotation("SELECT COUNT(1)\n" +
            "FROM (\n" +
            "SELECT A.sOrderNo,tTime=MAX(C.tUpdateTime)\n" +
            "FROM dbo.sdOrderHdr A(NOLOCK)\n" +
            "JOIN dbo.sdOrderDtl B(NOLOCK) ON B.usdOrderHdrGUID=A.uGUID\n" +
            "JOIN dbo.sdOrderLot C(NOLOCK) ON C.usdOrderDtlGUID = B.uGUID AND C.sLotStatus='完成' \n" +
            "LEFT JOIN dbo.sdOrderLot D(NOLOCK) ON D.usdOrderDtlGUID = B.uGUID AND D.sLotStatus='生产' \n" +
            "WHERE D.uGUID IS NULL\n" +
            "GROUP BY A.sOrderNo\n" +
            "HAVING MAX(C.tUpdateTime)>=DATEADD(MONTH,DATEDIFF(MONTH,0,GETDATE()),0)\n" +
            ") A1")
    private String orderCompletedThisMonth;



    @SqlOnFieldAnnotation("SELECT COUNT(1)\n" +
            "FROM (\n" +
            "SELECT A.sOrderNo,tTime=MAX(C.tUpdateTime)\n" +
            "FROM dbo.sdOrderHdr A(NOLOCK)\n" +
            "JOIN dbo.sdOrderDtl B(NOLOCK) ON B.usdOrderHdrGUID=A.uGUID\n" +
            "JOIN dbo.sdOrderLot C(NOLOCK) ON C.usdOrderDtlGUID = B.uGUID AND C.sLotStatus='完成' \n" +
            "LEFT JOIN dbo.sdOrderLot D(NOLOCK) ON D.usdOrderDtlGUID = B.uGUID AND D.sLotStatus='生产' \n" +
            "WHERE D.uGUID IS NULL\n" +
            "GROUP BY A.sOrderNo\n" +
            "HAVING MAX(C.tUpdateTime)>=DATEADD(YEAR,DATEDIFF(YEAR,0,GETDATE()),0)\n" +
            ") A1")
    private String annualCompletedOrder;


}
