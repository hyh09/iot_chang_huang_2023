package org.thingsboard.server.dao.sqlserver.server;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.dao.sqlserver.server.vo.RownumberDto;
import org.thingsboard.server.dao.util.CommonUtils;

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
    protected JdbcTemplate jdbcTemplate;

    public <T extends RownumberDto> PageData<T> pageQuery(String orderBY, String sql, List list, Pageable pageable, Class<T> mappedClass) {
        sql = sql.replaceFirst("select", " select row_number() over(order by " + orderBY + " asc) as rownumber ,");
        String sqlCount = "select count(*) from (" + sql + ") t_count_0";
        Integer count = jdbcTemplate.queryForObject(sqlCount, list.toArray(), Integer.class);
        StringBuffer sqlQuery = new StringBuffer();
        sqlQuery.append(" select ").append(" top(").append(pageable.getPageSize()).append(" ) *").append(" from  ( ")
                .append(sql).append(" ) temp where rownumber > ").append((pageable.getPageNumber()) * pageable.getPageSize());
        List<T> mapList = jdbcTemplate.query(sqlQuery.toString(), list.toArray(), new BeanPropertyRowMapper<>(mappedClass));
        if (CollectionUtils.isNotEmpty(mapList)) {
            mapList.stream().forEach(m1 -> {
                m1.setCreatedTime(CommonUtils.getTimestampOfDateTime(m1.getFactStartTime()));
                m1.setUpdatedTime(CommonUtils.getTimestampOfDateTime(m1.getFactEndTime()));
            });
        }
        Page<T> page = new PageImpl<T>(mapList, pageable, count);
        return new PageData<T>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }

    public <T> PageData<T> pageQuery02(String orderBY, String sql, List list, Pageable pageable, Class<T> mappedClass) {
        String sqlCount = "select count(*) from (" + sql + ") t_count_0";
        sql = sql.replaceFirst("select", " select row_number() over(order by " + orderBY + " asc) as rownumber ,");
        Integer count = jdbcTemplate.queryForObject(sqlCount, list.toArray(), Integer.class);
        StringBuffer sqlQuery = new StringBuffer();
        sqlQuery.append(" select ").append(" top(").append(pageable.getPageSize()).append(" ) *").append(" from  ( ")
                .append(sql).append(" ) temp where rownumber > ").append((pageable.getPageNumber()) * pageable.getPageSize());
        List<T> mapList = jdbcTemplate.query(sqlQuery.toString(), list.toArray(), new BeanPropertyRowMapper<>(mappedClass));
        Page<T> page = new PageImpl<T>(mapList, pageable, count);
        return new PageData<T>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }


}
