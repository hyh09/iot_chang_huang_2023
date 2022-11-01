package org.thingsboard.server.dao.kanban.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@Data
public class KanbanDeviceVo {

    /**
     *
     */
    private String  deviceId;

    /**
     * 设备名
     */
    @JsonProperty("Name")
    private String name;
    /**
     * 今日耗水量
     */
    @JsonProperty("ConsumptionToday_Water")
    private String consumptiontodayWater;
    /**
     * 今日耗电量
     */
    @JsonProperty("ConsumptionToday_Electricity")
    private String consumptiontodayElectricity;
    /**
     * 今日耗气量
     */
    @JsonProperty("ConsumptionToday_Gas")
    private String consumptiontodayGas;
    /**
     * 今日产量
     */
    @JsonProperty("ProductionToday")
    private String productionToday;
    /**
     * 总产量
     */
    @JsonProperty("ProductionTotal")
    private String productionTotal;
    /**
     * 今日预警  /api/deviceMonitor/board/alarmRecord/day/statistics
     */
    @JsonProperty("AlertToday")
    private String alertToday;
    /**
     * 昨日预警
     */
    @JsonProperty("AlertYesterday")
    private String alertYesterday;
    /**
     * 历史预警
     */
    @JsonProperty("AlertHistory")
    private String alertHistory;
    /**
     * 在线状态（0-在线 1-离线）  ///ClientService.isDeviceOnline()
     * /api/deviceMonitor/board/rtMonitor/device  中包含了
     */
    @JsonProperty("OnlineState")
    private String onlineState;
    /**
     * /零部件数据   /api/deviceMonitor/board/rtMonitor/device/{id}
     */
    @JsonProperty("ComponentData")
    private List<ComponentDataDTO> componentData;

}
