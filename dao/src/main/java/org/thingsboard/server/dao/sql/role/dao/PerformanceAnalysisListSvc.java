package org.thingsboard.server.dao.sql.role.dao;

import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;

import java.util.List;

/**
 * @Project Name: thingsboard
 * @File Name: PerformanceAnalysisListSvc
 * @Date: 2022/11/7 10:26
 * @author: wb04
 * 业务中文描述: 效能分析列表
 * Copyright (c) 2022,All Rights Reserved.
 */
public interface PerformanceAnalysisListSvc {

    List<EnergyEffciencyNewEntity> getAddValue(QueryTsKvVo queryTsKvVo, String keyName);

    /**
     * 产量列表
     */
   List<EnergyEffciencyNewEntity> yieldList(QueryTsKvVo queryTsKvVo);

    /**
     * 能耗的列表的： 水 电 气 产量
     * @param queryTsKvVo
     * @return
     */
   List<EnergyEffciencyNewEntity> queryEnergyListAll(QueryTsKvVo queryTsKvVo);
}
