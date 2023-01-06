package org.thingsboard.server.dao.board.factoryBoard.vo.collection.onlie;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.hs.entity.vo.DeviceOnlineStatusResult;

/**
 * @Project Name: thingsboard
 * @File Name: DeviceStatusNumVo
 * @Date: 2023/1/6 9:48
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
@ApiModel(value = "设备数量-在线-离线-在线率")
public class DeviceStatusNumVo extends DeviceOnlineStatusResult {

    @ApiModelProperty("在线率%")
    private String onlineRate;
}
