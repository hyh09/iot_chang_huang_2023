package org.thingsboard.server.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 工厂设备请求参数
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "工厂设备请求参数")
public class FactoryDeviceQuery {
    /**
     * 工厂Id
     */
    @ApiModelProperty("工厂Id")
    private String factoryId;

    /**
     * 车间Id
     */
    @ApiModelProperty("车间Id")
    private String workShopId;

    /**
     * 产线Id
     */
    @ApiModelProperty("产线Id")
    private String productionLineId;

    /**
     * 设备Id
     */
    @ApiModelProperty("设备Id")
    private String deviceId;

    /**
     * 是否未分配
     */
    @ApiModelProperty("是否未分配")
    private Boolean isUnAllocation;
}
