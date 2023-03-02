package org.thingsboard.server.common.data.vo.enums.key;

/**
 * @Project Name: long-win-iot
 * @File Name: KeyPcNameEnums
 * @Date: 2023/3/2 16:28
 * @author: wb04
 * 业务中文描述: 前端的指定的枚举
 * Copyright (c) 2023,All Rights Reserved.
 */
public enum KeyPcNameEnums {
    WATER("WATER", "耗水量"),
    ELECTRIC("ELECTRIC", "耗电量"),
    VAPOR("VAPOR", "耗气量"),
    ;


    KeyPcNameEnums(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;

    private String name;

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
}
