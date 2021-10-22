package org.thingsboard.server.dao.util.sql.jpa.transform;

/**
 * sql查询名称转换方式
 * @author Lee
 *
 */
public enum NameTransform {
	UN_CHANGE, //对象封装时，不改变sql语句查询结果的列名, 不去下划线，不转换大小写
    LOWER_CASE, //对象封装时, 将查询结果的列名全转小写，不去下划线
    UNDERLINE_TO_CAMEL ; // 将列名称去下划线转驼峰
}
