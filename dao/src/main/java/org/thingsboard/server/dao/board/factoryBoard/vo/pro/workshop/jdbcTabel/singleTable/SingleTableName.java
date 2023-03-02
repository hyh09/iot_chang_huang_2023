package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.singleTable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SingleTableName {

    String name() default "";
}
