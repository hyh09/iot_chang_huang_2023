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
 * 设备在线情况结果
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "设备在线情况结果")
public class DeviceOnlineStatusResult {
    /**
     * 在线设备数量
     */
    @ApiModelProperty("在线设备数量")
    private Integer onLineDeviceCount;

    /**
     * 离线设备数量
     */
    @ApiModelProperty("离线设备数量")
    private Integer offLineDeviceCount;

    /**
     * 设备总数量
     */
    @ApiModelProperty("设备总数量")
    private Integer allDeviceCount;
}
