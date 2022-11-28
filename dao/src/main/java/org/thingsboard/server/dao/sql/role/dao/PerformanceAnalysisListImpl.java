package org.thingsboard.server.dao.sql.role.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.vo.QueryEnergyVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.enums.key.KeyNameEnums;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;
import org.thingsboard.server.dao.sql.role.service.Imp.TsKvDeviceRepository;
import org.thingsboard.server.dao.sqlts.BaseAbstractSqlTimeseriesDao;
import org.thingsboard.server.dao.timeseries.TimeseriesService;

import javax.persistence.ColumnResult;
import javax.print.DocFlavor;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Project Name: thingsboard
 * @File Name: PerformanceAnalysisListImpl
 * @Date: 2022/11/7 10:27
 * @author: wb04
 * 业务中文描述: 效能分析列表接口
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
@Service
public class PerformanceAnalysisListImpl extends JpaSqlTool implements PerformanceAnalysisListSvc {

    @Autowired
    private FindKeyIdByKeyNameSvc findKeyIdByKeyNameSvc;
    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private TsKvDeviceRepository tsKvDeviceRepository;

    /**
     * KeyNameEnums
     *
     * @param queryTsKvVo
     * @return
     */
    @Override
    public List<EnergyEffciencyNewEntity> yieldList(QueryTsKvVo queryTsKvVo) {
        return getAddValue(queryTsKvVo, KeyNameEnums.capacities.getCode());
//        Integer keyId = findKeyIdByKeyNameSvc.getKeyIdByKeyName(KeyNameEnums.capacities.getCode());
//        Map<String, Object> param = new HashMap<>();
//        String sql = buildSql(queryTsKvVo, param, keyId);
//        List<EnergyEffciencyNewEntity> querySqlList = querySql(sql, param, "energyEffciencyNewEntity_01");
//        return querySqlList;
    }

    @Override
    public List<EnergyEffciencyNewEntity> queryEnergyListAll(QueryTsKvVo queryTsKvVo) {
        List<EnergyEffciencyNewEntity> capacities = new ArrayList<>();
        List<EnergyEffciencyNewEntity> water = new ArrayList<>();
        List<EnergyEffciencyNewEntity> electric = new ArrayList<>();
        List<EnergyEffciencyNewEntity> gas = new ArrayList<>();
        CompletableFuture.allOf(
                CompletableFuture.supplyAsync(() -> capacities.addAll(getAddValue(queryTsKvVo, KeyNameEnums.capacities.getCode()))),
                CompletableFuture.supplyAsync(() -> water.addAll(getAddValue(queryTsKvVo, KeyNameEnums.water.getCode()))),
                CompletableFuture.supplyAsync(() -> electric.addAll(getAddValue(queryTsKvVo, KeyNameEnums.electric.getCode()))),
                CompletableFuture.supplyAsync(() -> gas.addAll(getAddValue(queryTsKvVo, KeyNameEnums.gas.getCode())))

        ).join();

        if (CollectionUtils.isNotEmpty(capacities)) {
            Map<UUID, String> waterMap = water.stream().collect(Collectors.toMap(EnergyEffciencyNewEntity::getEntityId, EnergyEffciencyNewEntity::getCapacityAddedValue));
            Map<UUID, String> electricMap = electric.stream().collect(Collectors.toMap(EnergyEffciencyNewEntity::getEntityId, EnergyEffciencyNewEntity::getCapacityAddedValue));
            Map<UUID, String> gasMap = gas.stream().collect(Collectors.toMap(EnergyEffciencyNewEntity::getEntityId, EnergyEffciencyNewEntity::getCapacityAddedValue));
            capacities.stream().forEach(entity -> {
                entity.setWaterAddedValue(waterMap.get(entity.getEntityId()));
                entity.setElectricAddedValue(electricMap.get(entity.getEntityId()));
                entity.setGasAddedValue(gasMap.get(entity.getEntityId()));
            });

        }
        return capacities;
    }

    @Override
    public List<EnergyEffciencyNewEntity> getAddValue(QueryTsKvVo queryTsKvVo, String keyName) {
        List<EnergyEffciencyNewEntity> energyOfAllKeysList = new ArrayList<>();//

        Map<UUID, String> mapValue = new HashMap<>();
        //1.先查询出所有的设备
        List<DeviceEntity> deviceEntityList = findDeviceByCondition(queryTsKvVo);
        if (CollectionUtils.isEmpty(deviceEntityList)) {
            return new ArrayList<>();
        }
        Integer keyId = findKeyIdByKeyNameSvc.getKeyIdByKeyName(keyName);
        List<CompletableFuture> completableFutures = new ArrayList();
        deviceEntityList.stream().forEach(e1 -> {
            completableFutures.add(CompletableFuture.supplyAsync(() -> {
                String value = tsKvDeviceRepository.queryData(e1.getId(), queryTsKvVo.getStartTime(), queryTsKvVo.getEndTime(), keyId);
                mapValue.put(e1.getId(), value);
                return value;
            }));
        });

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).join();


        return deviceEntityList.stream().map(d1 -> {
            EnergyEffciencyNewEntity entity = new EnergyEffciencyNewEntity();
            entity.setEntityId(d1.getId());
            entity.setFactoryId(d1.getFactoryId());
            entity.setDeviceName(d1.getRename());
            entity.setWorkshopId(d1.getWorkshopId());
            String value = mapValue.get(d1.getId());
            entity.setCapacityAddedValue(StringUtils.isEmpty(value) ? "0" : value);
            return entity;
        }).collect(Collectors.toList());
    }

    /**
     * @return
     */
    private List<DeviceEntity> findDeviceByCondition(QueryTsKvVo queryTsKvVo) {
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setId(queryTsKvVo.getDeviceId());
        deviceEntity.setProductionLineId(queryTsKvVo.getProductionLineId());
        deviceEntity.setWorkshopId(queryTsKvVo.getWorkshopId());
        deviceEntity.setFactoryId(queryTsKvVo.getFactoryId());
        deviceEntity.setTenantId(queryTsKvVo.getTenantId());
        List<DeviceEntity> deviceEntities = deviceDao.findAllByEntity(deviceEntity);
        return deviceEntities;
    }


    private String buildSql(UUID id, Long startTime, Long endTime, Map<String, Object> param, Integer keyId) {
        StringBuffer TABLE_01_SQL = new StringBuffer();
        TABLE_01_SQL.append(" with   table01 as (  select entity_id,min(ts) startTs,max(ts) endTs,max(\"key\") as keyId from  ts_kv tk   where  tk.entity_id  =:entityId ");
//        TABLE_01_SQL.append("  select id  from  device d1 where 1=1  ");
//        sqlPartOnDevice(queryTsKvVo, TABLE_01_SQL, param);
//        TABLE_01_SQL.append(" ) ");
        TABLE_01_SQL.append("  and ts>=:startTime and ts<=:endTime and key =:keyId and  concat(long_v,dbl_v,str_v,json_v)<>'0'  group by entity_id ) ");
        TABLE_01_SQL.append(" SELECT  d1.id as entity_id ,d1.rename as deviceName,d1.picture, d1.dict_device_id as dictDeviceId ");
        TABLE_01_SQL.append(" ,d1.factory_id as factoryId,d1.workshop_id as workshopId,d1.production_line_id as productionLineId,");
        TABLE_01_SQL.append("   ( to_number((select  concat(long_v,dbl_v,str_v,json_v) from  ts_kv tk01  where  tk01.entity_id=t01.entity_id  and tk01.\"key\" = t01.keyId and  tk01.ts=t01.endTs ),\n" +
                "    '99999999999999999999999999999999.9999') \n" +
                "    -\n" +
                "    to_number((select  concat(long_v,dbl_v,str_v,json_v) from  ts_kv tk01  where  tk01.entity_id=t01.entity_id  and tk01.\"key\" = t01.keyId and  tk01.ts=t01.startTs ),\n" +
                "    '99999999999999999999999999999999.9999') \n" +
                "  ) as capacity_added_value ");
        TABLE_01_SQL.append(" from device d1 left join  table01 t01 on d1.id = t01.entity_id where 1=1  and  d1.id = :entityId  ");
//        sqlPartOnDevice(queryTsKvVo, TABLE_01_SQL, param);
        param.put("entityId", id);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("keyId", keyId);
        return TABLE_01_SQL.toString();
    }


}
