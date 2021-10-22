package org.thingsboard.server.common.data.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
public class PasswordVo {

    @NotBlank(message = "用户id不能为空")
    private  String UserId;

    @NotBlank(message = "密码不能为空")
    private  String password;


    public UUID getUserId() {
        return UUID.fromString(UserId);
    }
}
