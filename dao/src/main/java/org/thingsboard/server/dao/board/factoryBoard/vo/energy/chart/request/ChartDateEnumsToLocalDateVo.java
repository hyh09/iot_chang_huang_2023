package org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

/**
 * @Project Name: thingsboard
 * @File Name: ChartDateEnumsToVo
 * @Date: 2023/2/14 13:51
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChartDateEnumsToLocalDateVo {

    LocalDate beginDate;

    LocalDate endDate;

}
