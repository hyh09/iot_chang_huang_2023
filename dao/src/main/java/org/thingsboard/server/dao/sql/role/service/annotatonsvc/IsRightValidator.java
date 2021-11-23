package org.thingsboard.server.dao.sql.role.service.annotatonsvc;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsRightValidator implements ConstraintValidator<IsRight, String> {

    private  MunuTypeEnum[] array;
    private  String key;

    @Override
    public void initialize(IsRight constraintAnnotation) {
        array =constraintAnnotation.map();
        key = constraintAnnotation.key();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(array.length>0)
        {
            for(MunuTypeEnum e:array)
            {
                if(e.getValue().equals(value) && e.getKey().equals(key))
                {
                    return true;
                }
            }
            return false;
        }

        return true;
    }

}

