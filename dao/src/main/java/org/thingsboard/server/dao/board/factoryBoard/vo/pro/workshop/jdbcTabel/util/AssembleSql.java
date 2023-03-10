package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.util;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.enums.DataBaseTypeEnums;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.query.SqlColumnAnnotation;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.query.SqlOnFromTableAnnotation;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.singleTable.SingleColumn;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.singleTable.SingleTableName;

import java.lang.reflect.Field;
import java.util.*;

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
public class AssembleSql {

    private String sqlAll;


    private Map<String, ?> values;

    private String orderBy;

    private DataBaseTypeEnums dataBaseType;

    public AssembleSql(String sqlAll) {
        this.sqlAll = sqlAll;
    }

    public AssembleSql(String sqlAll, Map<String, ?> values) {
        this.sqlAll = sqlAll;
        this.values = values;
    }

    public AssembleSql(String sqlAll, Map<String, ?> values, String orderBy, DataBaseTypeEnums dataBaseType) {
        this.sqlAll = sqlAll;
        this.values = values;
        this.orderBy = orderBy;
        this.dataBaseType = dataBaseType;
    }

    /**
     * 构建Select 的查询sql
     * 1.目前支持
     * select  from  where group by
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> AssembleSql buildSql(T t) {
        AssembleSql.AssembleBuildSql assembleBuildSql = new AssembleBuildSql().assembleSqlBuilderFrom(t).assembleSqlBuilderSelect().assembleSqlBuilderWhere();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getSelectByDataBaseType(assembleBuildSql));
        stringBuffer.append(assembleBuildSql.selectSql).append(" ");
        stringBuffer.append(" FROM ");
        stringBuffer.append(assembleBuildSql.fromSql).append(" ");
        if (StringUtils.isNotEmpty(assembleBuildSql.whereSql)) {
            stringBuffer.append(" WHERE ");
            stringBuffer.append(assembleBuildSql.whereSql);
        }
        if (StringUtils.isNotEmpty(assembleBuildSql.lastGroupBy)) {
            stringBuffer.append(" GROUP BY ").append(assembleBuildSql.lastGroupBy);
        }
        stringBuffer.append(getOrderByDataBaseType(assembleBuildSql));


        return new AssembleSql(stringBuffer.toString(), assembleBuildSql.values, assembleBuildSql.orderBy, assembleBuildSql.dataBaseType);
    }


    private static String getSelectByDataBaseType(AssembleSql.AssembleBuildSql assembleBuildSql) {
        String sqlSelect = " SELECT ";
        String orderBy = assembleBuildSql.orderBy;
        if (assembleBuildSql.dataBaseType == DataBaseTypeEnums.SQLSERVER && StringUtils.isNotEmpty(orderBy)) {
            return " select row_number() over(order by " + orderBy + " asc) as rownumber ,";
        }
        return sqlSelect;

    }

    private static String getOrderByDataBaseType(AssembleSql.AssembleBuildSql assembleBuildSql) {
        String sqlSelect = "";
        String orderBy = assembleBuildSql.orderBy;
        if (assembleBuildSql.dataBaseType == DataBaseTypeEnums.POSTGRESQL && StringUtils.isNotEmpty(orderBy)) {
            return " ORDER BY" + orderBy;
        }
        return sqlSelect;

    }


    /**
     * 生成insertSql
     * INSERT INTO public."TEST_USER"
     * (user_name, role_name)
     * VALUES('', '');
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> AssembleSql buildSaveOneSql(T t) {
        AssembleSql.AssembleBuildSaveOneSql saveOneSql = new AssembleBuildSaveOneSql().assembleBuildSaveOneSql(t);
        StringBuffer insertSql = new StringBuffer();
        insertSql.append(" INSERT INTO ");
        insertSql.append(" ").append(saveOneSql.tableName).append(" ");
        Map<String, Object> columnValue = saveOneSql.values;
        List<String> fieldList = new ArrayList<>();
        columnValue.forEach((k1, v1) -> {
            fieldList.add(k1);
        });
        StringJoiner fieldStr = new StringJoiner(",", "(", ") ");
        fieldStr.add(String.join(",", fieldList));
        insertSql.append(fieldStr);
        insertSql.append(" VALUES ");
        StringJoiner valueStr = new StringJoiner(",", "(", ") ");
        fieldList.stream().forEach(str1 -> {
            valueStr.add(":" + str1);
        });
        insertSql.append(valueStr);

        return new AssembleSql(insertSql.toString(), saveOneSql.values);

    }


    public static class AssembleBuildSql {
        private String selectSql;

        private String fromSql;

        private String whereSql;

        private boolean whereFlg;

        private String lastGroupBy;


        private Map<String, ?> values;

        private Object object;

        private String orderBy;

        private DataBaseTypeEnums dataBaseType;


        AssembleBuildSql() {
        }

        public <T> AssembleSql.AssembleBuildSql assembleSqlBuilderFrom(T t) {
            this.object = t;
            SqlOnFromTableAnnotation sqlOnFromTableAnnotation = t.getClass().getAnnotation(SqlOnFromTableAnnotation.class);
            if (sqlOnFromTableAnnotation == null) {
                return null;
            }
            String fromSql = sqlOnFromTableAnnotation.from();
            if (StringUtils.isEmpty(fromSql)) {
                return null;
            }
            StringBuffer strSql01 = new StringBuffer();
            strSql01.append(fromSql);
            String whereSql = sqlOnFromTableAnnotation.whereValue();
            this.fromSql = fromSql;
            this.lastGroupBy = sqlOnFromTableAnnotation.groupByLast();
            this.orderBy = sqlOnFromTableAnnotation.orderBy();
            this.dataBaseType = sqlOnFromTableAnnotation.dataBaseType();


            if (StringUtils.isEmpty(whereSql)) {
                this.whereFlg = false;
                return this;
            }
            this.whereFlg = true;
            this.whereSql = whereSql;
            return this;
        }

        /**
         * 构建sql 查询字段的映射
         *
         * @param <T>
         * @return
         */
        public <T> AssembleSql.AssembleBuildSql assembleSqlBuilderSelect() {
            Field[] fields = this.object.getClass().getDeclaredFields();
            if (fields.length < 1) {
                throw new RuntimeException("该类没有字段,无法生产sql");
            }
            List<String> columnList = new ArrayList<>();
            for (Field f : fields) {
                f.setAccessible(true);
                SqlColumnAnnotation sqlColumnAnnotation = f.getAnnotation(SqlColumnAnnotation.class);
                if (sqlColumnAnnotation == null && sqlColumnAnnotation.ignoreField()) {
                    continue;
                }
                if (sqlColumnAnnotation != null && sqlColumnAnnotation.ignoreSelectField()) {
                    continue;
                }
                if (sqlColumnAnnotation == null && StringUtils.isEmpty(sqlColumnAnnotation.name())) {
                    //说明当前的列名和sql一致
                    columnList.add(f.getName());
                } else {
                    String columnAs = sqlColumnAnnotation.name() + " as " + f.getName();
                    columnList.add(columnAs);
                }
                //}
            }
            this.selectSql = String.join(",", columnList);
            return this;
        }

        /**
         * 1 . 类上的 where 如果有 当前缀
         * 2. 如果当前实体的某个字段的值 不为空，则动态拼接
         *
         * @param <T>
         * @return
         */
        public <T> AssembleSql.AssembleBuildSql assembleSqlBuilderWhere() {
            List<String> whereSqlist = new ArrayList<>();
            if (this.whereFlg) {
                whereSqlist.add(this.whereSql);
            }
            Field[] fields = this.object.getClass().getDeclaredFields();
            Map<String, Object> map = new HashMap<>();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    SqlColumnAnnotation sqlColumnAnnotation = field.getAnnotation(SqlColumnAnnotation.class);
                    if (sqlColumnAnnotation == null && sqlColumnAnnotation.ignoreField()) {
                        continue;
                    }
                    Object value = field.get(object);
                    String where = sqlColumnAnnotation.queryWhere();
                    if (value != null && StringUtils.isNotEmpty(value.toString())) {
                        map.put(field.getName(), value);
                        String whereValue = sqlColumnAnnotation.name() + " " + where + " :" + field.getName() + " ";
                        whereSqlist.add(whereValue);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            this.whereSql = String.join(" and ", whereSqlist);
            this.values = map;
            return this;
        }


    }

    /**
     * 构建insert 的语句
     */
    public static class AssembleBuildSaveOneSql {
        private String tableName;
        private Map<String, Object> values;
        private Object object;

        public <T> AssembleSql.AssembleBuildSaveOneSql assembleBuildSaveOneSql(T t) {
            this.object = t;
            SingleTableName singleTableName = t.getClass().getAnnotation(SingleTableName.class);
            String tableName = singleTableName.name();
            if (StringUtils.isNotEmpty(tableName)) {
                this.tableName = tableName;
            }
            Field[] fields = this.object.getClass().getDeclaredFields();
            Map<String, Object> map = new HashMap<>();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    SingleColumn singleColumn = field.getAnnotation(SingleColumn.class);
                    if (singleColumn == null && !singleColumn.insertable()) {
                        continue;
                    }
                    Object value = field.get(object);
                    String columnName = singleColumn.name();
                    if (StringUtils.isNotEmpty(columnName)) {
                        map.put(columnName, value);
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            values = map;
            return this;

        }
    }


}
