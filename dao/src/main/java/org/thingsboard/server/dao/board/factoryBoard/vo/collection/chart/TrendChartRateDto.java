package org.thingsboard.server.dao.board.factoryBoard.vo.collection.chart;

import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.util.decimal.DateLocaDateAndTimeUtil;

import java.time.LocalDate;

/**
 * @Project Name: thingsboard
 * @File Name: TrendChartRateDto
 * @Date: 2023/2/14 14:35
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
public class TrendChartRateDto {

    private LocalDate bdate;

    private String bootTime;


    public String getdateStr() {
        return DateLocaDateAndTimeUtil.formatDate(this.bdate);
    }


}
