package org.thingsboard.server.hs.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 数据字典类型
 *
 * @author wwj
 * @since 2021.10.18
 */
@Getter
public enum DictDataType {
    FLOAT("FLOAT", "浮点型"),
    BOOLEAN("BOOLEAN","布尔型"),
    NUMBER("NUMBER", "数值型"),
    CHARACTER("CHARACTER", "字符型");

    @JsonValue
    private final String code;
    private final String name;

    DictDataType(String code, String name){
        this.code = code;
        this.name = name;
    }

    public Map<String, String> toLinkMap() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        Arrays.stream(DictDataType.values()).forEach(e->{
            map.put(e.getName(), e.getCode());
        });
        return map;
    }
}
