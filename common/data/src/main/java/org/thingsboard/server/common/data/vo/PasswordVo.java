package org.thingsboard.server.common.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "用户管理界面下修改密码的实体类参数")
@ToString
@Data
public class PasswordVo {

    @ApiModelProperty(value = "用户id[用户id不能为空] string类型;")
    @NotBlank(message = "用户id不能为空")
    private  String userId;

    @ApiModelProperty(value = "密码[密码不能为空] string类型;")
    @NotBlank(message = "密码不能为空")
    private  String password;






}
