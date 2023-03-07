package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Project Name: thingsboard
 * @File Name: SqlColumnAnnotation
 * @Date: 2023/2/17 14:08
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlColumnAnnotation {

    String name() default "";

    String queryWhere() default "";

    /**
     * 是否忽略字段， 忽略的化，不在select 和 where后面处理
     *
     * @return
     */
    boolean ignoreField() default false;

    /**
     * 忽略select ,但是只在where 中处理
     *
     * @return
     */
    boolean ignoreSelectField() default false;

}
