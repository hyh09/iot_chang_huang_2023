package org.thingsboard.server.entity.rolemenu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 *
 */
@Data
@ToString
@ApiModel(value = "角色-菜单 绑定对象实体类")
public class RoleMenuVo {

    @NotNull(message = "[角色id]不能为空")
    @ApiModelProperty(value = "角色id")
    private UUID  roleId;

    @NotEmpty(message = "[菜单id]不能为空")
    @ApiModelProperty(value = "菜单入参id")
    private List<InputMenuVo> menuVoList;




}
