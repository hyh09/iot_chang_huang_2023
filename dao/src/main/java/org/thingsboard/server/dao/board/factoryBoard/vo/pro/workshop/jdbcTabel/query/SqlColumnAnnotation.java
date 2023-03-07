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

    boolean ignoreField() default false;

    boolean ignoreSelectField() default false;

}
