package org.thingsboard.server.common.data.vo.enums.key;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public enum KeyNameEnums {

    water("water", "耗水量"),
    electric("electric", "耗电量"),
    gas("gas", "耗气量"),
    capacities("capacities", "总产量");

    private String code;

    private String name;

    KeyNameEnums(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 创建人: wb04
     * 创建日期: 2023-03-01
     * 方法描述: 翻译当前的code; 用当前枚举中的name 翻译
     *
     * @param code
     * @return
     */
    public static String translateCode(String code) {
        Optional<KeyNameEnums> enumsOptional = Stream.of(KeyNameEnums.values()).filter(m1 -> m1.getCode().equals(code)).findFirst();
        if (enumsOptional.isPresent()) {
            return enumsOptional.get().getName();
        }
        return code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static List<String> getKeyCodes() {
        return Stream.of(KeyNameEnums.values()).map(KeyNameEnums::getCode).collect(Collectors.toList());
    }
}
