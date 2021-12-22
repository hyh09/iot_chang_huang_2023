package org.thingsboard.server.dao.hs.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 设备字典属性类型
 *
 * @author wwj
 * @since 2021.10.18
 */
@Getter
public enum DictDevicePropertyTypeEnum implements EnumGetter {
    DEVICE("DEVICE", "type-device"),
    COMPONENT("COMPONENT", "type-component");

    @JsonValue
    private final String code;
    private final String name;

    DictDevicePropertyTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
