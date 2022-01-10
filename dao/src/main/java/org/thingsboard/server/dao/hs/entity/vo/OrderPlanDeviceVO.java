package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.thingsboard.server.dao.hs.entity.po.OrderPlan;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 订单计划-设备
 *
 * @author wwj
 * @since 2021.10.18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "订单计划-设备")
public class OrderPlanDeviceVO {

    @ApiModelProperty(value = "设备计划id", notes = "仅用于展示")
    private String id;

    @NotNull
    @ApiModelProperty(value = "设备Id", required = true)
    private String deviceId;

    @ApiModelProperty(value = "设备名称", notes = "仅用于展示")
    private String deviceName;

    @NotNull
    @ApiModelProperty(value = "计划开始时间", required = true)
    private Long intendedStartTime;

    @NotNull
    @ApiModelProperty(value = "计划结束时间", required = true)
    private Long intendedEndTime;

    @NotNull
    @ApiModelProperty(value = "实际开始时间", required = true)
    private Long actualStartTime;

    @NotNull
    @ApiModelProperty(value = "实际结束时间", required = true)
    private Long actualEndTime;

    @NotNull
    @ApiModelProperty(value = "是否参与运算", required = true)
    private Boolean enabled;

    @ApiModelProperty(value = "产量", notes = "仅用于显示")
    private BigDecimal capacities;

    public OrderPlan toOrderPlan() {
        OrderPlan orderPlan = new OrderPlan();
        BeanUtils.copyProperties(this, orderPlan);
        return orderPlan;
    }
}
