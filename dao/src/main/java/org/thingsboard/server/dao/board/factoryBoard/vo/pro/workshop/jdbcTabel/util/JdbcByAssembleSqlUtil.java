package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.enums.DataBaseTypeEnums;

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


    public <T> PageData<T> pageQuery(T t, Pageable pageable) {
        AssembleSql assembleSql = AssembleSql.buildSql(t);
        DataBaseTypeEnums dataBaseTypeEnums = assembleSql.getDataBaseType();
        if (dataBaseTypeEnums == DataBaseTypeEnums.SQLSERVER) {
            return pageQuerySqlServer(t, pageable, assembleSql);
        }
        return pageQueryPgSqL(t, pageable, assembleSql);
    }


    private <T> PageData<T> pageQuerySqlServer(T t, Pageable pageable, AssembleSql assembleSql) {
        String sql = assembleSql.getSqlAll();
        Map<String, ?> values = assembleSql.getValues();
        String sqlCount = "select count(*) from (" + sql + ") t_count_0";
        MapSqlParameterSource parameters = new MapSqlParameterSource(values);
        NamedParameterJdbcTemplate givenParamJdbcTemp = new NamedParameterJdbcTemplate(jdbcTemplate);
        Integer count = givenParamJdbcTemp.queryForObject(sqlCount, parameters, Integer.class);
        StringBuffer sqlQuery = new StringBuffer();
        sqlQuery.append(" select ").append(" top(").append(pageable.getPageSize()).append(" ) *").append(" from  ( ")
                .append(sql).append(" ) temp where rownumber > ").append((pageable.getPageNumber()) * pageable.getPageSize());
        List<T> mapList = (List<T>) givenParamJdbcTemp.query(sqlQuery.toString(), parameters, new BeanPropertyRowMapper<>(t.getClass()));
        Page<T> page = new PageImpl<T>(mapList, pageable, count);
        return new PageData<T>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }


    private <T> PageData<T> pageQueryPgSqL(T t, Pageable pageable, AssembleSql assembleSql) {
        String sql = assembleSql.getSqlAll();
        Map<String, ?> values = assembleSql.getValues();
        String sqlCount = "select count(*) from (" + sql + ") t_count_0";
        MapSqlParameterSource parameters = new MapSqlParameterSource(values);
        NamedParameterJdbcTemplate givenParamJdbcTemp = new NamedParameterJdbcTemplate(jdbcTemplate);
        Integer count = givenParamJdbcTemp.queryForObject(sqlCount, parameters, Integer.class);
        StringBuffer sqlQuery = new StringBuffer();
        sqlQuery.append(sql);
        sqlQuery.append(" LIMIT ").append(pageable.getPageSize()).append(" OFFSET ").append((pageable.getPageNumber()) * pageable.getPageSize());
        List<T> mapList = (List<T>) givenParamJdbcTemp.query(sqlQuery.toString(), parameters, new BeanPropertyRowMapper<>(t.getClass()));
        Page<T> page = new PageImpl<T>(mapList, pageable, count);
        return new PageData<T>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }


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
