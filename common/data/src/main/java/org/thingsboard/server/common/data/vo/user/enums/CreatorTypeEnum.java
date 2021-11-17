package org.thingsboard.server.common.data.vo.user.enums;

/**
 * @program: thingsboard
 * @description: 创建者类别
 * @author: HU.YUNHUI
 * @create: 2021-11-17 09:24
 **/

public enum CreatorTypeEnum {

    TENANT_CATEGORY("TENANT_CATEGORY","由租户创建的"),
    FACTORY_MANAGEMENT("FACTORY_MANAGEMENT","由工厂创建的,工厂列表级别"),

    ;

    private  String code;

     private  String name;

    CreatorTypeEnum(String code, String name) {
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
}
