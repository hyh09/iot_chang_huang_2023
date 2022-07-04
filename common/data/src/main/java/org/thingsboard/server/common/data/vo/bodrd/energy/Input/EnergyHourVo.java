package org.thingsboard.server.common.data.vo.bodrd.energy.Input;

import lombok.Data;
import org.thingsboard.server.common.data.vo.AbstractDeviceVo;

import java.util.UUID;

/**
 * Project Name: thingsboard
 * File Name: EnergyHourVo
 * Package Name: org.thingsboard.server.common.data.vo.bodrd.energy.Input
 * Date: 2022/6/15 10:23
 * author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
public class EnergyHourVo extends AbstractDeviceVo {

    /**
     * 当前时间（精确到小时维度)
     */
//    private long currentTimeHour;


    private  long startTime;

    private  long endTime;





    private String groupBy;

    public EnergyHourVo() {
    }


    public EnergyHourVo(UUID factoryId,UUID  productionLineId,UUID workshopId,UUID id) {
        this.factoryId = factoryId;
        this.workshopId=workshopId;
        this.productionLineId=productionLineId;
        this.deviceId=id;
    }
}
