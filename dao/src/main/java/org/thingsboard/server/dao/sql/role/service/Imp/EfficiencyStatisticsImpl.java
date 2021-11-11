package org.thingsboard.server.dao.sql.role.service.Imp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.QueryRunningStatusVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.device.DictDeviceDataVo;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.common.data.vo.enums.EfficiencyEnums;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.common.data.vo.resultvo.devicerun.ResultRunStatusByDeviceVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.AppDeviceEnergyVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.ResultEnergyAppVo;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.FactoryEntity;
import org.thingsboard.server.dao.model.sql.ProductionLineEntity;
import org.thingsboard.server.dao.model.sql.WorkshopEntity;
import org.thingsboard.server.dao.model.sqlts.dictionary.TsKvDictionary;
import org.thingsboard.server.dao.model.sqlts.ts.TsKvEntity;
import org.thingsboard.server.dao.sql.device.DeviceRepository;
import org.thingsboard.server.dao.sql.productionline.ProductionLineRepository;
import org.thingsboard.server.dao.sql.role.dao.EffectTsKvRepository;
import org.thingsboard.server.dao.sql.role.entity.EffectTsKvEntity;
import org.thingsboard.server.dao.sql.role.service.EfficiencyStatisticsSvc;
import org.thingsboard.server.dao.sql.workshop.WorkshopRepository;
import org.thingsboard.server.dao.sqlts.dictionary.TsKvDictionaryRepository;
import org.thingsboard.server.dao.sqlts.ts.TsKvRepository;
import org.thingsboard.server.dao.util.StringUtilToll;

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

    @Autowired private EffectTsKvRepository effectTsKvRepository;
    @Autowired  private DeviceRepository deviceRepository;
    @Autowired private FactoryDao factoryDao;
    @Autowired private WorkshopRepository workshopRepository;
    @Autowired private ProductionLineRepository productionLineRepository;
    @Autowired private TsKvRepository tsKvRepository;
    @Autowired private DictDeviceService dictDeviceService;
    @Autowired private TsKvDictionaryRepository dictionaryRepository;


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
           List<String>  nameKey=  dictDeviceService.findAllByName(null, EfficiencyEnums.CAPACITY_001.getgName());
           String keyName=  nameKey.get(0);
           log.info("打印的产能key:{}",keyName);
            vo.setKey(keyName);
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
//        List<UUID> ids = pageList.stream().map(EffectTsKvEntity::getEntityId).collect(Collectors.toList());
//        log.info("当前的分页之后的数据之设备id的汇总:{}",ids);
//        List<DeviceEntity>  entities =  deviceDao.queryAllByIds(ids);
//        Map<UUID,DeviceEntity> map1 = entities.stream().collect(Collectors.toMap(DeviceEntity::getId,DeviceEntity->DeviceEntity));
//        log.info("查询到的设备信息map1:{}",map1);
        List<AppDeviceCapVo> appDeviceCapVoList = new ArrayList<>();
        pageList.stream().forEach(entity->{
            AppDeviceCapVo  capVo = new AppDeviceCapVo();
            log.info("entity:====>"+entity);
            capVo.setValue(getValueByEntity(entity));
            capVo.setDeviceId(entity.getEntityId().toString());
            capVo.setDeviceName(entity.getDeviceName());

            if(entity.getWorkshopId() != null) {
                Optional<WorkshopEntity> workshop = workshopRepository.findByTenantIdAndId(tenantId.getId(), entity.getWorkshopId());
                capVo.setWorkshopName(workshop.get().getName());
            }

            if(entity.getProductionLineId() != null) {
                Optional<ProductionLineEntity> productionLine = productionLineRepository.findByTenantIdAndId(tenantId.getId(), entity.getProductionLineId());
                capVo.setProductionName(productionLine.get().getName());
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
        Map<String,String> totalValueMap = new HashMap<>();
        /*********************************   暂时写死的*/
        List<String>  keys1 = new ArrayList<>();
//        keys1.add("totalMsgs");
//        keys1.add("successfulMsgs");
//        keys1.add("failedMsgs");
//        vo.setKeys(keys1);

           keys1=  dictDeviceService.findAllByName(null, EfficiencyEnums.ENERGY_002.getgName());
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
//        appVo.setTotalWaterValue(getTotalValue(effectTsKvEntities,18));
//        appVo.setTotalElectricValue(getTotalValue(effectTsKvEntities,19));
//        appVo.setTotalAirValue(getTotalValue(effectTsKvEntities,20));
        keys1.stream().forEach(str->{

            totalValueMap.put(str,getTotalValue(effectTsKvEntities,str));
        });
        appVo.setTotalValue(totalValueMap);
        return appVo;
    }

    /**
     * dictDeviceId
     * @param vo
     * @param tenantId
     * @return
     */
    @Override
    public Map<String, List<ResultRunStatusByDeviceVo>> queryTheRunningStatusByDevice(QueryRunningStatusVo vo, TenantId tenantId) {
             log.info("查询当前设备的运行状态入参:{}租户id{}",vo,tenantId.getId());
         DeviceEntity deviceInfo =     deviceRepository.findByTenantIdAndId(tenantId.getId(),vo.getDeviceId());
         if(deviceInfo ==  null){
             throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"查不到该设备");

         }
           log.info("查询当前的设备信息{}",deviceInfo);
          List<DictDeviceGroupPropertyVO>   dictDeviceGroupPropertyVOList =   dictDeviceService.listDictDeviceGroupProperty(deviceInfo.getDictDeviceId());
             log.info("查询到的当前设备{}的配置的属性:{}",vo.getDeviceId(),dictDeviceGroupPropertyVOList);
           List<String> keyNames=   dictDeviceGroupPropertyVOList.stream().map(DictDeviceGroupPropertyVO::getName).collect(Collectors.toList());
            log.info("查询到的当前设备{}的配置的keyNames属性:{}",vo.getDeviceId(),keyNames);
           List<TsKvDictionary> kvDictionaries= dictionaryRepository.findAllByKeyIn(keyNames);
             log.info("查询到的当前设备{}的配置的kvDictionaries属性:{}",vo.getDeviceId(),kvDictionaries);
           List<Integer> keys=   kvDictionaries.stream().map(TsKvDictionary::getKeyId).collect(Collectors.toList());
           Map<Integer, String> mapDict  = kvDictionaries.stream().collect(Collectors.toMap(TsKvDictionary::getKeyId,TsKvDictionary::getKey));
                     log.info("查询到的当前设备{}的配置的keys属性:{}###mapDict:{}",vo.getDeviceId(),keys,mapDict);
           List<TsKvEntity> entities= tsKvRepository.findAllByKeysAndEntityIdAndTime(vo.getDeviceId(),keys,vo.getStartTime(),vo.getEndTime());
                log.info("查询到的当前设备{}的配置的entities属性:{}",vo.getDeviceId(),entities);
           List<TsKvEntry> tsKvEntries  = new ArrayList<>();
            entities.stream().forEach(tsKvEntity -> {
                tsKvEntity.setStrKey(mapDict.get(tsKvEntity.getKey()));
              tsKvEntries.add(tsKvEntity.toData());
            });
            List<ResultRunStatusByDeviceVo>  voList = new ArrayList<>();
            voList =  tsKvEntries.stream().map(TsKvEntry ->{
                      ResultRunStatusByDeviceVo byDeviceVo= new ResultRunStatusByDeviceVo();
                      byDeviceVo.setKeyName(TsKvEntry.getKey());
                      byDeviceVo.setValue(TsKvEntry.getValue().toString());
                      byDeviceVo.setTime(TsKvEntry.getTs());
                      return     byDeviceVo;
            }).collect(Collectors.toList());
       Map<String,List<ResultRunStatusByDeviceVo>> map = voList.stream().collect(Collectors.groupingBy(ResultRunStatusByDeviceVo::getKeyName));
      log.info("查询到的当前的数据:{}",map);
        return map;
    }

    /**
     * 查询当前设备的属性
     * @param deviceId
     * @return
     */
    @Override
    public Object queryGroupDict(UUID deviceId,TenantId tenantId) {
        DeviceEntity deviceInfo =     deviceRepository.findByTenantIdAndId(tenantId.getId(),deviceId);
        if(deviceId == null)
        {
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"查询不到此设备!");

        }
        List<DictDeviceDataVo> dictDeviceDataVos = dictDeviceService.findGroupNameAndName(deviceInfo.getDictDeviceId());
        Map<String,List<DictDeviceDataVo>> map = dictDeviceDataVos.stream().collect(Collectors.groupingBy(DictDeviceDataVo::getGroupName));
        return map;
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


    private  String getTotalValue(List<EffectTsKvEntity> effectTsKvEntities,String key)
    {

        Double  totalSku =
                effectTsKvEntities.stream().filter(entity -> entity.getKeyName().equals(key)).mapToDouble(EffectTsKvEntity::getSubtractDouble).sum();

        Long  totalSku2 =
                effectTsKvEntities.stream().filter(entity ->  entity.getKeyName().equals(key)).mapToLong(EffectTsKvEntity::getSubtractLong).sum();
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
            Map<String,String> mapValue = new HashMap<>();

            appDeviceEnergyVo.setDeviceId(key.toString());
            EffectTsKvEntity  entity1 =value.get(0);
            if(entity1 != null) {
                appDeviceEnergyVo.setDeviceName(entity1.getDeviceName());
                appDeviceEnergyVo.setTime(entity1.getTs2());
                if (entity1.getWorkshopId() != null) {
                    Optional<WorkshopEntity> workshop = workshopRepository.findByTenantIdAndId(tenantId.getId(), entity1.getWorkshopId());
                    appDeviceEnergyVo.setWorkshopName(workshop.get().getName());
                }

                if (entity1.getProductionLineId() != null) {
                    Optional<ProductionLineEntity> productionLine = productionLineRepository.findByTenantIdAndId(tenantId.getId(), entity1.getProductionLineId());
                    appDeviceEnergyVo.setProductionName(productionLine.get().getName());
                }

                value.stream().forEach(effectTsKvEntity -> {
                    log.info("打印当前的key:"+effectTsKvEntity.getKey()+"effectTsKvEntity.getValue():"+effectTsKvEntity.getValue());
                    mapValue.put(effectTsKvEntity.getKeyName(),effectTsKvEntity.getValue());
                    //水
//                    if (effectTsKvEntity.getKey() == 18) {
//                        appDeviceEnergyVo.setWaterValue(effectTsKvEntity.getValue());
//                    }
//                    //电
//                    if (effectTsKvEntity.getKey() == 19) {
//                        appDeviceEnergyVo.setElectricValue(effectTsKvEntity.getValue());
//                    }
//                    //气
//                    if (effectTsKvEntity.getKey() == 20) {
//                        appDeviceEnergyVo.setAirValue(effectTsKvEntity.getValue());
//                    }
                });
            }

            appList.add(appDeviceEnergyVo);
        });

        return  appList;
    }
}
