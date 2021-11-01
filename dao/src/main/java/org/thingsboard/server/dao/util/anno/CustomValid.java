package org.thingsboard.server.dao.util.anno;

import java.lang.annotation.*;

/**
 * @program: thingsboard
 * @description: 自定义注解
 * @author: HU.YUNHUI
 * @create: 2021-10-29 14:32
 **/
@Documented
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomValid {
}
