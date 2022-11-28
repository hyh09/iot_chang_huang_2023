package org.thingsboard.server.dao.util.sql;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Project Name: thingsboard
 * @File Name: JdbcTemplateUtil
 * @Date: 2022/11/28 15:18
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */

@Slf4j
@Repository
public class JdbcTemplateUtil {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public <T> T queryForObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper)
    {
        List<T> results = jdbcTemplate.query(sql, args, argTypes, new RowMapperResultSetExtractor<>(rowMapper, 1));
        if(CollectionUtils.isEmpty(results)){
            return  null;
        }
        return results.get(0);
    }

    public <T> T  queryForObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType)
    {
        List<T> results = jdbcTemplate.query(sql, args, argTypes, new RowMapperResultSetExtractor<>( new SingleColumnRowMapper<>(requiredType), 1));
        if(CollectionUtils.isEmpty(results)){
            return  null;
        }
        return  results.get(0);

    }


}
