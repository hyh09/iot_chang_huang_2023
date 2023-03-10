package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.thingsboard.server.common.data.page.PageData;

import java.util.List;

/**
 * 实时监控数据
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "实时监控数据")
public class RTMonitorResult {

    /**
     * 设备列表
     */
    @ApiModelProperty("设备分页列表")
    private PageData<RTMonitorDeviceResult> devicePageData;

    /**
     * 全部设备id列表
     */
    @ApiModelProperty("全部设备id列表")
    private List<String> deviceIdList;

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

    /**
     * 预警次数列表，从远及近
     */
    @ApiModelProperty("预警次数列表，从远及近")
    private List<AlarmTimesResult> alarmTimesList;

}
