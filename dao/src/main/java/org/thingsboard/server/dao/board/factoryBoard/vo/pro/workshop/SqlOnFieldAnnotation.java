package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SqlOnFieldAnnotation {
    String value() default "";

}
