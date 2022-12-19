package org.thingsboard.server.dao.hs.entity.vo;

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
     * 分组属性实时数据
     */
    @ApiModelProperty("分组属性实时数据")
    private List<DictDeviceGroupVO> resultList;

    /**
     * 设备部件实时数据
     */
    @ApiModelProperty("设备部件实时数据")
    private List<DictDeviceComponentVO> componentList;

    /**
     * 未分组属性实时数据
     */
    @ApiModelProperty(value = "未分组属性实时数据", notes = "暂时不显示")
    private DictDeviceGroupVO resultUngrouped;

    /**
     * 图表
     */
    @ApiModelProperty(value = "相关的图表数据")
    private List<DictDeviceGraphVO> dictDeviceGraphs;

    /**
     * 预警次数列表，从远及近
     */
    @ApiModelProperty("预警次数列表，从远及近")
    private List<AlarmTimesResult> alarmTimesList;

    /**
     * 设备名称
     */
    @ApiModelProperty("设备名称")
    private String name;

    /**
     * 设备重命名名称
     */
    @ApiModelProperty("设备重命名名称")
    private String rename;

    /**
     * 设备图片
     */
    @ApiModelProperty("设备图片")
    private String picture;

    /**
     * 设备是否在线
     */
    @ApiModelProperty("设备是否在线")
    private Boolean isOnLine;
}
