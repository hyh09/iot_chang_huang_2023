package org.thingsboard.server.service.security.auth;

/**
 * @program: thingsboard
 * @description: 登录的平台类型
 * @author: HU.YUNHUI
 * @create: 2022-01-20 11:06
 **/
public enum ProviderEnums {

    platform_0("平台","0"),
    Intranet_1("内网的","1"),

    ;

    /**
     * 分组的名称
     */
    private  String gName;

    private  String code;

    ProviderEnums(String gName, String code) {
        this.gName = gName;
        this.code = code;
    }


    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
