package org.thingsboard.server.service.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @program: thingsboard
 * @description: 用户不存在#2022年1月20日 长胜分支登录接口新增校验
 * @author: HU.YUNHUI
 * @create: 2022-01-20 13:26
 **/

public class UserDoesNotExistException extends AuthenticationException {

    public UserDoesNotExistException(String msg) {
        super(msg);
    }

    public UserDoesNotExistException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
