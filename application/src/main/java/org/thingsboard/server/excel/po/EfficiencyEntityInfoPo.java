package org.thingsboard.server.excel.po;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * @Project Name: thingsboard
 * @File Name: EfficiencyEntityInfoPo
 * @Package Name: org.thingsboard.server.excel.po
 * @Date: 2022/8/9 16:08
 * @author: wb04
 * 业务中文描述: 能耗列表的导出对象
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EfficiencyEntityInfoPo implements Serializable {

    @ExcelProperty(value = "设备名称" ,index = 0)
    private  String rename;

    @ExcelProperty(value = "耗水量 (T)" ,index = 1)
    private String waterConsumption;


     @ExcelProperty(value = "耗电量 (KWH)",index = 2)
    private  String electricConsumption;

    @ExcelProperty(value = "耗气量 (T)",index = 3)
    private String  gasConsumption;

    @ExcelProperty(value = "产量(M)",index = 4)
    private String capacityConsumption;

    @ExcelProperty(value = "单位耗水量(T/M)",index = 5)
    private  String unitWaterConsumption;

     @ExcelProperty(value = "单位耗电量(KWH/M)",index = 6)
    private  String unitElectricConsumption;



     @ExcelProperty(value = "单位耗气量(T/M)",index = 7)
    private String unitGasConsumption;

}
