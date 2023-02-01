package org.thingsboard.server.dao.sqlserver.utils;

import lombok.extern.log4j.Log4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
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

    private int queryTotal(ConditionFunction t, String sql) {
        List<Object> params = new ArrayList<>();
        StringBuffer sqlBuffer = new StringBuffer(sql);
        t.sqlWrapper(params, sqlBuffer, false);
        Object[] para = params.toArray(new Object[params.size()]);
        log.info(">>>>>>>>>sql.toString()" + sqlBuffer);
        return this.jdbcTemplate.queryForObject(sqlBuffer.toString(), para, Integer.class);
    }

    /**
     * @param t
     * @param voClass  返回vo
     * @param listSql
     * @param pageLink
     * @param <C>      返回的vo
     * @return
     */
    private <C> Pair<Integer, List<C>> queryPageList(ConditionFunction t, Class<C> voClass, String listSql, PageLink pageLink) {
        int pageSize = pageLink.getPageSize();
        if (pageSize < 1) {
            throw new RuntimeException("页码大小不能小于1");
        }
        //0是第一页1是第二页
        int rowNumber = pageLink.getPage() * pageSize;
        if (rowNumber < 0) {
            rowNumber = 0;
        }
        //转为为count(*) sql
        Select select;
        try {
            select = (Select) CCJSqlParserUtil.parse(listSql);
        } catch (JSQLParserException e) {
            log.info(">>>>>>>>>Select:" + listSql);
            throw new RuntimeException("转为select count(*) 出错");
        }
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        plainSelect.setSelectItems(COUNT_SELECT_ITEM);
        int total = this.queryTotal(t, select.toString());
        if (total == 0) {
            return ImmutablePair.of(0, new ArrayList<>());
        }
        List<Object> params = new ArrayList<>();
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(listSql);
        t.sqlWrapper(params, sqlBuffer, true);
        sqlBuffer.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
        params.add(rowNumber);
        params.add(pageSize);
        Object[] para = params.toArray(new Object[params.size()]);
        log.info(">>>>>>>>>sql.toString()" + sqlBuffer);
        List queryList = this.jdbcTemplate.query(sqlBuffer.toString(), para, new BeanPropertyRowMapper(voClass));
        return ImmutablePair.of(total, queryList);
    }

    /**
     * 转换count(*)失败的时候使用
     *
     * @param t
     * @param voClass  返回vo
     * @param countSql 总条数
     * @param listSql
     * @param pageLink
     * @param <C>      返回的vo
     * @return
     */
    private <C> Pair<Integer, List<C>> queryPageList(ConditionFunction t, Class<C> voClass, String countSql, String listSql, PageLink pageLink) {
        int pageSize = pageLink.getPageSize();
        if (pageSize < 1) {
            throw new RuntimeException("页码大小不能小于1");
        }
        int rowNumber = (pageLink.getPage() - 1) * pageSize;
        if (rowNumber < 0) {
            rowNumber = 0;
        }
        int total = this.queryTotal(t, countSql);
        if (total == 0) {
            return ImmutablePair.of(0, new ArrayList<>());
        }
        List<Object> params = new ArrayList<>();
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(listSql);
        t.sqlWrapper(params, sqlBuffer, true);
        sqlBuffer.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
        params.add(rowNumber);
        params.add(pageSize);
        Object[] para = params.toArray(new Object[params.size()]);
        log.info(">>>>>>>>>sql.toString()" + sqlBuffer);
        List queryList = this.jdbcTemplate.query(sqlBuffer.toString(), para, new BeanPropertyRowMapper(voClass));
        return ImmutablePair.of(total, queryList);
    }

    /**
     * @param t
     * @param voClass
     * @param listSql
     * @param pageLink
     * @param <C>
     * @return
     */
    public <C> PageData<C> queryList(ConditionFunction t, Class<C> voClass, String listSql, PageLink pageLink) {
        Pair<Integer, List<C>> pagePair = this.queryPageList(t, voClass, listSql, pageLink);
        Integer total = pagePair.getLeft();
        List<C> recordList = pagePair.getRight();
        return new PageData<>(recordList, total / pageLink.getPageSize(), total, CollectionUtils.isNotEmpty(recordList));
    }

    /**
     * 转换count(*)失败的时候使用
     *
     * @param t
     * @param voClass
     * @param countSql
     * @param listSql
     * @param pageLink
     * @param <C>
     * @return
     */
    public <C> PageData<C> queryList(ConditionFunction t, Class<C> voClass, String countSql, String listSql, PageLink pageLink) {
        Pair<Integer, List<C>> pagePair = this.queryPageList(t, voClass, countSql, listSql, pageLink);
        Integer total = pagePair.getLeft();
        List<C> recordList = pagePair.getRight();
        return new PageData<>(recordList, total / pageLink.getPageSize(), total, CollectionUtils.isNotEmpty(recordList));
    }

    protected static final List<SelectItem> COUNT_SELECT_ITEM = Collections.singletonList(defaultCountSelectItem());

    private static SelectItem defaultCountSelectItem() {
        Function function = new Function();
        ExpressionList expressionList = new ExpressionList(Collections.singletonList(new LongValue(1)));
        function.setName("COUNT");
        function.setParameters(expressionList);
        return new SelectExpressionItem(function);
    }
}
