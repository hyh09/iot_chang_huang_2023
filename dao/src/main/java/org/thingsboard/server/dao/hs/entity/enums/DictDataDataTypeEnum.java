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
public enum DictDataDataTypeEnum {
    FLOAT("FLOAT", "type-float"),
    BOOLEAN("BOOLEAN","type-boolean"),
    NUMBER("NUMBER", "type-number"),
    CHARACTER("CHARACTER", "type-character");

    @JsonValue
    private final String code;
    private final String name;

    DictDataDataTypeEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    public static List<Map<String, String>> toResourceList() {
        List<Map<String, String>> list = new ArrayList<>();
        Arrays.stream(DictDataDataTypeEnum.values()).forEach(e->{
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put("name", e.getName());
            map.put("code", e.getCode());
            list.add(map);
        });
        return list;
    }
}
