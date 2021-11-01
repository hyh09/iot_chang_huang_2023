package org.thingsboard.server.entity.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@ToString
@ApiModel(value = "用户和角色绑定对象实体类")
public class UserRoleVo implements Serializable {

    @NotNull(message = "[用户id]不能为空")
    @ApiModelProperty(value = "用户id")
    private UUID userId;

    @NotNull(message = "[角色id]不能为空")
    @ApiModelProperty(value = "角色id")
    private UUID tenantSysRoleId;
    /**
     *中文描述: 备注
     */
    @ApiModelProperty(value = "用户角色关系中的备注信息;默认为空")
    private String remark;




}
