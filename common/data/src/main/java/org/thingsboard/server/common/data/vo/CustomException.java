package org.thingsboard.server.common.data.vo;

import lombok.Data;

/**
 * @program: thingsboard
 * @description: 自定义异常-统一格式返回
 * @author: HU.YUNHUI
 * @create: 2021-10-29 10:15
 **/

@Data
public class CustomException extends RuntimeException{

    /**
     * 状态码
     */
    private  String code;
    /**
     * 错误提示信息
     */
    private  String msg;

    public CustomException(String code) {
        this.code = code;
    }

    public CustomException(String code, Object message) {
        super(message.toString());
        this.code = code;
    }



}

