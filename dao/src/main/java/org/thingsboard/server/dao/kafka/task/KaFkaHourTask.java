package org.thingsboard.server.dao.kafka.task;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.vo.DeviceCapacityVo;
import org.thingsboard.server.common.data.vo.bodrd.energy.Input.EnergyHourVo;
import org.thingsboard.server.common.data.vo.enums.key.KeyNameEnums;
import org.thingsboard.server.dao.board.repository.EnergyLargeScreenReposutory;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.dao.kafka.util.FileUtil;
import org.thingsboard.server.dao.model.sqlts.dictionary.TsKvDictionary;
import org.thingsboard.server.dao.sql.census.service.StatisticalDataService;
import org.thingsboard.server.dao.sql.role.dao.BoardTrendChartRepositoryNewMethon;
import org.thingsboard.server.dao.sql.tskv.entity.EnergyHistoryHourEntity;
import org.thingsboard.server.dao.sql.tskv.service.EnergyHistoryHourService;
import org.thingsboard.server.dao.sqlts.dictionary.TsKvDictionaryRepository;
import org.thingsboard.server.dao.util.CommonUtils;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Project Name: thingsboard
 * File Name: KaFkaHourTask
 * Package Name: org.thingsboard.server.dao.kafka.task
 * Date: 2022/7/5 9:49
 * author: wb04
 * 业务中文描述:    水 电 气 产量 每小时的统计
 * Copyright (c) 2022,All Rights Reserved.
 */
@Service
@Slf4j
@AllArgsConstructor
public class KaFkaHourTask {

    private  final DeviceService deviceService;
    private final TsKvDictionaryRepository tsKvDictionaryRepository;
    private final EnergyLargeScreenReposutory energyLargeScreenReposutory;
    private final StatisticalDataService statisticalDataService;
    private final EnergyHistoryHourService energyHistoryHourService;
    private final BoardTrendChartRepositoryNewMethon boardTrendChartRepositoryNewMethon;


    /**
     * 查询全租户下所有设备
     * 按每小时 （当前小时减去一小时前到现在的时间区间的能耗)
     *         1.查询所有设备，排除网关的
     *         2.小时维度: 查询当前时间 整点时间，和前1小时的时间；
     *         3.天维度：
     *         
     */
    @Scheduled(cron = "0 5 0/1 * * ? ")
    public  void runHour()
    {
        log.info("当前执行的时间:{}", LocalDateTime.now());
        LocalDateTime hourEndTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startHourTime= hourEndTime.minusHours(1);//一小时整点时间
        log.info("当前统计的维度开始时间:{}结束时间{}",startHourTime,hourEndTime);
//        FileUtil.writeAppendFile("","当前执行的时间:{}"+LocalDateTime.now());
        List<Device>   deviceIdLists = deviceService.findAllBy();
        log.info("查询当前所有租户下的设备共计多少条:{}",deviceIdLists.size());
        if(CollectionUtils.isEmpty(deviceIdLists))
        {
            return;
        }
        Map<String,Integer>  mapKeyNameMap= keyToKeyIdMap();
        for(Device device:deviceIdLists)
        {
            try {
            energyHistoryHourService.save(getHour(device,startHourTime,hourEndTime,mapKeyNameMap));
                statisticalDataService.saveDataTask(device, hourEndTime, mapKeyNameMap);
            }catch (Exception e)
            {
                log.error("异常信息:{}",e);
            }
        }

    }


    private EnergyHistoryHourEntity  getHour(Device device,LocalDateTime startTime,LocalDateTime endTime, Map<String,Integer>  mapKeyNameMa)
    {
        EnergyHistoryHourEntity energyHistoryHourEntity  = new EnergyHistoryHourEntity();
        energyHistoryHourEntity.setTenantId(device.getTenantId().getId());
        energyHistoryHourEntity.setEntityId(device.getUuidId());
        energyHistoryHourEntity.setWaterAddedValue(getTimeRangeValue(device.getUuidId(),startTime,endTime,mapKeyNameMa.get(KeyNameEnums.water.getCode())));
        energyHistoryHourEntity.setElectricAddedValue(getTimeRangeValue(device.getUuidId(),startTime,endTime,mapKeyNameMa.get(KeyNameEnums.electric.getCode())));
        energyHistoryHourEntity.setGasAddedValue(getTimeRangeValue(device.getUuidId(),startTime,endTime,mapKeyNameMa.get(KeyNameEnums.gas.getCode())));
        energyHistoryHourEntity.setCapacityAddedValue(getTimeRangeValue(device.getUuidId(),startTime,endTime,mapKeyNameMa.get(KeyNameEnums.capacities.getCode())));
        energyHistoryHourEntity.setTs(CommonUtils.getTimestampOfDateTime(endTime));
        return  energyHistoryHourEntity;
    }


    /**
     * 获取当前的key 和
     * @return
     */
    private Map<String,Integer> keyToKeyIdMap()
    {
        List<String>  keyNmaes=  KeyNameEnums.getKeyCodes();
        log.info("查询当前的keyNma:{}",keyNmaes);
        List<TsKvDictionary>  tsKvDictionaries =  tsKvDictionaryRepository.findAllByKeyIn(keyNmaes);
        Map<String,Integer> mapKey=  tsKvDictionaries.stream().collect(Collectors.toMap(TsKvDictionary::getKey, TsKvDictionary::getKeyId));
        return  mapKey;
    }


    public    String  getTimeRangeValue(UUID  entityId,LocalDateTime  startTime,LocalDateTime endTime,int keyId)
    {
        DeviceCapacityVo  deviceCapacityVo  = new DeviceCapacityVo();
        deviceCapacityVo.setEntityId(entityId);
        deviceCapacityVo.setStartTime(CommonUtils.getTimestampOfDateTime(startTime));
        deviceCapacityVo.setEndTime(CommonUtils.getTimestampOfDateTime(endTime));
       return boardTrendChartRepositoryNewMethon.getCapacityValueByDeviceIdAndInTime(deviceCapacityVo,keyId);
    }



}
