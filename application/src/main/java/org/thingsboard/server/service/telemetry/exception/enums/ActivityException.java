package org.thingsboard.server.service.telemetry.exception.enums;

/**
 * @program: thingsboard
 * @description: 自定义异常枚举类
 * @author: HU.YUNHUI
 * @create: 2021-10-29 10:15
 **/
public enum ActivityException {

    FAILURE_ERROR("0","失败的异常状态"),

    ;
    private String code;
    private String message;

    ActivityException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
