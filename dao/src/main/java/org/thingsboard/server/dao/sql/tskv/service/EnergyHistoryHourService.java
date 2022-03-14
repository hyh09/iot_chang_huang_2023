package org.thingsboard.server.dao.sql.tskv.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.vo.enums.KeyTitleEnums;
import org.thingsboard.server.dao.kafka.vo.DataBodayVo;
import org.thingsboard.server.dao.sql.role.dao.tool.DataToConversionSvc;
import org.thingsboard.server.dao.sql.tskv.dao.EnergyHistoryHourDao;
import org.thingsboard.server.dao.sql.tskv.entity.EnergyHistoryHourEntity;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.dao.util.StringUtilToll;
import org.thingsboard.server.dao.util.sql.jpa.BaseSQLServiceImpl;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 统计能耗历史的数据表（分钟维度)
 * @author: HU.YUNHUI
 * @create: 2022-01-18 16:12
 **/
@Slf4j
@Service
public class EnergyHistoryHourService extends BaseSQLServiceImpl<EnergyHistoryHourEntity, UUID, EnergyHistoryHourDao> {

    private  final  String ZERO="0";

    @Autowired private DataToConversionSvc dataToConversionSvc;


    @Transactional
    public void  saveByHour(UUID  entityId, DataBodayVo tsKvEntry, String  title) {
        EnergyHistoryHourEntity entityDatabase = this.queryTodayByEntityId(entityId,tsKvEntry.getTs());
        if(entityDatabase == null){
            EnergyHistoryHourEntity   entityNew = setEntityProperOnSave( entityId,tsKvEntry,title);
            entityNew.setTs(CommonUtils.getConversionHours(tsKvEntry.getTs()));
               this.save(entityNew);
        }else {
            EnergyHistoryHourEntity   entityNew = setEntityProper(entityDatabase,tsKvEntry,title);
            entityNew.setTs(CommonUtils.getConversionHours(tsKvEntry.getTs()));
              updateRecord(entityNew);
        }

    }



 

    public EnergyHistoryHourEntity queryTodayByEntityId(UUID entityId, long timestamp)
    {
        return   this.dao.queryAllByEntityIdAndDate(entityId, CommonUtils.getConversionHours(timestamp));
    }


    /**
     *根据实体更新
     * @param energyChart
     * @return EnergyChartEntity
     */
    @Transactional
    public EnergyHistoryHourEntity updateRecord(EnergyHistoryHourEntity energyChart)  {
        return this.updateNonNull(energyChart.getId(), energyChart);
    }














    /**
     * 更新的设置
     * @param entityDatabase
     * @param tsKvEntry
     * @param title
     * @return
     */
    private EnergyHistoryHourEntity setEntityProper  (EnergyHistoryHourEntity  entityDatabase, DataBodayVo tsKvEntry, String  title){
        EnergyHistoryHourEntity  entityNew = new  EnergyHistoryHourEntity();
        entityNew.setEntityId(entityDatabase.getEntityId());
        entityNew.setId(entityDatabase.getId());
        entityNew.setCreatedTime(entityDatabase.getCreatedTime());
        long ts =   tsKvEntry.getTs();
//        entityNew.setTs(ts);
        if(title.equals(KeyTitleEnums.key_capacity.getgName()))
        {
            String  capOld = entityDatabase.getCapacityFirstValue();//要取今天第一条
            if(capOld == null)
            {
                entityNew.setCapacityFirstValue(tsKvEntry.getValue().toString());
            }

            Long firstTs = entityDatabase.getCapacityFirstTime();
            if(firstTs == null  )
            {
                entityDatabase.setCapacityFirstTime(ts);
            }
            entityDatabase.setCapacityLastTime(ts);
            String  capNow= tsKvEntry.getValue().toString();
            String capValue = StringUtilToll.sub(capNow,capOld);
            entityNew.setCapacityAddedValue(capValue);
            entityNew.setCapacityValue(tsKvEntry.getValue().toString());
        }
        if(title.equals(KeyTitleEnums.key_cable.getgName()))
        {
            String  electricOld= entityDatabase.getElectricFirstValue();
            if(electricOld == null)
            {
                entityNew.setElectricFirstValue(tsKvEntry.getValue().toString());
            }

            Long firstTs = entityDatabase.getElectricFirstTime();
            if(firstTs == null  )
            {
                entityDatabase.setElectricFirstTime(ts);
            }
            entityDatabase.setElectricLastTime(ts);

            String  valueNew= tsKvEntry.getValue().toString();
            String subValue =StringUtilToll.sub(valueNew,electricOld);
            entityNew.setElectricAddedValue(subValue);
            entityNew.setElectricValue(tsKvEntry.getValue().toString());
        }
        //气
        if(title.equals(KeyTitleEnums.key_gas.getgName()))
        {
            String  gasOld= entityDatabase.getGasFirstValue();
            if(gasOld == null)
            {
                entityNew.setGasFirstValue(tsKvEntry.getValue().toString());
            }

            Long firstTs = entityDatabase.getGasFirstTime();
            if(firstTs == null  )
            {
                entityDatabase.setGasFirstTime(ts);
            }
            entityDatabase.setGasLastTime(ts);

            String  valueNew= tsKvEntry.getValue().toString();
            String subValue =StringUtilToll.sub(valueNew,gasOld);
            entityNew.setGasAddedValue(subValue);
            entityNew.setGasValue(tsKvEntry.getValue().toString());
        }

        //水
        if(title.equals(KeyTitleEnums.key_water.getgName()))
        {
            String  waterOld= entityDatabase.getWaterFirstValue();
            if(waterOld == null)
            {
                entityNew.setWaterFirstValue(tsKvEntry.getValue().toString());
            }

            Long firstTs = entityDatabase.getWaterFirstTime();
            if(firstTs == null  )
            {
                entityDatabase.setWaterFirstTime(ts);
            }
            entityDatabase.setWaterLastTime(ts);

            String  valueNew= tsKvEntry.getValue().toString();
            String subValue =StringUtilToll.sub(valueNew,waterOld);
            entityNew.setWaterAddedValue(subValue);
            entityNew.setWaterValue(tsKvEntry.getValue().toString());
        }


        return  entityNew;
    }


    /**
     * 保存的时候设置
     * @param entityId
     * @param tsKvEntry
     * @param title
     * @return
     */
    private   EnergyHistoryHourEntity     setEntityProperOnSave  (UUID  entityId, DataBodayVo tsKvEntry, String  title){
        EnergyHistoryHourEntity entityNew = new  EnergyHistoryHourEntity();
        entityNew.setEntityId(entityId);
//        entityNew.setTs(tsKvEntry.getTs());
        if(title.equals(KeyTitleEnums.key_capacity.getgName()))
        {
            entityNew.setCapacityFirstValue(tsKvEntry.getValue().toString());
            entityNew.setCapacityFirstTime(tsKvEntry.getTs());
            entityNew.setCapacityLastTime(tsKvEntry.getTs());
            entityNew.setCapacityAddedValue(ZERO);
            entityNew.setCapacityValue(tsKvEntry.getValue().toString());
        }
        if(title.equals(KeyTitleEnums.key_cable.getgName()))
        {
            entityNew.setElectricFirstValue(tsKvEntry.getValue().toString());
            entityNew.setElectricFirstTime(tsKvEntry.getTs());
            entityNew.setElectricLastTime(tsKvEntry.getTs());
            entityNew.setElectricAddedValue(ZERO);
            entityNew.setElectricValue(tsKvEntry.getValue().toString());
        }
        //气
        if(title.equals(KeyTitleEnums.key_gas.getgName()))
        {
            entityNew.setGasFirstValue(tsKvEntry.getValue().toString());
            entityNew.setGasFirstTime(tsKvEntry.getTs());
            entityNew.setGasLastTime(tsKvEntry.getTs());
            entityNew.setGasAddedValue(ZERO);
            entityNew.setGasValue(tsKvEntry.getValue().toString());
        }

        //水
        if(title.equals(KeyTitleEnums.key_water.getgName()))
        {
            entityNew.setWaterFirstValue(tsKvEntry.getValue().toString());
            entityNew.setWaterFirstTime(tsKvEntry.getTs());
            entityNew.setWaterLastTime(tsKvEntry.getTs());
            entityNew.setWaterAddedValue(ZERO);
            entityNew.setWaterValue(tsKvEntry.getValue().toString());
        }


        return  entityNew;
    }


}
