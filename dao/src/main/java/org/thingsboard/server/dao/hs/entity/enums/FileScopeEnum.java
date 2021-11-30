package org.thingsboard.server.dao.hs.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * OSS 文件范围字段枚举
 *
 * @author wwj
 * @since 2021.11.26
 */
@Getter
public enum FileScopeEnum implements EnumGetter {
    DEVICE("DEVICE", "设备"),
    DEVICE_PROFILE("DEVICE_PROFILE", "设备配置"),
    DICT_DATA("DICT_DATA", "数据字典"),
    DICT_DEVICE("DICT_DEVICE", "设备字典"),
    DICT_DEVICE_COMPONENT("DICT_DEVICE_COMPONENT", "设备字典部件"),
    FACTORY("FACTORY", "工厂"),
    WORKSHOP("WORKSHOP", "车间"),
    PRODUCTION_LINE("PRODUCTION_LINE", "产线");

    @JsonValue
    private final String code;
    private final String name;

    FileScopeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
