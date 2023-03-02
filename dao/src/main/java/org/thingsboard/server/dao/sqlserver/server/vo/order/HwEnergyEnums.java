package org.thingsboard.server.dao.sqlserver.server.vo.order;

/**
 * @Project Name: thingsboard
 * @File Name: HwEnergyEnums
 * @Date: 2022/12/28 15:30
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
public enum HwEnergyEnums {
    WATER("水"),
    ELECTRICITY("电"),
    GAS("气"),
    ;
    private  String chineseField;

    HwEnergyEnums(String chineseField) {
        this.chineseField = chineseField;
    }

    public String getChineseField() {
        return chineseField;
    }

    public void setChineseField(String chineseField) {
        this.chineseField = chineseField;
    }
}
