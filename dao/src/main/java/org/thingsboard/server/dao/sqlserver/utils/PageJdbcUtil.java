package org.thingsboard.server.dao.sqlserver.utils;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.page.PageLink;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fwy
 * @date 2023/1/11 16:10
 */
@Component
@Log4j
public class PageJdbcUtil {
    @Resource(name = "sqlServerTemplate")
    private JdbcTemplate jdbcTemplate;

    private <K> int queryTotal(ConditionFunction<K> t, K dto, String sql) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sqlBuffer = new StringBuffer(sql);
        t.sqlWrapper(dto, params, sqlBuffer, false);
        Object[] para = params.toArray(new Object[params.size()]);
        log.info(">>>>>>>>>sql.toString()" + sqlBuffer.toString());
        return this.jdbcTemplate.queryForObject(sqlBuffer.toString(), para, Integer.class);
    }

    /**
     * @param t
     * @param dto
     * @param voClass 返回vo
     * @param countSql 总条数
     * @param listSql
     * @param pageLink
     * @param <K>      条件
     * @param <C>      返回的vo
     * @return
     */
    public <K, C> Pair<Integer, List<C>> queryPageList(ConditionFunction<K> t, K dto, Class<C> voClass, String countSql, String listSql, PageLink pageLink) {
        int pageSize = pageLink.getPageSize();
        if (pageSize < 1) {
            throw new RuntimeException("页码大小不能小于1");
        }
        int rowNumber = (pageLink.getPage() - 1) * pageSize;
        if (rowNumber < 0) {
            rowNumber = 0;
        }
        int total = this.queryTotal(t, dto, countSql);
        if (total == 0) {
            return ImmutablePair.of(0, new ArrayList<>());
        }
        List<Object> params = new ArrayList<>();
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(listSql);
        t.sqlWrapper(dto, params, sqlBuffer, true);
        sqlBuffer.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
        params.add(rowNumber);
        params.add(pageSize);
        Object[] para = params.toArray(new Object[params.size()]);
        log.info(">>>>>>>>>sql.toString()" + sqlBuffer);
        List queryList = this.jdbcTemplate.query(sqlBuffer.toString(), para, new BeanPropertyRowMapper(voClass));
        return ImmutablePair.of(total, queryList);
    }
}
