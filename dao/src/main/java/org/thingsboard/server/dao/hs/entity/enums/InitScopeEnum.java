package org.thingsboard.server.dao.hs.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 初始化范围字段枚举
 *
 * @author wwj
 * @since 2021.11.10
 */
@Getter
public enum InitScopeEnum implements EnumGetter {
    DICT_DEVICE_GROUP("DICT_DEVICE_GROUP", "设备字典分组");

    @JsonValue
    private final String code;
    private final String name;

    InitScopeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
