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
 * app首页数据
 *
 * @author wwj
 * @since 2021.12.3
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "app首页数据")
public class AppIndexResult {
    /**
     * 全部在线设备数量
     */
    @ApiModelProperty("在线设备数量")
    private Integer onLineDeviceCount;

    /**
     * 全部离线设备数量
     */
    @ApiModelProperty("离线设备数量")
    private Integer offLineDeviceCount;

    /**
     * 警报数据结果
     */
    @ApiModelProperty("警报数据结果")
    private AlarmDayResult alarmResult;

    /**
     * 工厂列表数据
     */
    @ApiModelProperty("工厂列表数据")
    List<AppIndexFactoryResult> factoryResultList;
}
