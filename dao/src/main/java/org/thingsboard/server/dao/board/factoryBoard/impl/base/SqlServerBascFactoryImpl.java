package org.thingsboard.server.dao.board.factoryBoard.impl.base;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.SqlOnFieldAnnotation;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.util.JdbcByAssembleSqlUtil;
import org.thingsboard.server.dao.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    protected JdbcByAssembleSqlUtil jdbcByAssembleSqlUtil;

    protected volatile ConcurrentMap<Class, Hashtable<String, SqlOnFieldAnnotation>> sqlMappingMap = new ConcurrentHashMap<>();


    public SqlServerBascFactoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        jdbcByAssembleSqlUtil = new JdbcByAssembleSqlUtil(jdbcTemplate);
    }


    protected void executeSqlByObject(Object obj) throws Exception {
        Hashtable<String, SqlOnFieldAnnotation> table = sqlMappingMap.get(obj.getClass());
        if (table.isEmpty()) {
            return;
        }
        for (Map.Entry<String, SqlOnFieldAnnotation> entry : table.entrySet()) {
            String fieldName = entry.getKey();
            SqlOnFieldAnnotation annotation = entry.getValue();
            String sql = annotation.value();
            Object value = jdbcTemplate.queryForObject(sql, String.class);
            boolean flg = annotation.postfixFlg();
            String methodName = annotation.postTargetMethod();
            if (flg && StringUtils.isNotEmpty(methodName)) {
                Class clazz = annotation.postTargetClass();
                Method m4 = clazz.getDeclaredMethod(methodName, String.class);
                m4.setAccessible(true);
                Object valuePost = m4.invoke(clazz.getDeclaredConstructor().newInstance()
                        , value);
                value = valuePost;
            }
            ReflectionUtils.setFieldValue(obj, fieldName, value);
        }
    }


}
