package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop;

import org.thingsboard.server.dao.board.factoryBoard.impl.FiledNamePostfixMethodUtil;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SqlOnFieldAnnotation {
    String value() default "";

    boolean postfixFlg() default false;

    Class postTargetClass() default  FiledNamePostfixMethodUtil.class;

    String  postTargetMethod() default "" ;

}
