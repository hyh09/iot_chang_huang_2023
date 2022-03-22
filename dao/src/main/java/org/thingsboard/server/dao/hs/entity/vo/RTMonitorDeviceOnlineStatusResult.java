package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

/**
 * 实时监控数据-设备在线状态
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "实时监控数据-设备在线状态")
public class RTMonitorDeviceOnlineStatusResult {
    /**
     * 全部设备id列表
     */
    @ApiModelProperty("全部设备id列表")
    private List<UUID> deviceIdList;

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
