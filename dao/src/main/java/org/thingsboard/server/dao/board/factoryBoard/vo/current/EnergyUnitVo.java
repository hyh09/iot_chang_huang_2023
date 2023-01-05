package org.thingsboard.server.dao.board.factoryBoard.vo.current;

import lombok.Data;
import lombok.ToString;

/**
 * @Project Name: thingsboard
 * @File Name: EnergyUnitVo
 * @Date: 2023/1/4 10:23
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
//@Builder
@ToString
public class EnergyUnitVo {

    /**
     * 单位
     */
    private String unit;

    /**
     * key中文描述
     */
    private String name;

    /**
     * 实际值
     */
    private String actualValue;

    /**
     * 自定义key
     */
    private String key;
}
