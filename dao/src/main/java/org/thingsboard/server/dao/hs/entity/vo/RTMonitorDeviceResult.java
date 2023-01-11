package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 实时监控设备数据
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "实时监控设备数据")
public class RTMonitorDeviceResult {

    /**
     * 设备id
     */
    @ApiModelProperty("设备id")
    private String id;

    /**
     * 设备名称
     */
    @ApiModelProperty("名称")
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
    private String image;

    /**
     * 是否在线
     */
    @ApiModelProperty("是否在线")
    private Boolean isOnLine;

    @ApiModelProperty("开机率")
    private BigDecimal operationRate;

    @ApiModelProperty("当前卡号")
    private String cardNo;

    @ApiModelProperty("产品名称")
    private String materialName;

    @ApiModelProperty("当前班组")
    private String workerGroupName;

    @ApiModelProperty("机台状态")
    private Integer state;
}
