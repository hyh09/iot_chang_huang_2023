package org.thingsboard.server.dao.board.factoryBoard.impl;

import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryEnergySvc;
import org.thingsboard.server.dao.board.factoryBoard.vo.current.CurrentUtilitiesVo;

/**
 * @Project Name: thingsboard
 * @File Name: FactoryEnergyImpl
 * @Date: 2023/1/4 9:32
 * @author: wb04
 * 业务中文描述: 工厂看板——能耗信息接口
 * Copyright (c) 2023,All Rights Reserved.
 */
@Service
public class FactoryEnergyImpl implements FactoryEnergySvc {
    @Override
    public CurrentUtilitiesVo queryCurrentEnergy(QueryTsKvVo queryTsKvVo) {
        return null;
    }
}
