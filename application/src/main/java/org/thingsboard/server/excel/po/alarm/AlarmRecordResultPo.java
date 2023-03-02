package org.thingsboard.server.excel.po.alarm;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * @Project Name: thingsboard
 * @File Name: AlarmRecordResultPo
 * @Date: 2022/8/16 13:01
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AlarmRecordResultPo implements Serializable {



    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间" ,index = 0)
    private String createdTime;

    /**
     * 设备名称
     */
    @ExcelProperty(value = "设备名称" ,index = 1)
    private String rename;

    /**
     * 报警标题
     */
    @ExcelProperty(value = "报警标题" ,index = 2)
    private String title;

    /**


    /**
     * 报警信息
     */
    @ExcelProperty(value = "报警信息",index = 3)
    private String info;

//    /**
//     * 状态
//     */
//    @ExcelProperty(value = "状态",index = 3)
//    private AlarmSimpleStatus status;

    /**
     * 状态显示值
     */
    @ExcelProperty(value = "状态",index = 4)
    private String statusStr;

//    /**
//     * 级别
//     */
//    @ApiModelProperty("级别")
//    private AlarmSimpleLevel level;

    /**
     * 级别显示值
     */
    @ExcelProperty(value = "严重程度",index = 5)
    private String levelStr;



}
