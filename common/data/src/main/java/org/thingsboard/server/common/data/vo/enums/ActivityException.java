package org.thingsboard.server.common.data.vo.enums;

/**
 * @program: thingsboard
 * @description: 自定义异常枚举类
 * @author: HU.YUNHUI
 * @create: 2021-10-29 10:15
 **/
public enum ActivityException {

    FAILURE_ERROR("0","失败的异常状态"),
    MAX_QUERY_ERROR("500","查询数据太大,请您缩小时间查询!"),

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
