package org.thingsboard.server.common.data.vo;
import java.lang.annotation.*;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2021-11-03 19:25
 **/

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RowName {
    String value() default "";
}
