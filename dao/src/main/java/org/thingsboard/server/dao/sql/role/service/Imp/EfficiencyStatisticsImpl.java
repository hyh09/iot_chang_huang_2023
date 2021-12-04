package org.thingsboard.server.dao.sql.role.service.Imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageDataAndTotalValue;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.QueryRunningStatusVo;
import org.thingsboard.server.common.data.vo.QueryTsKvHisttoryVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.device.DeviceDictionaryPropertiesVo;
import org.thingsboard.server.common.data.vo.device.DictDeviceDataVo;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.common.data.vo.enums.EfficiencyEnums;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.common.data.vo.resultvo.devicerun.ResultRunStatusByDeviceVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.AppDeviceEnergyVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.PcDeviceEnergyVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.ResultEnergyAppVo;
import org.thingsboard.server.common.data.vo.user.FindUserVo;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.PageUtil;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.hs.dao.DictDataEntity;
import org.thingsboard.server.dao.hs.dao.DictDataRepository;
import org.thingsboard.server.dao.hs.dao.DictDeviceComponentPropertyEntity;
import org.thingsboard.server.dao.hs.dao.DictDeviceComponentPropertyRepository;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.hs.service.DeviceDictPropertiesSvc;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.FactoryEntity;
import org.thingsboard.server.dao.model.sql.ProductionLineEntity;
import org.thingsboard.server.dao.model.sql.WorkshopEntity;
import org.thingsboard.server.dao.model.sqlts.dictionary.TsKvDictionary;
import org.thingsboard.server.dao.model.sqlts.ts.TsKvEntity;
import org.thingsboard.server.dao.sql.device.DeviceRepository;
import org.thingsboard.server.dao.sql.productionline.ProductionLineRepository;
import org.thingsboard.server.dao.sql.role.dao.EffectHistoryKvRepository;
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
    @Autowired private EffectHistoryKvRepository effectHistoryKvRepository;
    @Autowired private DeviceDictPropertiesSvc deviceDictPropertiesSvc;
    @Autowired private DictDeviceComponentPropertyRepository componentPropertyRepository;
    @Autowired private DictDataRepository dictDataRepository;//数据字典


    private  final  static String  HEADER_0= "设备名称";
    private  final  static String  HEADER_DEVICE_ID= "deviceId";
    private  final  static  String HEADER_1="createdTime";//创建时间

    private  final  static  String PRE_HISTORY_ENERGY="总耗";//历史能耗 ：
    private  final  static  String AFTER_HISTORY_ENERGY="量";//历史能耗 ：




    @Override
    public List<String> queryEntityByKeysHeader() {
        log.info("效能分页首页得数据，获取表头接口");
        List<String> strings= new ArrayList<>();
        strings.add(HEADER_0);
        List<DictDeviceGroupPropertyVO>    dictVoList= deviceDictPropertiesSvc.findAllDictDeviceGroupVO(EfficiencyEnums.ENERGY_002.getgName());
        dictVoList.stream().forEach(dataVo->{
            strings.add(getHomeKeyNameOnlyUtilNeW(dataVo));

        });
        dictVoList.stream().forEach(dataVo->{
            strings.add(getHomeKeyNameByUtilNeW(dataVo));

        });
        log.info("查询历史耗能的表头keys1{}",strings);
        return strings;
    }

    /**
     * 查询历史能耗的表头
     * @return
     */
    @Override
    public List<String> queryEnergyHistoryHeader() {
        log.info("查询历史耗能的表头");
        List<String> strings= new ArrayList<>();
        strings.add(HEADER_0);
        List<String>  keys1=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.ENERGY_002.getgName());
        log.info("查询历史耗能的表头{}",keys1);
        Map<String,String>  map = deviceDictPropertiesSvc.getUnit();
        log.info("查询历史耗能的表头map{}",map);

        keys1.stream().forEach(str01->{
            strings.add( getKeyNameByUtil(str01,map));
                }
        );
        strings.add(HEADER_1);
        log.info("查询历史耗能的表头keys1{}",keys1);
        return strings;
    }

    /**
     * 查询历史能耗
     * @param queryTsKvVo
     * @param tenantId
     * @param pageLink
     * @return
     */
    @Override
    public Object queryEnergyHistory(QueryTsKvHisttoryVo queryTsKvVo,TenantId tenantId, PageLink pageLink) {
         Map<String,String>  map = deviceDictPropertiesSvc.getUnit() ;
        DeviceEntity deviceInfo =     deviceRepository.findByTenantIdAndId(tenantId.getId(),queryTsKvVo.getDeviceId());
        if(deviceInfo == null)
        {
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"查询不到此设备!");

        }
        String deviceName = deviceInfo.getName();
        //先查询能耗的属性
        List<String>  keys1=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.ENERGY_002.getgName());
        queryTsKvVo.setKeys(keys1);
        Page<Map>  page=  effectHistoryKvRepository.queryEntity(queryTsKvVo,DaoUtil.toPageable(pageLink));
        List<Map> list = page.getContent();
        log.info("查询当前角色下的用户绑定数据list{}",list);
         if(CollectionUtils.isEmpty(list))
         {
             return new PageData<Map>(page.getContent(), page.getTotalPages(), page.getTotalElements(), page.hasNext());
         }
         List<Map> mapList = new ArrayList<>();
         for(Map m:list)
         {
             Map  map1 = new HashMap();
             m.forEach((k,v)->{

                 map1.put("设备名称",deviceName);
                 if(k.equals("ts"))
                 {
                     map1.put("createdTime",v); //createdTime
                 }
                 if(StringUtils.isNotBlank(map.get(k)))
                 {
                     String  strKey = k+"";
                     map1.put( getKeyNameByUtil(strKey,map),v);
                 }

             });
             mapList.add(map1);

         }

        return new PageData<Map>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext());


    }

    @Override
    public PageDataAndTotalValue<AppDeviceCapVo> queryPCCapApp(QueryTsKvVo vo, TenantId tenantId, PageLink pageLink) {
        if(StringUtils.isBlank(vo.getKey()))
        {
            List<String>  nameKey=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.CAPACITY_001.getgName());
            String keyName=  nameKey.get(0);
            log.info("打印的产能key:{}",keyName);
            vo.setKey(keyName);
        }
        if(vo.getFactoryId() == null)
        {
            vo.setFactoryId(getFirstFactory(tenantId));
        }
        List<EffectTsKvEntity> effectTsKvEntities = effectTsKvRepository.queryEntity(vo);
        List<AppDeviceCapVo> appDeviceCapVoList = new ArrayList<>();

        if(CollectionUtils.isEmpty(effectTsKvEntities))
        {
          return new PageDataAndTotalValue<AppDeviceCapVo>("0",appDeviceCapVoList, 0, 0, false);

        }
        Page<EffectTsKvEntity> page= PageUtil.createPageFromList(effectTsKvEntities,pageLink);
        List<EffectTsKvEntity> pageList=  page.getContent();

        pageList.stream().forEach(entity->{
            AppDeviceCapVo  capVo = new AppDeviceCapVo();
            log.info("entity:====>"+entity);
            capVo.setValue(getValueByEntity(entity));
            capVo.setDeviceId(entity.getEntityId().toString());
            capVo.setDeviceName(entity.getDeviceName());

            if(entity.getWorkshopId() != null) {
                Optional<WorkshopEntity> workshop = workshopRepository.findByTenantIdAndId(tenantId.getId(), entity.getWorkshopId());
                capVo.setWorkshopName(workshop.isPresent()?workshop.get().getName():"");
            }

            if(entity.getProductionLineId() != null) {
                Optional<ProductionLineEntity> productionLine = productionLineRepository.findByTenantIdAndId(tenantId.getId(), entity.getProductionLineId());
                capVo.setProductionName(productionLine.isPresent()?productionLine.get().getName():"");
            }
            appDeviceCapVoList.add(capVo);

        });
        return new PageDataAndTotalValue<AppDeviceCapVo>(getTotalValue(effectTsKvEntities),appDeviceCapVoList, page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }


    @Override
    public PageDataAndTotalValue<Map> queryEntityByKeys(QueryTsKvVo vo, TenantId tenantId, PageLink pageLink) throws JsonProcessingException {
        log.info("查询能耗的入参{}租户的id{}",vo,tenantId);
        List<String>  totalValueList = new ArrayList<>();
         List<String>  keys1 = new ArrayList<>();
        List<String>  headerList = new ArrayList<>();
        keys1=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.ENERGY_002.getgName());
        headerList.addAll(keys1);
        log.info("打印当前的表头name:{}",headerList);
        List<String>  nameKey=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.CAPACITY_001.getgName());

//        Map<String,String>  mapUnit  = dictDeviceService.getUnit();
        Map<String,DictDeviceGroupPropertyVO>  mapNameToVo  = deviceDictPropertiesSvc.getMapPropertyVo();

        if(CollectionUtils.isEmpty(nameKey))
        {
            throw  new CustomException("系统初始化的数据异常!");
        }
        String keyName=  nameKey.get(0);//产能的key
        log.info("查询包含产能得key:{}",keyName);
        keys1.add(keyName);
        vo.setKeys(keys1);

        if(vo.getFactoryId() == null && vo.getDeviceId() == null )
        {
            vo.setFactoryId(getFirstFactory(tenantId));
        }
        List<EffectTsKvEntity>  effectTsKvEntities =  effectTsKvRepository.queryEntityByKeys(vo,vo.getKeys());
        log.info("查询到的数据{}",effectTsKvEntities);
        if(CollectionUtils.isEmpty(effectTsKvEntities))
        {
            log.info("查询的能耗数据为空入参为:{}",vo);
            headerList.stream().forEach(s -> {
                DictDeviceGroupPropertyVO dvo=  mapNameToVo.get(s);
                String title =StringUtils.isBlank(dvo.getTitle())?dvo.getName():dvo.getTitle();
                totalValueList.add(title+": "+"0"+" ("+dvo.getUnit()+")");
            });
            return new PageDataAndTotalValue<Map>(totalValueList,new ArrayList<>(), 0, 0,false);
        }

        Map<UUID,List<EffectTsKvEntity>> map = effectTsKvEntities.stream().collect(Collectors.groupingBy(EffectTsKvEntity::getEntityId));
        log.info("查询到的全部数据转换为设备维度:{}",map);
        Set<UUID> keySet = map.keySet();
        log.info("打印当前的设备id:{}",keySet);
        List<UUID> entityIdsAll  = keySet.stream().collect(Collectors.toList());
        Page<UUID> page= PageUtil.createPageFromList(entityIdsAll,pageLink);
        Map<UUID,List<EffectTsKvEntity>>  listMap =  new HashMap<>();
        List<UUID>  pageList=page.getContent();
        for(int i=0;i<pageList.size();i++)
        {
            UUID uuid= pageList.get(i);
            listMap.put(uuid,map.get(uuid));
        }
        List<AppDeviceEnergyVo>  vos=   getEntityKeyValue(listMap,tenantId);//包含了总产能的

        ObjectMapper mapper = new ObjectMapper();
        String jsonlist = mapper.writeValueAsString(vos);
        log.info("输出打印的所有的能耗的数据:{}",jsonlist);

        List<PcDeviceEnergyVo>  resultList=   unitMap(vos,keyName);
        log.info("具体的返回包含单位能耗数据:{}",resultList);
        headerList.stream().forEach(str->{
            log.info("打印当前的str:{}",str);
            DictDeviceGroupPropertyVO dvo=  mapNameToVo.get(str);
            String title =StringUtils.isBlank(dvo.getTitle())?dvo.getName():dvo.getTitle();
            totalValueList.add(title+": "+getTotalValue(effectTsKvEntities, str)+ " ("+dvo.getUnit()+")");
        });
        return new PageDataAndTotalValue<Map>(totalValueList,todataByList(resultList, mapNameToVo ), page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }

    /**
     * app的产能接口
     * @return
     */
    @Override
    public ResultCapAppVo queryCapApp(QueryTsKvVo vo, TenantId tenantId) {
        ResultCapAppVo  resultCapAppVo = new ResultCapAppVo();
        log.info("app的产能分析接口入参:{}",vo);
        /***************暂时写死的 ***/
        if(StringUtils.isBlank(vo.getKey()))
        {
           List<String>  nameKey=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.CAPACITY_001.getgName());
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
        List<AppDeviceCapVo> appDeviceCapVoList = new ArrayList<>();
        pageList.stream().forEach(entity->{
            AppDeviceCapVo  capVo = new AppDeviceCapVo();
            log.info("entity:====>"+entity);
            capVo.setValue(getValueByEntity(entity));
            capVo.setDeviceId(entity.getEntityId().toString());
            capVo.setDeviceName(entity.getDeviceName());

            if(entity.getWorkshopId() != null) {
                Optional<WorkshopEntity> workshop = workshopRepository.findByTenantIdAndId(tenantId.getId(), entity.getWorkshopId());
                capVo.setWorkshopName(workshop.isPresent()?workshop.get().getName():"");
            }

            if(entity.getProductionLineId() != null) {
                Optional<ProductionLineEntity> productionLine = productionLineRepository.findByTenantIdAndId(tenantId.getId(), entity.getProductionLineId());
                capVo.setProductionName(productionLine.isPresent()?productionLine.get().getName():"");
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
        List<String>  keys1 = new ArrayList<>();
           keys1=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.ENERGY_002.getgName());
          vo.setKeys(keys1);

        if(vo.getFactoryId() == null)
        {
            vo.setFactoryId(getFirstFactory(tenantId));
        }
        List<EffectTsKvEntity>  effectTsKvEntities =  effectTsKvRepository.queryEntityByKeys(vo,vo.getKeys());
        log.info("查询到的数据{}",effectTsKvEntities);
        if(CollectionUtils.isEmpty(effectTsKvEntities))
        {
            keys1.stream().forEach(s -> {
                totalValueMap.put(s,"0");
            });
            appVo.setTotalValue(totalValueMap);
            return appVo;  //如果查询不到; 应该返回的对应的key 且
        }

//        List<EffectTsKvEntity>  pageList =  effectTsKvEntities.stream().skip((vo.getPage())*vo.getPageSize()).limit(vo.getPageSize()).
//                collect(Collectors.toList());
//        log.info("能效当前的分页之后的数据:{}",pageList);
//        List<UUID> ids = pageList.stream().map(EffectTsKvEntity::getEntityId).collect(Collectors.toList());
//        log.info(" 能效-当前的分页之后的数据之设备id的汇总:{}",ids);
        Map<UUID,List<EffectTsKvEntity>> map = effectTsKvEntities.stream().collect(Collectors.groupingBy(EffectTsKvEntity::getEntityId));
        log.info("查询到的数据转换为设备维度:{}",map);
        Set<UUID> keySet = map.keySet();
        log.info("打印当前的设备id:{}",keySet);
        List<UUID> entityIdsAll  = keySet.stream().collect(Collectors.toList());
        List<UUID>  pageList =  entityIdsAll.stream().skip((vo.getPage())*vo.getPageSize()).limit(vo.getPageSize()).
                collect(Collectors.toList());
        Map<UUID,List<EffectTsKvEntity>>  listMap =  new HashMap<>();


        for(int i=0;i<pageList.size();i++)
        {
            UUID uuid= pageList.get(i);
            listMap.put(uuid,map.get(uuid));
        }

        appVo.setAppDeviceCapVoList(getEntityKeyValue(listMap,tenantId));
        keys1.stream().forEach(str->{
            totalValueMap.put(str,getTotalValue(effectTsKvEntities,str));
        });
        appVo.setTotalValue(totalValueMap);
        return appVo;
    }


    /**
     * PC端的运行状态接口数据返回
     * @param vo
     * @param tenantId
     * @return key: 遥测数据的key
     */
    @Override
    public Map<String, List<ResultRunStatusByDeviceVo>> queryPcTheRunningStatusByDevice(QueryRunningStatusVo vo, TenantId tenantId) throws ThingsboardException {
        log.info("查询当前设备的运行状态入参:{}租户id{}",vo,tenantId.getId());
//        DeviceEntity deviceInfo =     deviceRepository.findByTenantIdAndId(tenantId.getId(),vo.getDeviceId());
//        if(deviceInfo ==  null){
//            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"查不到该设备");
//
//        }
//        log.info("查询当前的设备信息{}",deviceInfo);
//        List<DictDeviceGroupPropertyVO>   dictDeviceGroupPropertyVOList =   dictDeviceService.listDictDeviceGroupProperty(deviceInfo.getDictDeviceId());
//        log.info("查询到的当前设备{}的配置的属性:{}",vo.getDeviceId(),dictDeviceGroupPropertyVOList);
//        List<String> keyNames=   dictDeviceGroupPropertyVOList.stream().map(DictDeviceGroupPropertyVO::getName).collect(Collectors.toList());

        List<DeviceDictionaryPropertiesVo>  propertiesVos=   queryDictDevice(vo.getDeviceId(),tenantId);
        List<String> keyNames=propertiesVos.stream().map(DeviceDictionaryPropertiesVo::getName).collect(Collectors.toList());

        log.info("查询到的当前设备{}的配置的keyNames属性:{}",vo.getDeviceId(),keyNames);
        List<TsKvDictionary> kvDictionaries= dictionaryRepository.findAllByKeyIn(keyNames);
        log.info("查询到的当前设备{}的配置的kvDictionaries属性:{}",vo.getDeviceId(),kvDictionaries);
        List<Integer> keys=   kvDictionaries.stream().map(TsKvDictionary::getKeyId).collect(Collectors.toList());
        Map<Integer, String> mapDict  = kvDictionaries.stream().collect(Collectors.toMap(TsKvDictionary::getKeyId,TsKvDictionary::getKey));
        log.info("查询到的当前设备{}的配置的keys属性:{}###mapDict:{}",vo.getDeviceId(),keys,mapDict);
        List<TsKvEntity> entities= tsKvRepository.findAllByKeysAndEntityIdAndStartTimeAndEndTime(vo.getDeviceId(),keys,vo.getStartTime(),vo.getEndTime());
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
        return  keyNameNotFound(keyNames,map);
    }


    /**
     * dictDeviceId
     * @param vo
     * @param tenantId
     * @return
     */
    @Override
    public Map<String, List<ResultRunStatusByDeviceVo>> queryTheRunningStatusByDevice(QueryRunningStatusVo vo, TenantId tenantId) throws ThingsboardException {
             log.info("查询当前设备的运行状态入参:{}租户id{}",vo,tenantId.getId());
        List<DeviceDictionaryPropertiesVo>  propertiesVos=   queryDictDevice(vo.getDeviceId(),tenantId);
             log.info("查询到的当前设备{}的配置的属性条数:{}",vo.getDeviceId(),propertiesVos.size());

        Map<String, DeviceDictionaryPropertiesVo> translateMap = propertiesVos.stream().collect(Collectors.toMap(DeviceDictionaryPropertiesVo::getName, a -> a,(k1,k2)->k1));

        List<String> keyNames=   propertiesVos.stream().map(DeviceDictionaryPropertiesVo::getName).collect(Collectors.toList());
        log.info("查询到的当前设备{}的配置的keyNames属性:{}",vo.getDeviceId(),keyNames);
           List<TsKvDictionary> kvDictionaries= dictionaryRepository.findAllByKeyIn(keyNames);
             log.info("查询到的当前设备{}的配置的kvDictionaries属性:{}",vo.getDeviceId(),kvDictionaries);
           List<Integer> keys=   kvDictionaries.stream().map(TsKvDictionary::getKeyId).collect(Collectors.toList());
           Map<Integer, String> mapDict  = kvDictionaries.stream().collect(Collectors.toMap(TsKvDictionary::getKeyId,TsKvDictionary::getKey));
                     log.info("查询到的当前设备{}的配置的keys属性:{}###mapDict:{}",vo.getDeviceId(),keys,mapDict);
           List<TsKvEntity> entities= tsKvRepository.findAllByKeysAndEntityIdAndStartTimeAndEndTime(vo.getDeviceId(),keys,vo.getStartTime(),vo.getEndTime());
                log.info("查询到的当前设备{}的配置的entities属性:{}",vo.getDeviceId(),entities);
           List<TsKvEntry> tsKvEntries  = new ArrayList<>();
            entities.stream().forEach(tsKvEntity -> {
                tsKvEntity.setStrKey(mapDict.get(tsKvEntity.getKey()));
              tsKvEntries.add(tsKvEntity.toData());
            });
            List<ResultRunStatusByDeviceVo>  voList = new ArrayList<>();
            voList =  tsKvEntries.stream().map(TsKvEntry ->{
                      ResultRunStatusByDeviceVo byDeviceVo= new ResultRunStatusByDeviceVo();
                      String keyName=TsKvEntry.getKey();
                DeviceDictionaryPropertiesVo trnaslateVo=   translateMap.get(keyName);
                      byDeviceVo.setKeyName(keyName);
                      byDeviceVo.setValue(TsKvEntry.getValue().toString());
                      byDeviceVo.setTime(TsKvEntry.getTs());
                      byDeviceVo.setTitle(trnaslateVo != null?trnaslateVo.getTitle():"");
                      byDeviceVo.setUnit(trnaslateVo != null?trnaslateVo.getUnit():"");
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
        if(deviceInfo == null)
        {
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"查询不到此设备!");

        }
        List<DictDeviceDataVo> dictDeviceDataVos = deviceDictPropertiesSvc.findGroupNameAndName(deviceInfo.getDictDeviceId());
        Map<String,List<DictDeviceDataVo>> map = dictDeviceDataVos.stream().collect(Collectors.groupingBy(DictDeviceDataVo::getGroupName));
        map.put("部件",getParts( tenantId,deviceInfo.getDictDeviceId()));
        return map;
    }


    /**
     *
     * @param deviceId  设备id
     * @param tenantId  租户id
     * @return
     * @throws ThingsboardException
     */
    @Override
    public List<DeviceDictionaryPropertiesVo>  queryDictDevice(UUID deviceId, TenantId tenantId) throws ThingsboardException {
        List<DeviceDictionaryPropertiesVo>   deviceDictionaryPropertiesVos= new ArrayList<>();
        DeviceEntity deviceInfo =     deviceRepository.findByTenantIdAndId(tenantId.getId(),deviceId);
        if(deviceInfo == null )
        {
            throw  new ThingsboardException("查询不到此设备!", ThingsboardErrorCode.FAIL_VIOLATION);
        }
        List<DictDeviceDataVo> dictDeviceDataVos = deviceDictPropertiesSvc.findGroupNameAndName(deviceInfo.getDictDeviceId());
        log.info("查询到的结果dictDeviceDataVos：{}",dictDeviceDataVos);
        if(CollectionUtils.isEmpty(dictDeviceDataVos))
        {
            return deviceDictionaryPropertiesVos;
        }
        List<DictDeviceDataVo>  partsList  =  getParts(tenantId,deviceInfo.getDictDeviceId());
        dictDeviceDataVos.addAll(partsList);


        return  dictDeviceDataVos.stream().map(dataVo ->{
            String title =StringUtils.isBlank(dataVo.getTitle())?dataVo.getName():dataVo.getTitle();
            return new  DeviceDictionaryPropertiesVo(dataVo.getName(),title,dataVo.getUnit());
        }).collect(Collectors.toList());
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
     * @param listMap
     * @return
     */
    public  List<AppDeviceEnergyVo>  getEntityKeyValue(Map<UUID,List<EffectTsKvEntity>> listMap,TenantId tenantId)
    {
        List<AppDeviceEnergyVo> appList  = new ArrayList<>();

        listMap.forEach((key,value)->{
            AppDeviceEnergyVo appDeviceEnergyVo  = new  AppDeviceEnergyVo();
            Map<String,String> mapValue = new HashMap<>();
            Map<String,Long> timeValueMap = new HashMap<>();

            appDeviceEnergyVo.setDeviceId(key.toString());
            EffectTsKvEntity  entity1 =value.get(0);
            if(entity1 != null) {
                appDeviceEnergyVo.setDeviceName(entity1.getDeviceName());
                appDeviceEnergyVo.setTime(entity1.getTs2());
                if (entity1.getWorkshopId() != null) {
                    Optional<WorkshopEntity> workshop = workshopRepository.findByTenantIdAndId(tenantId.getId(), entity1.getWorkshopId());
                    appDeviceEnergyVo.setWorkshopName(workshop.isPresent()?workshop.get().getName():"");
                }

                if (entity1.getProductionLineId() != null) {
                    Optional<ProductionLineEntity> productionLine = productionLineRepository.findByTenantIdAndId(tenantId.getId(), entity1.getProductionLineId());
                    appDeviceEnergyVo.setProductionName(productionLine.isPresent()?productionLine.get().getName():"");
                }

                value.stream().forEach(effectTsKvEntity -> {
                    mapValue.put(effectTsKvEntity.getKeyName(),effectTsKvEntity.getValue());
                    timeValueMap.put(effectTsKvEntity.getKeyName(),effectTsKvEntity.getTs2());
                });
                appDeviceEnergyVo.setMapValue(mapValue);
                appDeviceEnergyVo.setTimeValueMap(timeValueMap);
            }

            appList.add(appDeviceEnergyVo);

        });

        return  appList;
    }


    /**
     *
     * @param vos
     * @param keyName 产能key
     */
    private  List<PcDeviceEnergyVo> unitMap(List<AppDeviceEnergyVo>  vos, String  keyName)
    {
        List<PcDeviceEnergyVo> resultList = new ArrayList<>();
        vos.stream().forEach(energyVo->{
            PcDeviceEnergyVo  vo = new  PcDeviceEnergyVo();
            vo.setDeviceId(energyVo.getDeviceId());
            vo.setDeviceName(energyVo.getDeviceName());
            vo.setProductionName(energyVo.getProductionName());
            vo.setWorkshopName(energyVo.getWorkshopName());
            Map<String,String>  mapOld =    energyVo.getMapValue();
         String   keyNameValue =   mapOld.get(keyName);
         log.info("当前设备的总产能:{}",keyNameValue);

         Map<String,Long> timeValueMap = energyVo.getTimeValueMap();
           Long time001 =  timeValueMap.get(keyName);
         log.info("打印设备的总产能的时间:{}",time001);


            Map<String,String>  map1 =  new HashMap<>();
            Map<String,String>  map2 =  new HashMap<>();

            mapOld.forEach((key,value1)->{
              if(!key.equals(keyName))
              {
                  //只会影响到单位能耗计算
                  //
                  //计算公式：总产能/总能耗/分钟数
                  map1.put(key,value1);
                Long  time02 =   (timeValueMap.get(key));
                Long  t3 =   (time001-time02);

                  Double  aDouble =  StringUtilToll.div(keyNameValue,value1,t3.toString());
                  map2.put(key,aDouble.toString());
              }
            });

            vo.setMapValue(map1);
            vo.setMapUnitValue(map2);
            resultList.add(vo);
        });


        return  resultList;

    }


    /**
     * 获取历史能耗的表头
     * @param str01  配置的遥测key
     * @param map  key 对应的单位
     * @return
     */
    private  String getKeyNameByUtil(String str01,Map  map)
    {
       return PRE_HISTORY_ENERGY+str01+AFTER_HISTORY_ENERGY+" ("+map.get(str01)+")";
    }



    /**
     * 获取首页能耗的 水 (单位)
     * @param str01  配置的遥测key
     * @param map  key 对应的单位
     * @return
     */
    private  String getHomeKeyNameOnlyUtil(String str01,Map  map)
    {
        return ""+str01+" ("+map.get(str01)+")";
    }


    /**
     * 能耗分析表头方法
     *  eg:  水 (w)
     * @param dataVo
     * @return
     */
    private  String getHomeKeyNameOnlyUtilNeW(DictDeviceGroupPropertyVO dataVo)
    {
        String title =StringUtils.isBlank(dataVo.getTitle())?dataVo.getName():dataVo.getTitle();
        return ""+title+" ("+dataVo.getUnit()+")";
    }
    /**
     * 能耗分析表头方法
     *  eg:  单位能耗水 (w)
     * @param dataVo
     * @return
     */
    private  String getHomeKeyNameByUtilNeW(DictDeviceGroupPropertyVO dataVo)
    {
        String title =StringUtils.isBlank(dataVo.getTitle())?dataVo.getName():dataVo.getTitle();
        return "单位能耗"+title+" ("+dataVo.getUnit()+")";
    }



    /**
     * 获取首页能耗的 单位能耗水 (单位)
     * @param str01  配置的遥测key
     * @param map  key 对应的单位
     * @return
     */
    private  String getHomeKeyNameByUtil(String str01,Map  map)
    {
        return "单位能耗"+str01+" ("+map.get(str01)+")";
    }



    private  List<Map>  todataByList(List<PcDeviceEnergyVo>  resultList,Map<String,DictDeviceGroupPropertyVO>  mapNameToVo )
    {
        List<Map>  mapList = new ArrayList<>();
//        Map<String,String>  mapUnit  = dictDeviceService.getUnit();

        resultList.stream().forEach(vo->{
            Map   map = new HashMap();
            map.put(HEADER_0,vo.getDeviceName());
            map.put(HEADER_DEVICE_ID,vo.getDeviceId());
            Map<String,String> mapData = vo.getMapValue();
            mapData.forEach((k1,v1)->{
                DictDeviceGroupPropertyVO dictVO=  mapNameToVo.get(k1);
                map.put(getHomeKeyNameOnlyUtilNeW(dictVO),v1);
            });
            Map<String,String> mapData1 = vo.getMapUnitValue();
            mapData1.forEach((k1,v1)->{
                DictDeviceGroupPropertyVO dictVO=  mapNameToVo.get(k1);
                map.put(getHomeKeyNameByUtilNeW(dictVO),v1);
            });
            mapList.add(map);
        });
        return mapList;
    }


    /**
     * 将查询不到的key也返回
     * @param keys name 的集合
     * @param map
     * @return
     */
    private  Map  keyNameNotFound(List<String> keys, Map<String,List<ResultRunStatusByDeviceVo>> map)
    {
        keys.stream().forEach(str->{
            if(CollectionUtils.isEmpty( map.get(str)))
            {
                map.put(str,new ArrayList<>());
            }
        });
        return  map;
    }


    /**
     * 获取部件的属性
     * @param tenantId 租户
     * @param dictDeviceId  设备字典
     * @return
     */
    private   List<DictDeviceDataVo>  getParts(TenantId tenantId,UUID  dictDeviceId)
    {
        List<DictDeviceComponentPropertyEntity>  componentPropertyEntities = componentPropertyRepository.findAllByDictDeviceId(dictDeviceId);
        log.info("打印获取设备部件的属性:{}",componentPropertyEntities);
        List<DictDeviceDataVo> partsList=
                componentPropertyEntities.stream().map(component->{
                    DictDeviceDataVo  vo= new DictDeviceDataVo();
                    vo.setGroupName("部件");
                    vo.setName(component.getName());
                    String title =StringUtils.isBlank(component.getTitle())?component.getName():component.getTitle();
                    vo.setTitle(title);
                    Optional<DictDataEntity>  dictDataEntity  = dictDataRepository.findByTenantIdAndId(tenantId.getId(),component.getDictDataId());
                    vo.setUnit(dictDataEntity.isPresent()?dictDataEntity.get().getUnit():"");
                    return vo;
                }).collect(Collectors.toList());
        return  partsList;

    }
}
