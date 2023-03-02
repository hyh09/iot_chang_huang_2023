package org.thingsboard.server.dao.board.factoryBoard.vo.collection.collectionVolume;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.ChartDataVo;

import java.util.List;

/**
 * @Project Name: thingsboard
 * @File Name: HourlyTrendGraphOfCollectionVolumeVo
 * @Date: 2023/2/8 11:34
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HourlyTrendGraphOfCollectionVolumeVo {
    /**
     * 今天的维度
     */
    private List<ChartDataVo> todayLine;

    /**
     * 昨日的维度
     */
    private List<ChartDataVo> yesterdayLine;


}
