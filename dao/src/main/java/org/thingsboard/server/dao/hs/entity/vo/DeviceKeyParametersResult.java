package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 设备关键参数
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "设备关键参数")
public class DeviceKeyParametersResult {

    /**
     * 设备Id
     */
    @ApiModelProperty("设备Id")
    private String id;

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
     * 开机率
     */
    @ApiModelProperty("开机率")
    private double operationRate;

    /**
     * 维护时长
     */
    @ApiModelProperty("维护时长")
    private double maintenanceDuration;

    /**
     * 开机时长
     */
    @ApiModelProperty("开机时长")
    private double startingUpDuration;

    /**
     * 停机时长
     */
    @ApiModelProperty("停机时长")
    private double shutdownDuration;

    /**
     * 设备班次时长
     */
    @ApiModelProperty("设备班次时长")
    private double shiftDuration;

    /**
     * 产能效率
     */
    @ApiModelProperty("产能效率")
    private double capacityEfficiency;

    /**
     * 良品率
     */
    @ApiModelProperty("良品率")
    private double qualityRate;

    /**
     * 次品数
     */
    @ApiModelProperty("次品数")
    private double inQualityNum;

    /**
     * 产出数
     */
    @ApiModelProperty("产出数")
    private double output;

    /**
     * oee
     */
    @ApiModelProperty("oee")
    private double oee;
}
