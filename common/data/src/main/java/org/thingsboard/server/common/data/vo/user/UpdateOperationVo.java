package org.thingsboard.server.common.data.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 系统开关的入参
 * @author: HU.YUNHUI
 * @create: 2022-02-10 13:40
 **/
@Data
@ToString
@ApiModel(value = "用户/角色 系统开关的入参")
public class UpdateOperationVo {

    @NotNull(message = "[id]不能为空")
    @ApiModelProperty(value = "用户id;或者角色id")
    private UUID id;

    @ApiModelProperty(value = "0是可编辑， 1是不可")
    @Max(value = 1,message="超过范围最大值为1")
    @Min(value = 0,message="超过范围最小值为0")
    private  Integer operationType;





}
