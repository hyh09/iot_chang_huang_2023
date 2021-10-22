package org.thingsboard.server.common.data.vo;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@ToString
@Data
public class PasswordVo {

    @NotBlank(message = "用户id不能为空")
    private  String userId;

    @NotBlank(message = "密码不能为空")
    private  String password;



}
