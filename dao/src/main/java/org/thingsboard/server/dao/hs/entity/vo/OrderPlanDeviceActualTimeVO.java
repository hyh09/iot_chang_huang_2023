package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 订单计划-设备-实际时间
 *
 * @author wwj
 * @since 2021.10.18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "订单计划-设备-实际时间")
public class OrderPlanDeviceActualTimeVO {

    @NotNull
    @ApiModelProperty(value = "实际开始时间", required = true)
    private Long actualStartTime;

    @NotNull
    @ApiModelProperty(value = "实际结束时间", required = true)
    private Long actualEndTime;
}
