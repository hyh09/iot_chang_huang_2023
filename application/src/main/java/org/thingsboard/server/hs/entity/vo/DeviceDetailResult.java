package org.thingsboard.server.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 设备详情
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "设备详情")
public class DeviceDetailResult {

    /**
     * 设备Id
     */
    @ApiModelProperty("设备Id")
    private String id;

    /**
     * 工厂名称
     */
    @ApiModelProperty("工厂名称")
    private String factoryName;

    /**
     * 车间名称
     */
    @ApiModelProperty("车间名称")
    private String workShopName;

    /**
     * 产线名称
     */
    @ApiModelProperty("产线名称")
    private String productionLineName;

    /**
     * 是否未分配
     */
    @ApiModelProperty("是否未分配")
    private Boolean isUnAllocation;

    /**
     * 实时数据
     */
    @ApiModelProperty("实时数据")
    private List<DeviceDetailGroupResult> resultList;

    /**
     * 预警次数列表，从远及近
     */
    @ApiModelProperty("预警次数列表，从远及近")
    private List<AlarmTimesResult> alarmTimesList;
}
