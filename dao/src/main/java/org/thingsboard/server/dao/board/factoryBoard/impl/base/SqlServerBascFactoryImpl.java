package org.thingsboard.server.dao.board.factoryBoard.impl.base;

import org.springframework.jdbc.core.JdbcTemplate;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.OrderProductionVo;

import java.util.Hashtable;

/**
 * @Project Name: thingsboard
 * @File Name: SqlServerBascFactoryImpl
 * @Date: 2023/2/16 17:34
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */

public  abstract class SqlServerBascFactoryImpl {

    protected JdbcTemplate jdbcTemplate;

    protected Hashtable<String, String> orderProductionSql;

    public SqlServerBascFactoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    protected  void   getOrderProductionSql(OrderProductionVo orderProductionVo){
//        orderProductionSql
        //ReflectionUtils.setFieldValue(orderProductionVo,);


//       return jdbcTemplate.queryForObject(orderProductionEnums.getSql(),String.class);
    }


}
