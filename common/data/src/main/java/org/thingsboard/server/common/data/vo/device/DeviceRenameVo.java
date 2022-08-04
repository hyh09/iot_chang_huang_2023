package org.thingsboard.server.common.data.vo.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Project Name: thingsboard
 * File Name: DeviceRenameVo
 * Package Name: org.thingsboard.server.common.data.vo.device
 * Date: 2022/8/4 16:00
 * author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
public class DeviceRenameVo   {

    @ApiModelProperty("设备名称")
    protected String rename;
}
