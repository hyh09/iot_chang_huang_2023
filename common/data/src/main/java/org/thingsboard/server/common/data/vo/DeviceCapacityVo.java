package org.thingsboard.server.common.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 设备产能入参
 * @author: HU.YUNHUI
 * @create: 2021-12-29 10:05
 **/
@Data
@ToString
@ApiModel(value = "设备产能入参")
public class DeviceCapacityVo {

    /**
     *中文描述: 设备id
     * 是否必传:  是
     */
    @ApiModelProperty("中文描述: 设备id;【必填】  ")
    private UUID  id;

    /**
     *中文描述: 起始时间
     * 是否必传: 是
     */
    @ApiModelProperty("中文描述: 起始时间;【必填】  ")
    private  Long startTime;

    /**
     *中文描述: 结束时间
     * 是否必传: 是
     */
    @ApiModelProperty("中文描述: 结束时间;【必填】  ")
    private  Long endTime;
}
