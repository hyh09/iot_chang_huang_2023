package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop;

import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.query.SqlColumnAnnotation;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.query.SqlOnFromTableAnnotation;

/**
 * @Project Name: thingsboard
 * @File Name: OrderFulfillmentVo
 * @Date: 2023/2/21 15:12
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
@SqlOnFromTableAnnotation(from = "  ( " +
        "SELECT A.sOrderNo,tTime=MAX(CONVERT(NVARCHAR(10),C.tUpdateTime,120))\n" +
        "FROM dbo.sdOrderHdr A(NOLOCK)\n" +
        "JOIN dbo.sdOrderDtl B(NOLOCK) ON B.usdOrderHdrGUID=A.uGUID\n" +
        "JOIN dbo.sdOrderLot C(NOLOCK) ON C.usdOrderDtlGUID = B.uGUID AND C.sLotStatus='完成' \n" +
        "LEFT JOIN dbo.sdOrderLot D(NOLOCK) ON D.usdOrderDtlGUID = B.uGUID AND D.sLotStatus='生产' \n" +
        "WHERE D.uGUID IS NULL\n" +
        "GROUP BY A.sOrderNo\n" +
        "HAVING MAX(C.tUpdateTime)>=DATEADD(DAY,-7,CONVERT(NVARCHAR(10),GETDATE(),120)) \n" +
        ") A1 " , groupByLast = " GROUP BY A1.tTime ")
public class OrderFulfillmentVo {

    /** 2022-12-16*/
    @SqlColumnAnnotation(name = " A1.tTime ")
    private String time;

    @SqlColumnAnnotation(name = " COUNT(1) ")
    private String value;
}
