package org.thingsboard.server.dao.sqlserver.utils;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fwy
 * @date 2023/1/11 16:10
 */
@Log4j
public class PageJdbcUtil {
    @Resource(name = "sqlServerTemplate")
    private JdbcTemplate jdbcTemplate;

    private <K> int queryTotal(ConditionFunction<K> t, K dto, String sql) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sqlBuffer = new StringBuffer(sql);
        t.sqlWrapper(dto, params, sqlBuffer);
        Object[] para = params.toArray(new Object[params.size()]);
        log.info(">>>>>>>>>sql.toString()" + sqlBuffer.toString());
        return this.jdbcTemplate.queryForObject(sqlBuffer.toString(), para, Integer.class);
    }

    /**
     * @param t
     * @param dto
     * @param v
     * @param countSql
     * @param listSql
     * @param pageSize
     * @param rowNumber
     * @param <K>       条件
     * @param <C>       返回的vo
     * @return
     */
    public <K, C> Pair<Integer, List<C>> queryPageList(ConditionFunction<K> t, K dto, C v, String countSql, String listSql, Integer pageSize, Integer rowNumber) {
        int total = this.queryTotal(t, dto, countSql);
        if (total == 0) {
            return ImmutablePair.of(0, new ArrayList<>());
        }
        List<Object> params = new ArrayList<Object>();
        StringBuffer sqlBuffer = new StringBuffer(listSql);
        if (pageSize != null) {
            sqlBuffer.append("SELECT TOP(?) * FROM ( ");
            params.add(pageSize);
        }
        t.sqlWrapper(dto, params, sqlBuffer);
        if (rowNumber != null) {
            sqlBuffer.append(" )temp_row where rownumber >? ");
            params.add(rowNumber);
        }
        Object[] para = params.toArray(new Object[params.size()]);
        log.info(">>>>>>>>>sql.toString()" + sqlBuffer.toString());
        List queryList = this.jdbcTemplate.query(sqlBuffer.toString(), para, new BeanPropertyRowMapper(v.getClass()));
        return ImmutablePair.of(total, queryList);
    }
}
