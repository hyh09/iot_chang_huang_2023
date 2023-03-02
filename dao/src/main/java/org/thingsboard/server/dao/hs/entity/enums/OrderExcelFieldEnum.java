package org.thingsboard.server.dao.hs.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 订单Excel字段枚举
 *
 * @author wwj
 * @since 2021.11.10
 */
@Getter
public enum OrderExcelFieldEnum {
    FACTORY("factoryId", "*工厂");

    @JsonValue
    private final String code;
    private final String name;

    OrderExcelFieldEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
