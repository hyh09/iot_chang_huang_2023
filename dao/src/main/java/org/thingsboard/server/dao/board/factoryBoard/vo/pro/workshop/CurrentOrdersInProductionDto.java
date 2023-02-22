package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.query.SqlColumnAnnotation;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.query.SqlOnFromTableAnnotation;

/**
 * @Project Name: thingsboard
 * @File Name: CurrentOrdersInProductionVo
 * @Date: 2023/2/21 18:00
 * @author: wb04
 * 业务中文描述: 当前在产订单
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
@ApiModel(value = "当前在产订单 ")
@SqlOnFromTableAnnotation(from = " dbo.sdOrderHdr A(NOLOCK)\n" +
        "JOIN dbo.sdOrderDtl B(NOLOCK) ON B.usdOrderHdrGUID=A.uGUID\n" +
        "JOIN dbo.sdOrderLot C(NOLOCK) ON C.usdOrderDtlGUID = B.uGUID AND C.sLotStatus='生产' \n" +
        "JOIN dbo.pbCustomer F(NOLOCK) ON F.uGUID = A.upbCustomerGUID\n" +
        "JOIN dbo.mmMaterial G(NOLOCK) ON G.uGUID=B.ummMaterialGUID\n" +
        "JOIN (\n" +
        "SELECT A1.usdOrderDtlGUID,iCount=COUNT(1),nQty=SUM(A1.nQty)\n" +
        "FROM dbo.sdOrderLot A1(NOLOCK)\n" +
        "GROUP BY A1.usdOrderDtlGUID\n" +
        ") D ON D.usdOrderDtlGUID = B.uGUID\n" +
        "LEFT JOIN (\n" +
        "SELECT A2.usdOrderDtlGUID,iCount=COUNT(1)\n" +
        "FROM dbo.sdOrderLot A2(NOLOCK)\n" +
        "WHERE A2.sLotStatus='完成'\n" +
        "GROUP BY A2.usdOrderDtlGUID\n" +
        ") E ON E.usdOrderDtlGUID = B.uGUID",
       groupByLast = " GROUP BY A.sOrderNo,F.sCustomerNo")
public class CurrentOrdersInProductionDto {

    @ApiModelProperty("订单编号")
    @SqlColumnAnnotation(name = "A.sOrderNo")
    private  String orderNumber;

    @ApiModelProperty("客户编号")
    @SqlColumnAnnotation(name = "F.sCustomerNo")
    private  String customerNumber;

    @ApiModelProperty("物料名")
    @SqlColumnAnnotation(name = "dbo.fnpbConcatStringEx(G.sMaterialName,',')")
    private  String materialName;

    @ApiModelProperty("下单数")
    @SqlColumnAnnotation(name = "SUM(D.nQty)")
    private  String numberOfOrders;

    @ApiModelProperty("进度百分比")
    @SqlColumnAnnotation(name = "SUM(ISNULL(E.iCount,0))*100/SUM(D.iCount)")
    private  String progress;


}
