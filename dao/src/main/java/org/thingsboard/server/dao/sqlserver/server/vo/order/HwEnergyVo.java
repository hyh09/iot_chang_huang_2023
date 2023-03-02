package org.thingsboard.server.dao.sqlserver.server.vo.order;

import lombok.Data;
import lombok.ToString;

/**
 * @Project Name: thingsboard
 * @File Name: HwEnergyVo
 * @Date: 2022/12/28 15:26
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@ToString
public class HwEnergyVo {

    private String usdOrderDtlGUID;

    private String name;

    private String useValue;
}
