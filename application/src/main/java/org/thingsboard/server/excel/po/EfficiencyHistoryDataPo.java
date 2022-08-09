package org.thingsboard.server.excel.po;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * Project Name: all-in-one-multi-end-code
 * File Name: EfficiencyHistoryDataVo
 * Package Name: org.thingsboard.server.common.data.effciency
 * Date: 2022/7/26 9:37
 * author: wb04
 * 业务中文描述: 能耗历史的返回对象
 * Copyright (c) 2022,All Rights Reserved.
 *
 * @author wb04
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EfficiencyHistoryDataPo implements Serializable {

    @ExcelProperty(value = "设备名称",index = 0)
    private String deviceName;

    @ExcelProperty(value = "耗水量 (T)",index = 1)
    private String water;


    @ExcelProperty(value = "电",index = 2)
    private String electric;


    @ExcelProperty(value = "耗电量 (KWH)",index = 3)
    private String gas;

    @ExcelProperty(value ="耗气量 (T)",index = 4)
    private long createdTime;



}
