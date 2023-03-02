package org.thingsboard.server.excel.po;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * @Project Name: thingsboard
 * @File Name: CapacityHistoryPo
 * @Package Name: org.thingsboard.server.excel.po
 * @Date: 2022/8/9 13:33
 * @author: wb04
 * 业务中文描述: 历史产量导出对象
 *   注： 长胜和前端界面保持一致下一条减去上一条
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CapacityHistoryPo implements Serializable {
    /**
     * 设备的名称
     */
    @ExcelProperty(value = "设备名称" ,index = 0)
    private String deviceName;

    @ExcelProperty(value = "产量 (M)" ,index = 1)
    private  String value;

    /**
     * 上报时间
     */
    @ExcelProperty(value = "上报时间" ,index = 2)
    private  String  createdTime;
}
