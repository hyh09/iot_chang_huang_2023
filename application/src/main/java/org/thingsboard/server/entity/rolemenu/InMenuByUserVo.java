package org.thingsboard.server.entity.rolemenu;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.annotatonsvc.IsRight;
import org.thingsboard.server.annotatonsvc.MunuTypeEnum;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@ToString
public class InMenuByUserVo {

    private UUID  userId;

    private String tenantMenuName;


    @ApiModelProperty("菜单类型（PC/APP）")
    @IsRight(map = {MunuTypeEnum.PC, MunuTypeEnum.APP}, key = "systemType",message = "所传的要在 菜单类型范围中（PC/APP)")
    private String menuType;




}
