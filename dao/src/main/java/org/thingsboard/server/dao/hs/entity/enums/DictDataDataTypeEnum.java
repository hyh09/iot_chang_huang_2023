package org.thingsboard.server.dao.hs.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.*;

/**
 * 数据字典类型
 *
 * @author wwj
 * @since 2021.10.18
 */
@Getter
public enum DictDataDataTypeEnum implements EnumGetter {
    FLOAT("FLOAT", "type-float"),
    BOOLEAN("BOOLEAN", "type-boolean"),
    NUMBER("NUMBER", "type-number"),
    CHARACTER("CHARACTER", "type-character");

    @JsonValue
    private final String code;
    private final String name;

    DictDataDataTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
