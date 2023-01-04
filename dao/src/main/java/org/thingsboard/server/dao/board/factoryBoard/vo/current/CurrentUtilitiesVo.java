package org.thingsboard.server.dao.board.factoryBoard.vo.current;

import lombok.Data;
import lombok.ToString;

/**
 * @Project Name: thingsboard
 * @File Name: CurrentUtilitiesVo
 * @Date: 2023/1/4 10:18
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
public class CurrentUtilitiesVo {

    private EnergyUnitVo water;

    private EnergyUnitVo electricity;

    private EnergyUnitVo gas;
}
