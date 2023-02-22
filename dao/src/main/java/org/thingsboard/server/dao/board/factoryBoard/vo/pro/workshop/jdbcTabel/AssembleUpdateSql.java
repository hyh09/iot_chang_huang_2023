package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * @Project Name: demo-all
 * @File Name: AssembleSql
 * @Date: 2023/2/17 14:35
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
public class AssembleUpdateSql {

    private String sqlAll;

    private Map<String, ?> values;

    public AssembleUpdateSql(String sqlAll) {
        this.sqlAll = sqlAll;
    }

    public AssembleUpdateSql(String sqlAll, Map<String, ?> values) {
        this.sqlAll = sqlAll;
        this.values = values;
    }


    /**
     * UPDATE public.test_user
     * SET role_name='', user_name='';
     */
    public static class AssembleBuildUpdateSql {
        private String updateTable;

        private String setSql;

        private String whereSql;

        private Map<String, ?> values;

        private Object object;

        AssembleBuildUpdateSql() {
        }
    }
}
