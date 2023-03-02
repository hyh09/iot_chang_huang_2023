package org.thingsboard.server.common.data.vo.rolevo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 角色绑定用户对象
 * @author: HU.YUNHUI
 * @create: 2021-11-02 15:17
 **/
@Data
public class RoleBindUserVo {

    @NotEmpty(message = "[用户id]不能为空")
    @ApiModelProperty(value = "用户id集合  1000")
    private List<UUID> userIds;

    @NotNull(message = "[角色id]不能为空")
    @ApiModelProperty(value = "角色id")
    private UUID tenantSysRoleId;
    /**
     *中文描述: 备注
     */
    @ApiModelProperty(value = "用户角色关系中的备注信息;默认为空")
    private String remark;

}
