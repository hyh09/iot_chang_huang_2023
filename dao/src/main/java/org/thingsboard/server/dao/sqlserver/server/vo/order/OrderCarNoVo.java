package org.thingsboard.server.dao.sqlserver.server.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("OrderCarNoVo卡片下面的物料信息-卡片信息")

public class OrderCarNoVo extends RownumberDto {

    @ApiModelProperty("uGuid")
    private String uGuid;


    /**
     * 设备序号 sEquipmentNo
     */
    @ApiModelProperty("设备序号")
    private String deviceNo;

    /**
     * 设备名称 sEquipmentName
     */
    @ApiModelProperty("设备名称")
    private String deviceName;

    /**
     * 生产卡号
     */
    @ApiModelProperty("生产卡号")
    private String sCardNo;
    /**
     * 物料序号 sMaterialNo
     */
    @ApiModelProperty("物料序号")
    private String materialNo;
    /**
     * 物料名称 sMaterialName
     */
    @ApiModelProperty("物料名称")
    private String materialName;

    /**
     * 颜色序号
     */
    @ApiModelProperty("颜色序号")
    private String colorNo;

    /**
     * 颜色名称 tmColor 中的 name
     */
    @ApiModelProperty("颜色名称")
    private String colorName;

    /**
     * 工组编号
     */
    @ApiModelProperty("工组编号")
    private String workerGroupNo;

    /**
     * 工组名称
     */
    @ApiModelProperty("工组名称")
    private String workerGroupName;

    /**
     * 工人编号
     */
    @ApiModelProperty("工人编号")
    private String workerNo;

    /**
     * 工人名称
     */
    @ApiModelProperty("工人名称")
    private String workerName;
    /**
     * 产量
     */
    @ApiModelProperty("产量")
    private String nTrackQty;

//    private String tCreateTime;

    /**
     * 备注
     */
    @ApiModelProperty("sRemark备注")
    private String sRemark;
}
