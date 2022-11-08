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

    /**
     * 产量列表
     */
   List<EnergyEffciencyNewEntity> yieldList(QueryTsKvVo queryTsKvVo);
}
