package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.workshop.Workshop;

/**
 * @Project Name: thingsboard
 * @File Name: WorkshopAndRunRateVo
 * @Date: 2023/1/6 13:43
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
@ApiModel(value = "车间的信息")
public class WorkshopAndRunRateVo extends Workshop {

    @ApiModelProperty("运行率%")
    private String onlineRate;

//    private CustomerId customerId;

}
