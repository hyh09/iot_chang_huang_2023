package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Project Name: thingsboard
 * @File Name: SqlOnTableAnnotation
 * @Date: 2023/2/17 14:05
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlOnFromTableAnnotation {

    String from() default "";

    String whereValue() default "";

}
