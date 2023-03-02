package org.thingsboard.server.dao.sql.census.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.vo.enums.KeyTitleEnums;
import org.thingsboard.server.common.data.vo.enums.key.KeyNameEnums;
import org.thingsboard.server.dao.kafka.vo.DataBodayVo;
import org.thingsboard.server.dao.sql.census.dao.StatisticalDataDao;
import org.thingsboard.server.dao.sql.census.entity.StatisticalDataEntity;
import org.thingsboard.server.dao.sql.census.service.svc.TskvDataServiceSvc;
import org.thingsboard.server.dao.sql.census.vo.StaticalDataVo;
import org.thingsboard.server.dao.util.BeanToMap;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.dao.util.StringUtilToll;
import org.thingsboard.server.dao.util.sql.jpa.BaseSQLServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 创建时间: 2021-12-21 11:26:27
 * 创建人: HU.YUNHUI
 * 描述: 【当天的产能能耗的增量数据和当天历史数据】 对应的service
 */
@Service
public class StatisticalDataService extends BaseSQLServiceImpl<StatisticalDataEntity, UUID, StatisticalDataDao> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());


    private final String ZERO = "0";
    @Autowired
    private TskvDataServiceSvc tskvDataServiceSvc;


    /**
     * @param device
     * @param endTime      整点时刻; 延迟5分钟的;
     *                     00点05分 刷新上一天的数据，
     *                     其他时间都是每个1小时刷新当天数据
     * @param mapKeyNameMa
     */
    @Transactional
    public void saveDataTask(Device device, LocalDateTime endTime, Map<String, Integer> mapKeyNameMa) {
        try {
            Long startTime02 = 0L; //开始时间的天维度不存在跨天问题
            Long endTime02 = 0L;
            if (endTime.getHour() == 0) {
                startTime02 = CommonUtils.getTimestampOfDateTime(endTime.minusHours(24)); //统计昨天0点到今天23点59分的59秒或者到今天0点
                endTime02 = CommonUtils.getTimestampOfDateTime(endTime);//CommonUtils.getZero();//今天0点
            } else {
                startTime02 = CommonUtils.getZeroByLocalDateTime(endTime);//今天0点
                endTime02 = CommonUtils.getTimestampOfDateTime(endTime);//统一1小时刷新一; 避免设备多，延迟跨时间问题
            }

            StatisticalDataEntity statisticalDataEntity = this.dao.queryAllByEntityIdAndDate(device.getUuidId(), CommonUtils.longToLocalDate(startTime02));//
            if (statisticalDataEntity == null) {
                StaticalDataVo water = tskvDataServiceSvc.getInterval(device.getUuidId(), mapKeyNameMa.get(KeyNameEnums.water.getCode()), startTime02, endTime02, null);
                StaticalDataVo electric = tskvDataServiceSvc.getInterval(device.getUuidId(), mapKeyNameMa.get(KeyNameEnums.electric.getCode()), startTime02, endTime02, null);
                StaticalDataVo gas = tskvDataServiceSvc.getInterval(device.getUuidId(), mapKeyNameMa.get(KeyNameEnums.gas.getCode()), startTime02, endTime02, null);
                StaticalDataVo capacities = tskvDataServiceSvc.getInterval(device.getUuidId(), mapKeyNameMa.get(KeyNameEnums.capacities.getCode()), startTime02, endTime02, null);
                StatisticalDataEntity saveEntity = tskvDataServiceSvc.StaticalDataVoToStatisticalDataEntity(new StatisticalDataEntity(), water, electric, gas, capacities);
                if (saveEntity.getDate() != null) {
                    saveEntity.setEntityId(device.getUuidId());
                    this.save(saveEntity);
                }
                return;
            } else {
                StaticalDataVo water = tskvDataServiceSvc.getInterval(device.getUuidId(), mapKeyNameMa.get(KeyNameEnums.water.getCode()), startTime02, endTime02, statisticalDataEntity.getWaterFirstValue());
                StaticalDataVo electric = tskvDataServiceSvc.getInterval(device.getUuidId(), mapKeyNameMa.get(KeyNameEnums.electric.getCode()), startTime02, endTime02, statisticalDataEntity.getElectricFirstValue());
                StaticalDataVo gas = tskvDataServiceSvc.getInterval(device.getUuidId(), mapKeyNameMa.get(KeyNameEnums.gas.getCode()), startTime02, endTime02, statisticalDataEntity.getGasFirstValue());
                StaticalDataVo capacities = tskvDataServiceSvc.getInterval(device.getUuidId(), mapKeyNameMa.get(KeyNameEnums.capacities.getCode()), startTime02, endTime02, statisticalDataEntity.getCapacityFirstValue());
                StatisticalDataEntity updateEntity = tskvDataServiceSvc.StaticalDataVoToStatisticalDataEntity(statisticalDataEntity, water, electric, gas, capacities);
                updateEntity.setEntityId(device.getUuidId());
                this.dao.updateNonNull(updateEntity.getId(), updateEntity);
                return;
            }
        } catch (Exception e) {
            logger.error("===saveDataTask异常====>{}", e);
        }

    }


    @Transactional
    public StatisticalDataEntity todayDataProcessing(UUID entityId, DataBodayVo tsKvEntry, String title) {
        logger.info("打印的数据todayDataProcessing:{},entityId{}title{}", tsKvEntry, entityId, title);
        StatisticalDataEntity entityDatabase = this.queryTodayByEntityId(entityId, tsKvEntry.getTs());
        if (entityDatabase == null) {
            StatisticalDataEntity entityNew = setEntityProperOnSave(entityId, tsKvEntry, title);
            return this.save(entityNew);
        } else {
            StatisticalDataEntity entityNew = setEntityProper(entityDatabase, tsKvEntry, title);
            return updateRecord(entityNew);

        }
    }

    /**
     * 根据实体保存
     *
     * @param statisticalData
     * @return StatisticalDataEntity
     */
    @Transactional
    public StatisticalDataEntity save(StatisticalDataEntity statisticalData) {
//        LocalDate date = LocalDate.now();
//        statisticalData.setDate(date);
        return super.save(statisticalData);
    }

    /**
     * 根据实体类的查询
     *
     * @param statisticalData 实体对象
     * @return List<StatisticalDataEntity> list对象
     * @throws Exception
     */
    public List<StatisticalDataEntity> findAllByStatisticalDataEntity(StatisticalDataEntity statisticalData) throws Exception {
        List<StatisticalDataEntity> statisticalDatalist = findAll(BeanToMap.beanToMapByJackson(statisticalData));
        return statisticalDatalist;
    }

    /**
     * 根据实体更新
     *
     * @param statisticalData
     * @return StatisticalDataEntity
     */
    @Transactional
    public StatisticalDataEntity updateRecord(StatisticalDataEntity statisticalData) {
//
//            if (statisticalData.getId() == null) {
//          throw new ThingsboardException("Requested id wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);
//             }
        return this.updateNonNull(statisticalData.getId(), statisticalData);
    }


    public StatisticalDataEntity queryTodayByEntityId(UUID entityId, long timestamp) {
//          LocalDate date = LocalDate.now();
        return this.dao.queryAllByEntityIdAndDate(entityId, StringUtilToll.getLocalDateByTimestamp(timestamp));

    }


    /**
     * 更新的设置
     *
     * @param entityDatabase
     * @param tsKvEntry
     * @param title
     * @return
     */
    private StatisticalDataEntity setEntityProper(StatisticalDataEntity entityDatabase, DataBodayVo tsKvEntry, String title) {
        StatisticalDataEntity entityNew = new StatisticalDataEntity();
        entityNew.setEntityId(entityDatabase.getEntityId());
        entityNew.setId(entityDatabase.getId());
        long ts = tsKvEntry.getTs();
        entityNew.setTs(ts);
        if (title.equals(KeyTitleEnums.key_capacity.getgName())) {
            String capOld = entityDatabase.getCapacityFirstValue();//要取今天第一条
            if (capOld == null) {
                entityNew.setCapacityFirstValue(tsKvEntry.getValue().toString());
            }

            Long firstTs = entityDatabase.getCapacityFirstTime();
            if (firstTs == null) {
                entityDatabase.setCapacityFirstTime(ts);
            }
            entityDatabase.setCapacityLastTime(ts);
            String capNow = tsKvEntry.getValue().toString();
            String capValue = StringUtilToll.sub(capNow, capOld);
            entityNew.setCapacityAddedValue(capValue);
            entityNew.setCapacityValue(tsKvEntry.getValue().toString());
        }
        if (title.equals(KeyTitleEnums.key_cable.getgName())) {
            String electricOld = entityDatabase.getElectricFirstValue();
            if (electricOld == null) {
                entityNew.setElectricFirstValue(tsKvEntry.getValue().toString());
            }

            Long firstTs = entityDatabase.getElectricFirstTime();
            if (firstTs == null) {
                entityDatabase.setElectricFirstTime(ts);
            }
            entityDatabase.setElectricLastTime(ts);

            String valueNew = tsKvEntry.getValue().toString();
            String subValue = StringUtilToll.sub(valueNew, electricOld);
            entityNew.setElectricAddedValue(subValue);
            entityNew.setElectricValue(tsKvEntry.getValue().toString());
        }
        //气
        if (title.equals(KeyTitleEnums.key_gas.getgName())) {
            String gasOld = entityDatabase.getGasFirstValue();
            if (gasOld == null) {
                entityNew.setGasFirstValue(tsKvEntry.getValue().toString());
            }

            Long firstTs = entityDatabase.getGasFirstTime();
            if (firstTs == null) {
                entityDatabase.setGasFirstTime(ts);
            }
            entityDatabase.setGasLastTime(ts);

            String valueNew = tsKvEntry.getValue().toString();
            String subValue = StringUtilToll.sub(valueNew, gasOld);
            entityNew.setGasAddedValue(subValue);
            entityNew.setGasValue(tsKvEntry.getValue().toString());
        }

        //水
        if (title.equals(KeyTitleEnums.key_water.getgName())) {
            String waterOld = entityDatabase.getWaterFirstValue();
            if (waterOld == null) {
                entityNew.setWaterFirstValue(tsKvEntry.getValue().toString());
            }

            Long firstTs = entityDatabase.getWaterFirstTime();
            if (firstTs == null) {
                entityDatabase.setWaterFirstTime(ts);
            }
            entityDatabase.setWaterLastTime(ts);

            String valueNew = tsKvEntry.getValue().toString();
            String subValue = StringUtilToll.sub(valueNew, waterOld);
            entityNew.setWaterAddedValue(subValue);
            entityNew.setWaterValue(tsKvEntry.getValue().toString());
        }


        return entityNew;
    }


    /**
     * 保存的时候设置
     *
     * @param entityId
     * @param tsKvEntry
     * @param title
     * @return
     */
    private StatisticalDataEntity setEntityProperOnSave(UUID entityId, DataBodayVo tsKvEntry, String title) {
        StatisticalDataEntity entityNew = new StatisticalDataEntity();
        entityNew.setEntityId(entityId);
        entityNew.setTs(tsKvEntry.getTs());
        if (title.equals(KeyTitleEnums.key_capacity.getgName())) {
            entityNew.setCapacityFirstValue(tsKvEntry.getValue().toString());
            entityNew.setCapacityFirstTime(tsKvEntry.getTs());
            entityNew.setCapacityLastTime(tsKvEntry.getTs());
            entityNew.setCapacityAddedValue(ZERO);
            entityNew.setCapacityValue(tsKvEntry.getValue().toString());
        }
        if (title.equals(KeyTitleEnums.key_cable.getgName())) {
            entityNew.setElectricFirstValue(tsKvEntry.getValue().toString());
            entityNew.setElectricFirstTime(tsKvEntry.getTs());
            entityNew.setElectricLastTime(tsKvEntry.getTs());
            entityNew.setElectricAddedValue(ZERO);
            entityNew.setElectricValue(tsKvEntry.getValue().toString());
        }
        //气
        if (title.equals(KeyTitleEnums.key_gas.getgName())) {
            entityNew.setGasFirstValue(tsKvEntry.getValue().toString());
            entityNew.setGasFirstTime(tsKvEntry.getTs());
            entityNew.setGasLastTime(tsKvEntry.getTs());
            entityNew.setGasAddedValue(ZERO);
            entityNew.setGasValue(tsKvEntry.getValue().toString());
        }

        //水
        if (title.equals(KeyTitleEnums.key_water.getgName())) {
            entityNew.setWaterFirstValue(tsKvEntry.getValue().toString());
            entityNew.setWaterFirstTime(tsKvEntry.getTs());
            entityNew.setWaterLastTime(tsKvEntry.getTs());
            entityNew.setWaterAddedValue(ZERO);
            entityNew.setWaterValue(tsKvEntry.getValue().toString());
        }


        return entityNew;
    }


}
