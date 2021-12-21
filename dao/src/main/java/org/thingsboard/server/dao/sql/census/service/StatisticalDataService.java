package org.thingsboard.server.dao.sql.census.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.vo.enums.KeyTitleEnums;
import org.thingsboard.server.dao.sql.census.dao.StatisticalDataDao;
import org.thingsboard.server.dao.sql.census.entity.StatisticalDataEntity;
import org.thingsboard.server.dao.util.BeanToMap;
import org.thingsboard.server.dao.util.StringUtilToll;
import org.thingsboard.server.dao.util.sql.jpa.BaseSQLServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
/**	
  创建时间: 2021-12-21 11:26:27	
  创建人: HU.YUNHUI	
  描述: 【当天的产能能耗的增量数据和当天历史数据】 对应的service	
*/	
@Service	
public class StatisticalDataService  extends BaseSQLServiceImpl<StatisticalDataEntity, UUID, StatisticalDataDao> {	
	
  	protected Logger logger = LoggerFactory.getLogger(this.getClass());


  	private  final  String ZERO="0";


    @Transactional
    public  StatisticalDataEntity  todayDataProcessing(EntityId entityId, TsKvEntry tsKvEntry,String  title)
    {
        logger.info("打印的数据:{}",tsKvEntry);
        StatisticalDataEntity  entityDatabase = this.queryTodayByEntityId(entityId.getId());
        if(entityDatabase == null){
            StatisticalDataEntity   entityNew = setEntityProperOnSave( entityId,tsKvEntry,title);
               return   this.save(entityNew);
        }else {
            StatisticalDataEntity   entityNew = setEntityProper(entityDatabase,tsKvEntry,title);
             return updateRecord(entityNew);

        }
    }
	
    /**	
     *根据实体保存	
     * @param statisticalData	
     * @return StatisticalDataEntity	
     */	
    @Transactional	
    public StatisticalDataEntity save(StatisticalDataEntity statisticalData){
        LocalDate date = LocalDate.now();
        statisticalData.setDate(date);
        return super.save(statisticalData);	
    }	
	
   /**	
    * 根据实体类的查询	
    * @param statisticalData  实体对象	
    * @return List<StatisticalDataEntity> list对象	
    * @throws Exception	
    */	
  public  List<StatisticalDataEntity> findAllByStatisticalDataEntity(StatisticalDataEntity statisticalData) throws Exception {	
            List<StatisticalDataEntity> statisticalDatalist = findAll( BeanToMap.beanToMapByJackson(statisticalData));	
            return  statisticalDatalist;	
   }	
	
    /**	
      *根据实体更新	
      * @param statisticalData	
      * @return StatisticalDataEntity	
      */
      @Transactional
      public StatisticalDataEntity updateRecord(StatisticalDataEntity statisticalData)    {
//
//            if (statisticalData.getId() == null) {
//          throw new ThingsboardException("Requested id wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);
//             }
            return this.updateNonNull(statisticalData.getId(), statisticalData);	
        }


      public  StatisticalDataEntity  queryTodayByEntityId(UUID entityId)
      {
          LocalDate date = LocalDate.now();
        return   this.dao.queryAllByEntityIdAndDate(entityId,date);

      }


    /**
     * 更新的设置
     * @param entityDatabase
     * @param tsKvEntry
     * @param title
     * @return
     */
      private   StatisticalDataEntity     setEntityProper  (StatisticalDataEntity  entityDatabase,TsKvEntry tsKvEntry,String  title){
          StatisticalDataEntity  entityNew = new  StatisticalDataEntity();
          entityNew.setEntityId(entityDatabase.getEntityId());
          entityNew.setId(entityDatabase.getId());
          if(title.equals(KeyTitleEnums.key_capacity.getgName()))
          {
              String  capOld = entityDatabase.getElectricFirstValue();//要取今天第一条
              if(capOld == null)
              {
                  entityNew.setElectricFirstValue(tsKvEntry.getValue().toString());
              }

              String  capNow= tsKvEntry.getValue().toString();
              String capValue =StringUtilToll.sub(capNow,capOld);
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
    private   StatisticalDataEntity     setEntityProperOnSave  (EntityId entityId,TsKvEntry tsKvEntry,String  title){
        StatisticalDataEntity  entityNew = new  StatisticalDataEntity();
        entityNew.setEntityId(entityId.getId());
        if(title.equals(KeyTitleEnums.key_capacity.getgName()))
        {
            entityNew.setCapacityFirstValue(tsKvEntry.getValue().toString());
            entityNew.setCapacityAddedValue(ZERO);
            entityNew.setCapacityValue(tsKvEntry.getValue().toString());
        }
        if(title.equals(KeyTitleEnums.key_cable.getgName()))
        {
            entityNew.setElectricFirstValue(tsKvEntry.getValue().toString());
            entityNew.setElectricAddedValue(ZERO);
            entityNew.setElectricValue(tsKvEntry.getValue().toString());
        }
        //气
        if(title.equals(KeyTitleEnums.key_gas.getgName()))
        {
            entityNew.setGasFirstValue(tsKvEntry.getValue().toString());
            entityNew.setGasAddedValue(ZERO);
            entityNew.setGasValue(tsKvEntry.getValue().toString());
        }

        //水
        if(title.equals(KeyTitleEnums.key_water.getgName()))
        {
            entityNew.setWaterFirstValue(tsKvEntry.getValue().toString());
            entityNew.setWaterAddedValue(ZERO);
            entityNew.setWaterValue(tsKvEntry.getValue().toString());
        }


        return  entityNew;
    }


}	
