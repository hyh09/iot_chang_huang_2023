package org.thingsboard.server.common.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 查询用户的输入条件
 * @author: HU.YUNHUI
 * @create: 2021-11-02 17:44
 **/
@Data
@ToString
@ApiModel(value = "查询用户的输入条件")
public class QueryUserVo {

    private UUID  roleId;

    @ApiModelProperty(value = "手机号")
    private  String phoneNumber;

    @ApiModelProperty(value = "邮箱")
    private  String email;

    @ApiModelProperty(value = "用户编码")
    private  String userCode;

    @ApiModelProperty(value = "用名称")
    private  String userName;

    private  UUID tenantId;


    private  UUID createId;

    private  UUID factoryId;

    private  String type;

    private  int userLevel;




}
