/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import org.thingsboard.server.common.data.validation.NoXss;
import org.thingsboard.server.dao.TenantEntityDao;
import org.thingsboard.server.dao.TenantEntityWithDataDao;
import org.thingsboard.server.dao.exception.DataValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public abstract class DataValidator<D extends BaseData<?>> {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    //只允许字母和数字的
    public static final String ONLY_LETTER_OR_NUMBER = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]*";



    private static Validator fieldsValidator;

    static {
        initializeFieldsValidator();
    }

    public void validate(D data, Function<D, TenantId> tenantIdFunction) {
        try {
            if (data == null) {
                throw new DataValidationException("Data object can't be null!");
            }

            List<String> validationErrors = validateFields(data);
            if (!validationErrors.isEmpty()) {
                throw new IllegalArgumentException("Validation error: " + String.join(", ", validationErrors));
            }

            TenantId tenantId = tenantIdFunction.apply(data);
            validateDataImpl(tenantId, data);
            if (data.getId() == null) {
                validateCreate(tenantId, data);
            } else {
                validateUpdate(tenantId, data);
            }
        } catch (DataValidationException e) {
            log.error("Data object is invalid: [{}]", e.getMessage());
            throw e;
        }
    }

    protected void validateDataImpl(TenantId tenantId, D data) {
    }

    protected void validateCreate(TenantId tenantId, D data) {
    }

    protected void validateUpdate(TenantId tenantId, D data) {
    }

    protected boolean isSameData(D existentData, D actualData) {
        return actualData.getId() != null && existentData.getId().equals(actualData.getId());
    }

    public static void validateEmail(String email) {
        if (!doValidateEmail(email)) {
//            throw new DataValidationException("Invalid email address format '" + email + "'!");
            throw new DataValidationException("无效的邮箱格式【 " + email + "】!");

        }
    }

    /**
     * 校验编码 例如：用户的编码  角色的编码 只允许为:  字母+数字
     * @param code
     */
    public  static  void validateCode(String code)
    {
       if(!doValidateCode(code))
       {
           throw new DataValidationException("无效的格式【 " + code + "】！编码要为 字母和数字的组合！");
       }
    }


    private  static  boolean doValidateCode(String code)
    {
        if(code ==  null)
        {
            return  false;
        }
      return code.matches(ONLY_LETTER_OR_NUMBER);
    }


    private static boolean doValidateEmail(String email) {
        if (email == null) {
            return false;
        }

        Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
        return emailMatcher.matches();
    }

    private List<String> validateFields(D data) {
        Set<ConstraintViolation<D>> constraintsViolations = fieldsValidator.validate(data);
        return constraintsViolations.stream()
                .map(ConstraintViolation::getMessage)
                .distinct()
                .collect(Collectors.toList());
    }

    protected void validateNumberOfEntitiesPerTenant(TenantId tenantId,
                                                     TenantEntityDao tenantEntityDao,
                                                     long maxEntities,
                                                     EntityType entityType) {
        if (maxEntities > 0) {
            long currentEntitiesCount = tenantEntityDao.countByTenantId(tenantId);
            if (currentEntitiesCount >= maxEntities) {
                throw new DataValidationException(String.format("Can't create more then %d %ss!",
                        maxEntities, entityType.name().toLowerCase().replaceAll("_", " ")));
            }
        }
    }

    protected void validateMaxSumDataSizePerTenant(TenantId tenantId,
                                                     TenantEntityWithDataDao dataDao,
                                                     long maxSumDataSize,
                                                     long currentDataSize,
                                                     EntityType entityType) {
        if (maxSumDataSize > 0) {
            if (dataDao.sumDataSizeByTenantId(tenantId) + currentDataSize > maxSumDataSize) {
                throw new DataValidationException(String.format("Failed to create the %s, files size limit is exhausted %d bytes!",
                        entityType.name().toLowerCase().replaceAll("_", " "), maxSumDataSize));
            }
        }
    }

    protected static void validateJsonStructure(JsonNode expectedNode, JsonNode actualNode) {
        Set<String> expectedFields = new HashSet<>();
        Iterator<String> fieldsIterator = expectedNode.fieldNames();
        while (fieldsIterator.hasNext()) {
            expectedFields.add(fieldsIterator.next());
        }

        Set<String> actualFields = new HashSet<>();
        fieldsIterator = actualNode.fieldNames();
        while (fieldsIterator.hasNext()) {
            actualFields.add(fieldsIterator.next());
        }

        if (!expectedFields.containsAll(actualFields) || !actualFields.containsAll(expectedFields)) {
            throw new DataValidationException("Provided json structure is different from stored one '" + actualNode + "'!");
        }
    }

    private static void initializeFieldsValidator() {
        HibernateValidatorConfiguration validatorConfiguration = Validation.byProvider(HibernateValidator.class).configure();
        ConstraintMapping constraintMapping = validatorConfiguration.createConstraintMapping();
        constraintMapping.constraintDefinition(NoXss.class).validatedBy(NoXssValidator.class);
        validatorConfiguration.addMapping(constraintMapping);

        fieldsValidator = validatorConfiguration.buildValidatorFactory().getValidator();
    }
}
