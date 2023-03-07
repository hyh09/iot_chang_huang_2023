package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.util;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Project Name: long-win-iot
 * @File Name: JbbcRowMapper
 * @Date: 2023/3/7 16:20
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
public class JbbcRowMapper<T> implements RowMapper<T> {

    private Class<T> mappedClass;

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        return null;
    }
}
