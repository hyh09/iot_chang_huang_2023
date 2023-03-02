package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop;

import org.thingsboard.server.dao.board.factoryBoard.impl.FiledNamePostfixMethodUtil;

import java.lang.annotation.*;

/**
 * @author wb04
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SqlOnFieldAnnotation {
    /**
     * 字段对应的sql
     *
     * @return
     */
    String value() default "";

    /**
     * 是否处理字段
     *
     * @return
     */
    boolean postfixFlg() default false;

    /**
     * 处理字段的 类
     *
     * @return
     */
    Class postTargetClass() default FiledNamePostfixMethodUtil.class;

    /**
     * 目标类中的方法
     *
     * @return
     */
    String postTargetMethod() default "";

}
