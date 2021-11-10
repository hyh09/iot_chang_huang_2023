package org.thingsboard.server.dao.sql.role.service.Imp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.AppDeviceEnergyVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.ResultEnergyAppVo;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.FactoryEntity;
import org.thingsboard.server.dao.model.sql.ProductionLineEntity;
import org.thingsboard.server.dao.model.sql.WorkshopEntity;
import org.thingsboard.server.dao.productionline.ProductionLineDao;
import org.thingsboard.server.dao.sql.factory.FactoryRepository;
import org.thingsboard.server.dao.sql.role.dao.EffectTsKvRepository;
import org.thingsboard.server.dao.sql.role.entity.EffectTsKvEntity;
import org.thingsboard.server.dao.sql.role.service.EfficiencyStatisticsSvc;
import org.thingsboard.server.dao.util.StringUtilToll;
import org.thingsboard.server.dao.workshop.WorkshopDao;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: thingsboard
 * @description: 效能分析接口业务层
 * @author: HU.YUNHUI
 * @create: 2021-11-09 11:16
 **/
@Service
@Slf4j
public class EfficiencyStatisticsImpl implements EfficiencyStatisticsSvc {

    @Autowired  private EffectTsKvRepository effectTsKvRepository;
    @Autowired  private DeviceDao deviceDao;
    @Autowired  private FactoryDao factoryDao;
    @Autowired  private WorkshopDao workshopDao;
    @Autowired  private ProductionLineDao productionLineDao;


    /**
     * app的产能接口
     * @return
     */
    @Override
    public ResultCapAppVo queryCapApp(QueryTsKvVo vo, TenantId tenantId) {
        ResultCapAppVo  resultCapAppVo = new ResultCapAppVo();
        log.info("app的产能分析接口入参:{}",vo);
        /***************暂时写死的 ***/
        if(StringUtils.isNotBlank(vo.getKey()))
        {
            vo.setKey("tmpFailed");//先写死定死
        }
        if(vo.getFactoryId() == null)
        {
            vo.setFactoryId(getFirstFactory(tenantId));
        }
        List<EffectTsKvEntity> effectTsKvEntities = effectTsKvRepository.queryEntity(vo);
        if(CollectionUtils.isEmpty(effectTsKvEntities))
        {
            return   new ResultCapAppVo();
        }
        List<EffectTsKvEntity>  pageList =  effectTsKvEntities.stream().skip((vo.getPage())*vo.getPageSize()).limit(vo.getPageSize()).
                collect(Collectors.toList());
        log.info("当前的分页之后的数据:{}",pageList);
        List<UUID> ids = pageList.stream().map(EffectTsKvEntity::getEntityId).collect(Collectors.toList());
        log.info("当前的分页之后的数据之设备id的汇总:{}",ids);
        List<DeviceEntity>  entities =  deviceDao.queryAllByIds(ids);
        Map<UUID,DeviceEntity> map1 = entities.stream().collect(Collectors.toMap(DeviceEntity::getId,DeviceEntity->DeviceEntity));
        log.info("查询到的设备信息map1:{}",map1);
        List<AppDeviceCapVo> appDeviceCapVoList = new ArrayList<>();
        pageList.stream().forEach(entity->{
            AppDeviceCapVo  capVo = new AppDeviceCapVo();
            DeviceEntity  entity1 = map1.get(entity.getEntityId());
            //会存在为空的
            capVo.setValue(getValueByEntity(entity));
            capVo.setDeviceId(entity.getEntityId().toString());
            capVo.setDeviceName(entity1.getName());

            if(entity1.getWorkshopId() != null) {
                Workshop workshop = workshopDao.findById(tenantId, entity1.getWorkshopId());
                capVo.setWorkshopName(workshop.getName());
            }

            if(entity1.getProductionLineId() != null) {
                ProductionLine productionLine = productionLineDao.findById(tenantId, entity1.getProductionLineId());
                capVo.setProductionName(productionLine.getName());
            }
            appDeviceCapVoList.add(capVo);

        });
        resultCapAppVo.setTotalValue(getTotalValue(effectTsKvEntities));
        resultCapAppVo.setAppDeviceCapVoList(appDeviceCapVoList);
        return resultCapAppVo;
    }


    /**
     * 能耗的查询
     * @param vo
     * @param tenantId
     * @return
     */
    @Override
    public ResultEnergyAppVo queryEntityByKeys(QueryTsKvVo vo, TenantId tenantId) {
        log.info("查询能耗的入参{}租户的id{}",vo,tenantId);
        ResultEnergyAppVo appVo = new  ResultEnergyAppVo();

        /*********************************   暂时写死的*/
        List<String>  keys1 = new ArrayList<>();
        keys1.add("totalMsgs");
        keys1.add("successfulMsgs");
        keys1.add("failedMsgs");
        vo.setKeys(keys1);

        if(vo.getFactoryId() == null)
        {
            vo.setFactoryId(getFirstFactory(tenantId));
        }
        List<EffectTsKvEntity>  effectTsKvEntities =  effectTsKvRepository.queryEntityByKeys(vo,vo.getKeys());
        log.info("查询到的数据{}",effectTsKvEntities);
        if(CollectionUtils.isEmpty(effectTsKvEntities))
        {
            return appVo;
        }

        List<EffectTsKvEntity>  pageList =  effectTsKvEntities.stream().skip((vo.getPage())*vo.getPageSize()).limit(vo.getPageSize()).
                collect(Collectors.toList());
        log.info("能效当前的分页之后的数据:{}",pageList);
        List<UUID> ids = pageList.stream().map(EffectTsKvEntity::getEntityId).collect(Collectors.toList());
        log.info(" 能效-当前的分页之后的数据之设备id的汇总:{}",ids);
        Map<UUID,List<EffectTsKvEntity>> map = pageList.stream().collect(Collectors.groupingBy(EffectTsKvEntity::getEntityId));

//        Map<UUID,List<EffectTsKvEntity>>  entityMap=   listToMap(effectTsKvEntities);
        log.info("查询到的数据转换为设备维度:{}",map);
        appVo.setAppDeviceCapVoList(getEntityKeyValue(map,tenantId));
        //总的
        appVo.setTotalWaterValue(getTotalValue(effectTsKvEntities,18));
        appVo.setTotalElectricValue(getTotalValue(effectTsKvEntities,19));
        appVo.setTotalAirValue(getTotalValue(effectTsKvEntities,20));

        return appVo;
    }

    /**
     * 获取当前租户的第一个工厂id
     * @param tenantId 当前登录人的租户
     * @return
     */
    public  UUID  getFirstFactory(TenantId  tenantId)
    {
        FactoryEntity  factory = factoryDao.findFactoryByTenantIdFirst(tenantId.getId());
        log.info("查询当前租户{}的第一个工厂{}",tenantId.getId(),factory);
        if(factory == null)
        {
            log.error("查询当前租户{}没有工厂,不能查询效能分析之产能数据");
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"当前租户没有工厂");

        }
        return factory.getId();
    }







    private  String  getValueByEntity(EffectTsKvEntity entity)
    {
        if(entity.getSubtractDouble()>0)
        {
            return  entity.getSubtractDouble().toString();
        }
        if(entity.getSubtractLong()>0)
        {
            return  entity.getSubtractLong().toString();

        }
        return "0";
    }


    private  String getTotalValue(List<EffectTsKvEntity> effectTsKvEntities)
    {

        Double  totalSku =
                effectTsKvEntities.stream().mapToDouble(EffectTsKvEntity::getSubtractDouble).sum();

        Long  totalSku2 =
                effectTsKvEntities.stream().mapToLong(EffectTsKvEntity::getSubtractLong).sum();
        double dvalue =  StringUtilToll.add(totalSku.toString(),totalSku2.toString());
        return dvalue+"";
    }


    private  String getTotalValue(List<EffectTsKvEntity> effectTsKvEntities,int key)
    {

        Double  totalSku =
                effectTsKvEntities.stream().filter(entity -> entity.getKey()== key).mapToDouble(EffectTsKvEntity::getSubtractDouble).sum();

        Long  totalSku2 =
                effectTsKvEntities.stream().filter(entity -> entity.getKey()== key).mapToLong(EffectTsKvEntity::getSubtractLong).sum();
        double dvalue =  StringUtilToll.add(totalSku.toString(),totalSku2.toString());
        return dvalue+"";
    }

 /**
     * 暂时写死的
     * @param listMap
     * @return
     */
    public  List<AppDeviceEnergyVo>  getEntityKeyValue(Map<UUID,List<EffectTsKvEntity>> listMap,TenantId tenantId)
    {
        List<AppDeviceEnergyVo> appList  = new ArrayList<>();

        listMap.forEach((key,value)->{
            AppDeviceEnergyVo appDeviceEnergyVo  = new  AppDeviceEnergyVo();
            appDeviceEnergyVo.setDeviceId(key.toString());
            EffectTsKvEntity  entity1 =value.get(0);
            if(entity1 != null) {
                appDeviceEnergyVo.setDeviceName(entity1.getDeviceName());
                appDeviceEnergyVo.setTime(entity1.getTs2());
                if (entity1.getWorkshopId() != null) {
                    Workshop workshop = workshopDao.findById(tenantId, entity1.getWorkshopId());
                    appDeviceEnergyVo.setWorkshopName(workshop.getName());
                }

                if (entity1.getProductionLineId() != null) {
                    ProductionLine productionLine = productionLineDao.findById(tenantId, entity1.getProductionLineId());
                    appDeviceEnergyVo.setProductionName(productionLine.getName());
                }
                value.stream().forEach(effectTsKvEntity -> {
                    log.info("打印当前的key:"+effectTsKvEntity.getKey()+"effectTsKvEntity.getValue():"+effectTsKvEntity.getValue());
                    //水
                    if (effectTsKvEntity.getKey() == 18) {
                        appDeviceEnergyVo.setWaterValue(effectTsKvEntity.getValue());
                    }
                    //电
                    if (effectTsKvEntity.getKey() == 19) {
                        appDeviceEnergyVo.setElectricValue(effectTsKvEntity.getValue());
                    }
                    //气
                    if (effectTsKvEntity.getKey() == 20) {
                        appDeviceEnergyVo.setAirValue(effectTsKvEntity.getValue());
                    }
                });
            }

            appList.add(appDeviceEnergyVo);
        });

        return  appList;
    }
}
