package org.thingsboard.server.dao.board.factoryBoard.vo.energy.top;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 * @Project Name: thingsboard
 * @File Name: FactoryEnergyTop
 * @Date: 2023/1/4 16:06
 * @author: wb04
 * 业务中文描述: 能耗top5
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
public class FactoryEnergyTop {

    private UUID deviceId;

    private  String deviceName;

    private String water;

    private String electricity;

    private String gas;

}
