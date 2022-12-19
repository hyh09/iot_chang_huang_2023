package org.thingsboard.server.excel.po;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * Project Name: thingsboard
 * File Name: AppDeviceCapPo
 * Package Name: org.thingsboard.server.excel.po
 * Date: 2022/8/8 16:02
 * author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AppDeviceCapPo implements Serializable {
    @ExcelProperty(value = "设备名称" ,index = 0)
    private String rename;

    @ExcelProperty(value = "产量 (M)" ,index = 1)
    private String value;


}
