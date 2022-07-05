package org.thingsboard.server.dao.sql.census.service.svc;


import org.thingsboard.server.dao.sql.census.entity.StatisticalDataEntity;
import org.thingsboard.server.dao.sql.census.vo.StaticalDataVo;

import java.util.UUID;

public interface TskvDataServiceSvc {

    /**
     * 长胜的逻辑
     *    如果遥测值为0,取这个时间之前的不为0 的一条值
     * @param entityId
     * @param time
     * @param key
     * @return
     */
     String  setPreviousByZero(UUID entityId, long time, Integer  key);


    /**
     * 返回时间范围内的数据 （两端时间)
     * @param entityId
     * @param key
     * @param startTime
     * @param endTime
     * @return
     */
    public StaticalDataVo getInterval(UUID entityId, int key, Long startTime, Long endTime, String firstValue);



    /***
     * 水电气产量 4个合并
     * @param statisticalDataEntity 如果更新的就将查询的结果传进来； 如果新增就new StatisticalDataEntit
     * @param waterVo
     * @param electricVo
     * @param gasVo
     * @param capacitiesVo
     * @return
     */
    StatisticalDataEntity  StaticalDataVoToStatisticalDataEntity(StatisticalDataEntity statisticalDataEntity,StaticalDataVo waterVo,StaticalDataVo electricVo,
                                                                 StaticalDataVo gasVo,StaticalDataVo capacitiesVo,Long endTime02);
}
