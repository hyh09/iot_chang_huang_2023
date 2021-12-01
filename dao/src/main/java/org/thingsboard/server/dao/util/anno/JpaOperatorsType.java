package org.thingsboard.server.dao.util.anno;

import org.thingsboard.server.dao.util.sql.JpaQueryHelper;

import java.lang.annotation.*;

/**
 * @program: thingsboard
 * @description: 操作类型
 * @author: HU.YUNHUI
 * @create: 2021-12-01 18:07
 **/
@Documented
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JpaOperatorsType {

    JpaQueryHelper.Operators value();
}
