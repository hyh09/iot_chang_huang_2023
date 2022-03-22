package org.thingsboard.server.dao.sql.role.service.annotatonsvc;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {IsRightValidator.class })
public @interface IsRight {


    String message() default "这是验证失败的提示信息";

    MunuTypeEnum[] map();

    String  key();

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };


}