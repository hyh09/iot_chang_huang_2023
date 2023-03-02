package org.thingsboard.server.dao.hs.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 工厂层级结构行类型
 *
 * @author wwj
 * @since 2021.10.18
 */
@Getter
public enum FactoryHierarchyRowTypeEnum implements EnumGetter {
    FACTORY("factory", "工厂"),
    WORKSHOP("workShop", "车间"),
    PRODUCTION_LINE("prodLine", "产线"),
    DEVICE("device", "设备");

    @JsonValue
    private final String code;
    private final String name;

    FactoryHierarchyRowTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
