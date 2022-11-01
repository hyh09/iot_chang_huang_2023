package org.thingsboard.server.dao.kanban.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.thingsboard.server.dao.kanban.vo.inside.ComponentDataDTO;

import java.util.List;
import java.util.UUID;


/**
 * @Project Name: thingsboard
 * @File Name: KanbanDeviceVo
 * @Date: 2022/11/1 9:48
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@ToString
@ApiModel(value = "设备的信息看板")
public class KanbanDeviceVo {

    /**
     *
     */
    @ApiModelProperty(value = "设备id")
    private String  deviceId;

    /**
     * 设备名
     */
    @ApiModelProperty(value = "设备名")
    @JsonProperty("Name")
    private String name;
    /**
     * 今日耗水量
     */
    @ApiModelProperty(value = "今日耗水量")
    @JsonProperty("ConsumptionToday_Water")
    private String consumptiontodayWater;
    /**
     * 今日耗电量
     */
    @ApiModelProperty(value = "今日耗电量")
    @JsonProperty("ConsumptionToday_Electricity")
    private String consumptiontodayElectricity;
    /**
     * 今日耗气量
     */
    @ApiModelProperty(value = "今日耗气量")
    @JsonProperty("ConsumptionToday_Gas")
    private String consumptiontodayGas;
    /**
     * 今日产量
     */
    @ApiModelProperty(value = "今日产量")
    @JsonProperty("ProductionToday")
    private String productionToday;
    /**
     * 总产量
     */
    @ApiModelProperty(value = "总产量")
    @JsonProperty("ProductionTotal")
    private String productionTotal;
    /**
     * 今日预警  /api/deviceMonitor/board/alarmRecord/day/statistics
     */
    @ApiModelProperty(value = "今日预警")
    @JsonProperty("AlertToday")
    private String alertToday;
    /**
     * 昨日预警
     */
    @ApiModelProperty(value = "昨日预警")
    @JsonProperty("AlertYesterday")
    private String alertYesterday;
    /**
     * 历史预警
     */
    @ApiModelProperty(value = "历史预警")
    @JsonProperty("AlertHistory")
    private String alertHistory;
    /**
     * 在线状态（0-在线 1-离线）  ///ClientService.isDeviceOnline()
     * /api/deviceMonitor/board/rtMonitor/device  中包含了
     */
    @ApiModelProperty(value = "在线状态（0-在线 1-离线）")
    @JsonProperty("OnlineState")
    private String onlineState;
    /**
     * /零部件数据   /api/deviceMonitor/board/rtMonitor/device/{id}
     */
    @ApiModelProperty(value = "零部件数据")
    @JsonProperty("ComponentData")
    private List<ComponentDataDTO> componentData;

}
