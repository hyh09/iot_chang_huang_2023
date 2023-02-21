package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JdbcByAssembleSqlUtil {

    private JdbcTemplate jdbcTemplate;


    /**
     * 查询返回list
     *
     * @param <T>
     * @param t
     * @return
     */
    public <T> List<T> finaListByObj(T t) {
        AssembleSql assembleSql = AssembleSql.buildSql(t);
        String sql = assembleSql.getSqlAll();
        Map<String, ?> values = assembleSql.getValues();
        MapSqlParameterSource parameters = new MapSqlParameterSource(values);
        NamedParameterJdbcTemplate givenParamJdbcTemp = new NamedParameterJdbcTemplate(jdbcTemplate);
        return (List<T>) givenParamJdbcTemp.query(sql, parameters, new BeanPropertyRowMapper<>(t.getClass()));
    }


    public <T> void saveOne(T t) {

    }


    /**
     * @param t   被更新的数据 排除nulL的字段，和剔除 args的字段
     * @param arg 更新的条件
     * @param <T>
     * @return
     */
    public <T> T updateByFiledExcludeNull(T t, String... arg) {
        return t;
    }

    public <T> T findOne(T t) {
        return t;

    }


}
