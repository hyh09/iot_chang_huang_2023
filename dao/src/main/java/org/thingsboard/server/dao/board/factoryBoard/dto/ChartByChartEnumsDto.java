package org.thingsboard.server.dao.board.factoryBoard.dto;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @Project Name: long-win-iot
 * @File Name: ChartByChartEnumsDto
 * @Date: 2023/1/5 14:01
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
public class ChartByChartEnumsDto {

    private LocalDateTime localDateTime;

    private String waterValue;

    private String electricValue;

    private String gasValue;
}
