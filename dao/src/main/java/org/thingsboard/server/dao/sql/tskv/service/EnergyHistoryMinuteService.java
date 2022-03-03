package org.thingsboard.server.dao.sql.tskv.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.QueryTsKvHisttoryVo;
import org.thingsboard.server.common.data.vo.enums.KeyTitleEnums;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.sql.role.dao.tool.DataToConversionSvc;
import org.thingsboard.server.dao.sql.tskv.dao.EnergyHistoryMinuteDao;
import org.thingsboard.server.dao.sql.tskv.entity.EnergyHistoryMinuteEntity;
import org.thingsboard.server.dao.sql.tskv.svc.EnergyHistoryMinuteSvc;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.dao.util.StringUtilToll;
import org.thingsboard.server.dao.util.sql.jpa.BaseSQLServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 统计能耗历史的数据表（分钟维度)
 * @author: HU.YUNHUI
 * @create: 2022-01-18 16:12
 **/
@Slf4j
@Service
public class EnergyHistoryMinuteService  extends BaseSQLServiceImpl<EnergyHistoryMinuteEntity, UUID, EnergyHistoryMinuteDao> implements EnergyHistoryMinuteSvc {

    private  final  String ZERO="0";

    @Autowired private DataToConversionSvc dataToConversionSvc;

    @Override
    @Transactional
    public void  saveByMinute(EntityId entityId, TsKvEntry tsKvEntry, String  title) {
        EnergyHistoryMinuteEntity entityDatabase = this.queryTodayByEntityId(entityId.getId(),tsKvEntry.getTs());
        if(entityDatabase == null){
            EnergyHistoryMinuteEntity   entityNew = setEntityProperOnSave( entityId,tsKvEntry,title);
            entityNew.setTs(CommonUtils.getConversionMinutes(tsKvEntry.getTs()));
               this.save(entityNew);
        }else {
            EnergyHistoryMinuteEntity   entityNew = setEntityProper(entityDatabase,tsKvEntry,title);
            entityNew.setTs(CommonUtils.getConversionMinutes(tsKvEntry.getTs()));
              updateRecord(entityNew);
        }

    }

    @Override
    public PageData<EnergyHistoryMinuteEntity> queryByDeviceIdAndTs(QueryTsKvHisttoryVo  vo, PageLink pageLink) {
        Page<EnergyHistoryMinuteEntity>   page = dao.queryByDeviceIdAndTs(vo.getDeviceId(),vo.getStartTime(),vo.getEndTime(), DaoUtil.toPageable(pageLink));
        return new PageData<EnergyHistoryMinuteEntity>(page.getContent(), page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }

    @Override
    public PageData<Map> queryTranslateTitle(QueryTsKvHisttoryVo vo,String deviceName, PageLink pageLink) {
        Page<EnergyHistoryMinuteEntity>   page = dao.queryByDeviceIdAndTs(vo.getDeviceId(),vo.getStartTime(),vo.getEndTime(), DaoUtil.toPageable(pageLink));
        List<Map> mapList =  dataToConversionSvc.resultProcessByEnergyHistoryMinuteEntity(page.getContent(),deviceName);
        return new PageData<Map>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }

    public EnergyHistoryMinuteEntity queryTodayByEntityId(UUID entityId, long timestamp)
    {
        return   this.dao.queryAllByEntityIdAndDate(entityId, CommonUtils.getConversionMinutes(timestamp));
    }


    /**
     *根据实体更新
     * @param energyChart
     * @return EnergyChartEntity
     */
    @Transactional
    public EnergyHistoryMinuteEntity updateRecord(EnergyHistoryMinuteEntity energyChart)  {
        return this.updateNonNull(energyChart.getId(), energyChart);
    }














    /**
     * 更新的设置
     * @param entityDatabase
     * @param tsKvEntry
     * @param title
     * @return
     */
    private EnergyHistoryMinuteEntity setEntityProper  (EnergyHistoryMinuteEntity  entityDatabase, TsKvEntry tsKvEntry, String  title){
        EnergyHistoryMinuteEntity  entityNew = new  EnergyHistoryMinuteEntity();
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
    private   EnergyHistoryMinuteEntity     setEntityProperOnSave  (EntityId entityId, TsKvEntry tsKvEntry, String  title){
        EnergyHistoryMinuteEntity entityNew = new  EnergyHistoryMinuteEntity();
        entityNew.setEntityId(entityId.getId());
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
