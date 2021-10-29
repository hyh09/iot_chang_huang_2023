package org.thingsboard.server.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
     * 设备图片
     */
    @ApiModelProperty("设备图片")
    private String image;

    /**
     * 是否在线
     */
    @ApiModelProperty("是否在线")
    private Boolean isOnLine;
}
