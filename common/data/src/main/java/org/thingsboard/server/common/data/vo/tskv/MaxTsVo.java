package org.thingsboard.server.common.data.vo.tskv;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2021-12-07 11:50
 **/
@Data
@ToString
public class MaxTsVo {

    @ApiModelProperty("设备id UUID类型")
    private UUID deviceId;

    @ApiModelProperty("产线id  UUID类型")
    private UUID productionLineId;


    @ApiModelProperty("车间id UUID类型")
    private UUID workshopId;

    @ApiModelProperty("工厂id  UUID类型")
    private UUID factoryId;

    private UUID tenantId;

    @ApiModelProperty("当前要传的属性 数组类型  ###不需要前端传了")
    private String key;

    @ApiModelProperty("当前要传的属性 数组类型  ###能耗的水 电气 入参; ##也不需要传了")
    private List<String> keys;

    /**
     * 产能标记 true是产能的统计
     */
    private Boolean capSign=false;

}
