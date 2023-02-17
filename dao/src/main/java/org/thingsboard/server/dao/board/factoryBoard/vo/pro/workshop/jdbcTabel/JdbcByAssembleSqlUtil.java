package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * @Project Name: thingsboard
 * @File Name: JdbcByAssembleSqlUtil
 * @Date: 2023/2/17 16:15
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
public class JdbcByAssembleSqlUtil {

    private JdbcTemplate jdbcTemplate;



    public <T> List<?> finaListByObj(T t){
        AssembleSql assembleSql =   AssembleSql.buildSql(t);
        String sql =assembleSql.getSqlAll();
        Map<String, ?> values  =assembleSql.getValues();
        MapSqlParameterSource parameters = new MapSqlParameterSource(values);
        NamedParameterJdbcTemplate givenParamJdbcTemp = new NamedParameterJdbcTemplate(jdbcTemplate);
        return  givenParamJdbcTemp.query(sql, parameters, new BeanPropertyRowMapper<>(t.getClass()));
    }


    public <T>  void saveOne(T t){

    }


    public <T> T updateByFiled(T t){
        return  t;
    }

    public <T> T findOne(T t){
        return  t;

    }




}
