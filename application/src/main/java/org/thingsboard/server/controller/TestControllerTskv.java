package org.thingsboard.server.controller;;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.vo.DeviceCapacityVo;
import org.thingsboard.server.common.data.vo.enums.key.KeyNameEnums;
import org.thingsboard.server.controller.test.vo.TestSaveVo;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sqlts.dictionary.TsKvDictionary;
import org.thingsboard.server.dao.model.sqlts.ts.TsKvEntity;
import org.thingsboard.server.dao.sql.census.service.StatisticalDataService;
import org.thingsboard.server.dao.sql.role.dao.BoardTrendChartRepositoryNewMethon;
import org.thingsboard.server.dao.sql.tskv.entity.EnergyHistoryHourEntity;
import org.thingsboard.server.dao.sql.tskv.service.EnergyHistoryHourService;
import org.thingsboard.server.dao.sqlts.dictionary.TsKvDictionaryRepository;
import org.thingsboard.server.dao.sqlts.ts.TsKvRepository;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Project Name: thingsboard
 * File Name: TestControllerTskv
 * Package Name: org.thingsboard.server.controller.test
 * Date: 2022/7/5 13:39
 * author: wb04
 * 业务中文描述:  手动补历史时间点数据
 * Copyright (c) 2022,All Rights Reserved.
 */
@Api(value = "测试数据", tags = {"手动补历史时间点数据"})
@Slf4j
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/test/tsky")
public class TestControllerTskv  extends BaseController {

    @Autowired private DeviceDao deviceDao;
    @Autowired  private   DeviceService deviceService;
    @Autowired private  EnergyHistoryHourService energyHistoryHourService;
    @Autowired private  StatisticalDataService statisticalDataService;
    @Autowired  private  TsKvDictionaryRepository tsKvDictionaryRepository;
    @Autowired private  BoardTrendChartRepositoryNewMethon boardTrendChartRepositoryNewMethon;


    @PostMapping("/test001")
    public  String testSave(@RequestBody TestSaveVo vo)
    {
        log.info("当前执行的时间:{}", vo.getLocalDateTime());
        LocalDateTime hourEndTime =  vo.getLocalDateTime().withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startHourTime= hourEndTime.minusHours(1);//一小时整点时间
        log.info("当前统计的维度开始时间:{}结束时间{}",startHourTime,hourEndTime);
        List<Device>  deviceIdLists =  DaoUtil.convertDataList(deviceDao.queryAllByIds(vo.getIds()));

        log.info("查询当前所有租户下的设备共计多少条:{}",deviceIdLists.size());
        if(CollectionUtils.isEmpty(deviceIdLists))
        {
            return "查询不到设备";
        }
        Map<String,Integer>  mapKeyNameMap= keyToKeyIdMap();
        for(Device  device:deviceIdLists) {
            try {
                statisticalDataService.saveDataTask(device, hourEndTime, mapKeyNameMap);
            } catch (Exception e) {
                log.error("异常信息:{}", e);
            }
        }
        return "全部执行完成";

    }



    @PostMapping("/test002")
    public  String testSavehour(@RequestBody TestSaveVo vo)
    {
        log.info("当前执行的时间:{}", vo.getLocalDateTime());
        LocalDateTime hourEndTime =  vo.getLocalDateTime().withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startHourTime= hourEndTime.minusHours(1);//一小时整点时间
        log.info("当前统计的维度开始时间:{}结束时间{}",startHourTime,hourEndTime);
        List<Device>  deviceIdLists =  DaoUtil.convertDataList(deviceDao.queryAllByIds(vo.getIds()));

        log.info("查询当前所有租户下的设备共计多少条:{}",deviceIdLists.size());
        if(CollectionUtils.isEmpty(deviceIdLists))
        {
            return "查询不到设备";
        }
        Map<String,Integer>  mapKeyNameMap= keyToKeyIdMap();
        for(Device  device:deviceIdLists) {
            try {
                energyHistoryHourService.save(getHour(device,startHourTime,hourEndTime,mapKeyNameMap));
            } catch (Exception e) {
                log.error("异常信息:{}", e);
            }
        }
        return "全部执行完成";

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


    private EnergyHistoryHourEntity getHour(Device device, LocalDateTime startTime, LocalDateTime endTime, Map<String,Integer>  mapKeyNameMa)
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


    public    String  getTimeRangeValue(UUID  entityId,LocalDateTime  startTime,LocalDateTime endTime,int keyId)
    {
        DeviceCapacityVo deviceCapacityVo  = new DeviceCapacityVo();
        deviceCapacityVo.setEntityId(entityId);
        deviceCapacityVo.setStartTime(CommonUtils.getTimestampOfDateTime(startTime));
        deviceCapacityVo.setEndTime(CommonUtils.getTimestampOfDateTime(endTime));
        return boardTrendChartRepositoryNewMethon.getCapacityValueByDeviceIdAndInTime(deviceCapacityVo,keyId);
    }



}
