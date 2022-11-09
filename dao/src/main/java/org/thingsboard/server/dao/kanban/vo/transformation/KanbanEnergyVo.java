package org.thingsboard.server.dao.kanban.vo.transformation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 * @Project Name: thingsboard
 * @File Name: KanbanEnergyVO
 * @Date: 2022/11/1 11:09
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@Builder
@ToString
public class KanbanEnergyVo {

    /**
     *
     */
    private UUID deviceId;


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
}
