package org.thingsboard.server.dao.sqlserver.server.vo.order;

import lombok.Data;
import org.thingsboard.server.dao.sqlserver.server.vo.RownumberDto;



/**
 * @Project Name: thingsboard
 * @File Name: OrderAnalysisVo
 * @Date: 2022/12/28 14:14
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
public class OrderAnalysisVo extends RownumberDto {

    /**
     * 卡片号，只用于前端搜索使用
     */
    private String sCardNo;

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 品名
     */
    private String materialName;
    /**
     * 颜色
     */
    private String colorName;
    /**
     *订单数量
     */
    private String numberOfOrder;
    /**
     * 卡片数量
     */
    private String numberOfCards;
    /**
     * 创建者
     */
    private String creator;




    private String uGuid;

    private String water;

    private String electricity;

    private String gas;

    /**
     * 整理要求
     */
    private String sremark;

    /**
     * 时长
     */
    private String duration;


}
