package org.thingsboard.server.common.data.vo.enums.key;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public enum KeyNameEnums {

    water("water","耗水量"),
    electric("electric","耗电量"),
    gas("gas","耗气量"),
    capacities("capacities","总产量")
    ;

    private  String code;

    private  String name;

    KeyNameEnums(String code, String name) {
        this.code = code;
        this.name = name;
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


    public  static List<String>  getKeyCodes()
    {
       return Stream.of(KeyNameEnums.values()).map(KeyNameEnums::getCode).collect(Collectors.toList());
    }
}
