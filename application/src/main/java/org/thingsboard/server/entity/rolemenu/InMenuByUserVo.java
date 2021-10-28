package org.thingsboard.server.entity.rolemenu;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@ToString
public class InMenuByUserVo {

    private UUID  userId;

    private String tenantMenuName;


    @NotBlank(message = "菜单类型（PC/APP)不能为空")
    @ApiModelProperty("菜单类型（PC/APP）")
    private String menuType;




}
