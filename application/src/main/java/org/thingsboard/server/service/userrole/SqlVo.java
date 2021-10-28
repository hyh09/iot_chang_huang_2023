package org.thingsboard.server.service.userrole;

import lombok.Data;

import java.util.Map;

@Data
public class SqlVo {

    private  String sql;

    private Map<String, Object> param;


    public SqlVo(String sql, Map<String, Object> param) {
        this.sql = sql;
        this.param = param;
    }
}
