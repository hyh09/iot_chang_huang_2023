package org.thingsboard.server.dao.hs.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.thingsboard.server.common.data.vo.DeviceCapacityVo;
import org.thingsboard.server.dao.hs.utils.CommonUtil;

/**
 * 订单设备
 *
 * @author wwj
 * @since 2021.10.18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "订单设备计划")
public class OrderPlan extends BasePO {

    private static final long serialVersionUID = 4934987555236873733L;

    @ApiModelProperty(value = "Id")
    private String id;

    @ApiModelProperty(value = "租户Id")
    private String tenantId;

    @ApiModelProperty(value = "设备Id")
    private String deviceId;

    @ApiModelProperty(value = "订单Id")
    private String orderId;

    @ApiModelProperty(value = "计划开始时间")
    private Long intendedStartTime;

    @ApiModelProperty(value = "计划结束时间")
    private Long intendedEndTime;

    @ApiModelProperty(value = "实际开始时间")
    private Long actualStartTime;

    @ApiModelProperty(value = "实际结束时间")
    private Long actualEndTime;

    @ApiModelProperty(value = "是否参与运算")
    private Boolean enabled;

    @ApiModelProperty(value = "排序值")
    private Integer sort;

    public DeviceCapacityVo toDeviceCapacityVO() {
        DeviceCapacityVo capacityVO = new DeviceCapacityVo();
        capacityVO.setId(CommonUtil.toUUIDNullable(id));
        capacityVO.setEntityId(CommonUtil.toUUIDNullable(deviceId));
        capacityVO.setStartTime(actualStartTime);
        capacityVO.setEndTime(actualEndTime);
        return capacityVO;
    }
}
