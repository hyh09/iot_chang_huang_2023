package org.thingsboard.server.common.data.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 *
 */
@Data
@ApiModel(value = "用户得重复校验接口")
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {

    @ApiModelProperty(value = "用户id;如果是修改需要传此值; 新增不想要传")
    private String userId;

    @ApiModelProperty(value = "手机号")
    private  String phoneNumber;

    @ApiModelProperty(value = "邮箱")
    private  String email;

    @ApiModelProperty(value = "用户编码")
    private  String userCode;

    private UUID  factoryId;


    private  UUID tenantId;
}
