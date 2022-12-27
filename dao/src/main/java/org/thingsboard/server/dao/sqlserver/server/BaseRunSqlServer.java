package org.thingsboard.server.dao.sqlserver.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.page.PageData;

import java.util.List;

/**
 * @Project Name: thingsboard
 * @File Name: BaseRunSqlServer
 * @Date: 2022/12/27 14:17
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Component
public class BaseRunSqlServer {
    @Autowired
    @Qualifier("sqlServerTemplate")
    private JdbcTemplate jdbcTemplate;

    public <T> PageData<T> pageQuery(String sql, List list, Pageable pageable, Class<T> mappedClass) {
        String sqlCount = "select count(*) from (" + sql + ") t_count_0";
        Integer count = jdbcTemplate.queryForObject(sqlCount, list.toArray(), Integer.class);
        StringBuffer sqlQuery = new StringBuffer();
        sqlQuery.append(" select ").append(" top(").append(pageable.getPageSize()).append(" ) *").append(" from  ( ")
                .append(sql).append(" ) temp where rownumber > ").append((pageable.getPageNumber()) * pageable.getPageSize());
        List<T> mapList = jdbcTemplate.query(sqlQuery.toString(), list.toArray(), new BeanPropertyRowMapper<>(mappedClass));
        Page<T> page = new PageImpl<T>(mapList, pageable, count);
        return new PageData<T>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }

}
