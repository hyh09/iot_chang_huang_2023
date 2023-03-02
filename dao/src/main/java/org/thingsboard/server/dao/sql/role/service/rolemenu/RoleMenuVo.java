package org.thingsboard.server.dao.sql.role.service.rolemenu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

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

    @ApiModelProperty( "如果是工厂管理员新增字段【必传】 ##2021-12-02新增字段")
    private UUID  factoryId;

//    @NotNull(message = "[角色id]不能为空")
    @ApiModelProperty(value = "角色id")
    private UUID  roleId;

    @ApiModelProperty(value = "全选--菜单入参id;{注:为空视为解绑此角色下的菜单")
    private List<UUID>  menuVoList;

    @ApiModelProperty(value = "半选--菜单入参id;{注:为空视为解绑此角色下的菜单}")
    private List<UUID>  semiSelectList;


    private UUID  tenantId;





}
