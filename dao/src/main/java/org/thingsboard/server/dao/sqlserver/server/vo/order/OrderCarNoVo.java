package org.thingsboard.server.dao.sqlserver.server.vo.order;

import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.sqlserver.server.vo.RownumberDto;

/**
 * @Project Name: long-win-iot
 * @File Name: OrderCarNoVo
 * @Date: 2023/1/9 10:56
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
public class OrderCarNoVo extends RownumberDto {

    private String uGuid;


    /**
     * 设备序号 sEquipmentNo
     */
    private String deviceNo;

    /**
     * 设备名称 sEquipmentName
     */
    private String deviceName;

    /**
     * 生产卡号
     */
    private String sCardNo;
    /**
     * 物料序号 sMaterialNo
     */
    private String materialNo;
    /**
     * 物料名称 sMaterialName
     */
    private String materialName;

    /**
     * 颜色序号
     */
    private String colorNo;

    /**
     * 颜色名称 tmColor 中的 name
     */
    private String colorName;

    /**
     * 工作编号
     */
    private String workerGroupNo;

    /**
     * 工组名称
     */
    private String workerGroupName;

    /**
     * 工人编号
     */
    private String workerNo;

    /**
     * 工人名称
     */
    private String workerName;
    /**
     * 产量
     */
    private String nTrackQty;

    private String tCreateTime;

    /**
     * 备注
     */
    private String sRemark;
}
