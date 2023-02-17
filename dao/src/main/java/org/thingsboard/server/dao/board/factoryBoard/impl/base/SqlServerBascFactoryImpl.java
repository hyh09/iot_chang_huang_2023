package org.thingsboard.server.dao.board.factoryBoard.impl.base;

import org.springframework.jdbc.core.JdbcTemplate;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.OrderProductionVo;
import org.thingsboard.server.dao.util.ReflectionUtils;

import java.util.Hashtable;
import java.util.Map;

/**
 * @Project Name: thingsboard
 * @File Name: SqlServerBascFactoryImpl
 * @Date: 2023/2/16 17:34
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */

public abstract class SqlServerBascFactoryImpl {

    protected JdbcTemplate jdbcTemplate;

    protected Hashtable<String, String> orderProductionSql;

    public SqlServerBascFactoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    protected void getOrderProductionSql(OrderProductionVo orderProductionVo) {
        System.out.println("打印初始化的sql:" + JacksonUtil.toString(orderProductionSql));
        for (Map.Entry<String, String> entry : orderProductionSql.entrySet()) {
            String fieldName = entry.getKey();
            String sql = entry.getValue();

            String value = jdbcTemplate.queryForObject(sql, String.class);
            ReflectionUtils.setFieldValue(orderProductionVo, fieldName, value);
        }
    }


}
