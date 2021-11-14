package org.thingsboard.server.common.data.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @program: thingsboard
 * @description: 编码生成查询
 * @author: HU.YUNHUI
 * @create: 2021-11-01 15:30
 **/
@Data
@ToString
@ApiModel(value = "用户/角色 编码的生成实体出入参实体")
public class CodeVo {

    @ApiModelProperty(value = "查询编码的生成结果; 目前： 1代表是用户  2代表角色")
    private  String  key;
    @ApiModelProperty(value = "对应的编码")
    private  String  code;
}
