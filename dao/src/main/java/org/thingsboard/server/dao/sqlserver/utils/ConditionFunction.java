package org.thingsboard.server.dao.sqlserver.utils;

import java.util.List;

/**
 * @author fwy
 * @date 2023/1/11 16:09设计模式
 */
public interface ConditionFunction {
    /**
     * 条件拼接函数
     *
     * @param params
     * @param sql
     * @param orderFlag 判断是否排序
     */
    void sqlWrapper(List<Object> params, StringBuffer sql, Boolean orderFlag);
}
