package org.thingsboard.server.dao.sql.role.service.rolemenu;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.sql.role.service.annotatonsvc.IsRight;
import org.thingsboard.server.dao.sql.role.service.annotatonsvc.MunuTypeEnum;
//import org.thingsboard.server.annotatonsvc.IsRight;
//import org.thingsboard.server.annotatonsvc.MunuTypeEnum;

import java.util.UUID;

@Data
@ToString
@ApiModel(value = "查询角色下菜单的实体【入参】")
public class InMenuByUserVo {

    @ApiModelProperty("用户标识id")
    private UUID  userId;

    @ApiModelProperty("角色id")
    private UUID  roleId;

    @ApiModelProperty("菜单的名称:")
    private String tenantMenuName;


    @ApiModelProperty("菜单类型（PC/APP）")
    @IsRight(map = {MunuTypeEnum.PC, MunuTypeEnum.APP}, key = "systemType",message = "所传的要在 菜单类型范围中（PC/APP)")
    private String menuType;

    @ApiModelProperty( "如果是工厂管理员新增字段【必传】")
    private UUID  factoryId;


    private UUID  tenantId;





}
