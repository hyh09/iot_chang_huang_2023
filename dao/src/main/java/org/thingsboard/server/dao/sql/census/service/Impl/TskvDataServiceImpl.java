package org.thingsboard.server.dao.sql.census.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.kafka.vo.DataBodayVo;
import org.thingsboard.server.dao.model.sqlts.ts.TsKvEntity;
import org.thingsboard.server.dao.sql.census.entity.StatisticalDataEntity;
import org.thingsboard.server.dao.sql.census.service.svc.TskvDataServiceSvc;
import org.thingsboard.server.dao.sql.census.vo.StaticalDataVo;
import org.thingsboard.server.dao.sqlts.BaseAbstractSqlTimeseriesDao;
import org.thingsboard.server.dao.sqlts.ts.TsKvRepository;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.dao.util.StringUtilToll;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Project Name: thingsboard
 * File Name: TskvDataServiceImpl
 * Package Name: org.thingsboard.server.dao.sql.census.service.Impl
 * Date: 2022/7/5 16:54
 * author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
@Service
public class TskvDataServiceImpl extends BaseAbstractSqlTimeseriesDao implements TskvDataServiceSvc {
    @Autowired
    private TsKvRepository tsKvRepository;


    @Override
    public String setPreviousByZero(UUID entityId, long time, Integer key) {
        Long longTime = tsKvRepository.findAllMaxTime(entityId, key, time);
        if (longTime == null) {
            return "0";
        }
        TsKvEntity tsKvEntity = tsKvRepository.findAllByTsAndEntityIdAndKey(entityId, key, longTime);
        TsKvEntry entry = tsKvEntity.toData();
        Object o = entry.getValue();
        if (o == null) {
            return "0";
        }
        return o.toString();


    }


    /**
     * @param entityId
     * @param key
     * @param startTime
     * @param endTime
     * @return
     */
    public StaticalDataVo getInterval(UUID entityId, int key, Long startTime, Long endTime, String firstValue) {
        StaticalDataVo dataVo = new StaticalDataVo();
        TsKvEntry tsKvEntityEnd = DaoUtil.getData(tsKvRepository.findAllTodayLastData(entityId, key, startTime, endTime));
        if (tsKvEntityEnd == null) {
            return null;
        }
        dataVo.setLastTime(tsKvEntityEnd.getTs());
        dataVo.setLastValue(tsKvEntityEnd.getValue() != null ? StringUtilToll.roundUp(tsKvEntityEnd.getValueAsString()) : "0");
        if (StringUtilToll.isNotZero(firstValue)) {
            dataVo.setAddValue(StringUtilToll.sub(tsKvEntityEnd.getValueAsString(), firstValue));
            return dataVo;
        }
        TsKvEntry tsKvEntityStart = DaoUtil.getData(tsKvRepository.findAllTodayFirstData(entityId, key, startTime, endTime));
        String firstValue02 = tsKvEntityStart.getValueAsString();
        if (StringUtilToll.isZero(tsKvEntityStart.getValueAsString())) {
            firstValue02 = setPreviousByZero(entityId, tsKvEntityStart.getTs(), key);
        }
        dataVo.setFirstValue(firstValue02);
        dataVo.setFirstTime(tsKvEntityEnd.getTs());
        dataVo.setAddValue(StringUtilToll.sub(tsKvEntityEnd.getValueAsString(), firstValue02));
        return dataVo;


    }


    /**
     * 将水电气产量 转换最后入库对象
     * @param waterVo
     * @param electricVo
     * @param gasVo
     * @param capacitiesVo
     * @return
     */
    @Override
    public StatisticalDataEntity StaticalDataVoToStatisticalDataEntity(StatisticalDataEntity statisticalDataEntity,StaticalDataVo waterVo,
                                                                       StaticalDataVo electricVo,
                                                                       StaticalDataVo gasVo,
                                                                       StaticalDataVo capacitiesVo
                                                                      ) {

        if(statisticalDataEntity.getId() == null)
        {
            statisticalDataEntity.setDate(getLocalDateByVo(waterVo,electricVo,gasVo,capacitiesVo));
        }
        //水
        if(waterVo != null) {
            statisticalDataEntity.setWaterFirstTime(waterVo.getFirstTime());
            statisticalDataEntity.setWaterFirstValue(waterVo.getFirstValue());
            statisticalDataEntity.setWaterLastTime(waterVo.getLastTime());
            statisticalDataEntity.setWaterValue(waterVo.getLastValue());
            statisticalDataEntity.setWaterAddedValue(waterVo.getAddValue());
            statisticalDataEntity.setTs(waterVo.getLastTime());
        }

        //电
        if(electricVo != null) {
            statisticalDataEntity.setElectricFirstTime(electricVo.getFirstTime());
            statisticalDataEntity.setElectricFirstValue(electricVo.getFirstValue());
            statisticalDataEntity.setElectricLastTime(electricVo.getLastTime());
            statisticalDataEntity.setElectricValue(electricVo.getLastValue());
            statisticalDataEntity.setElectricAddedValue(electricVo.getAddValue());
            statisticalDataEntity.setTs(electricVo.getLastTime());
        }

        //气
        if(gasVo != null) {
            statisticalDataEntity.setGasFirstTime(gasVo.getFirstTime());
            statisticalDataEntity.setGasFirstValue(gasVo.getFirstValue());
            statisticalDataEntity.setGasLastTime(gasVo.getLastTime());
            statisticalDataEntity.setGasValue(gasVo.getLastValue());
            statisticalDataEntity.setGasAddedValue(gasVo.getAddValue());
            statisticalDataEntity.setTs(gasVo.getLastTime());
        }

        //产量
        if(capacitiesVo != null) {
            statisticalDataEntity.setCapacityFirstTime(capacitiesVo.getFirstTime());
            statisticalDataEntity.setCapacityFirstValue(capacitiesVo.getFirstValue());
            statisticalDataEntity.setCapacityLastTime(capacitiesVo.getLastTime());
            statisticalDataEntity.setCapacityValue(capacitiesVo.getLastValue());
            statisticalDataEntity.setCapacityAddedValue(capacitiesVo.getAddValue());
            statisticalDataEntity.setTs(capacitiesVo.getLastTime());
        }

        return statisticalDataEntity;
    }


    private LocalDate  getLocalDateByVo(StaticalDataVo waterVo,
                                        StaticalDataVo electricVo,
                                        StaticalDataVo gasVo,
                                        StaticalDataVo capacitiesVo)
    {
        if(StringUtilToll.isNotZero(waterVo.getLastTime()+"") ) {
          return   CommonUtils.getLocalDateByLong(waterVo.getLastTime());
        }
        if(StringUtilToll.isNotZero(electricVo.getLastTime()+"") ) {
            return   CommonUtils.getLocalDateByLong(electricVo.getLastTime());
        }
        if(StringUtilToll.isNotZero(gasVo.getLastTime()+"") ) {
            return   CommonUtils.getLocalDateByLong(gasVo.getLastTime());
        }
        return   CommonUtils.getLocalDateByLong(capacitiesVo.getLastTime());


    }
}
