package org.thingsboard.server.dao.sql.role.service.Imp;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.thingsboard.server.common.data.page.PageDataAndTotalValue;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.*;
import org.thingsboard.server.common.data.vo.device.DeviceDictionaryPropertiesVo;
import org.thingsboard.server.common.data.vo.device.DictDeviceDataVo;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.common.data.vo.enums.EfficiencyEnums;
import org.thingsboard.server.common.data.vo.enums.KeyTitleEnums;
import org.thingsboard.server.common.data.vo.home.ResultHomeCapAppVo;
import org.thingsboard.server.common.data.vo.home.ResultHomeEnergyAppVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.common.data.vo.resultvo.devicerun.ResultRunStatusByDeviceVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.AppDeviceEnergyVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.PcDeviceEnergyVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.ResultEnergyAppVo;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.PageUtil;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.hs.dao.*;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.hs.service.ClientService;
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
import org.thingsboard.server.dao.sql.role.dao.EffciencyAnalysisRepository;
import org.thingsboard.server.dao.sql.role.dao.EffectHistoryKvRepository;
import org.thingsboard.server.dao.sql.role.dao.EffectTsKvRepository;
import org.thingsboard.server.dao.sql.role.dao.tool.DataToConversionSvc;
import org.thingsboard.server.dao.sql.role.entity.CensusSqlByDayEntity;
import org.thingsboard.server.dao.sql.role.entity.EffectTsKvEntity;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;
import org.thingsboard.server.dao.sql.role.service.EfficiencyStatisticsSvc;
import org.thingsboard.server.dao.sql.tskv.svc.EnergyHistoryMinuteSvc;
import org.thingsboard.server.dao.sql.workshop.WorkshopRepository;
import org.thingsboard.server.dao.sqlts.dictionary.TsKvDictionaryRepository;
import org.thingsboard.server.dao.sqlts.ts.TsKvRepository;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.dao.util.StringUtilToll;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    // 设备字典Repository
    @Autowired  DictDeviceRepository dictDeviceRepository;
    @Autowired    ClientService clientService;



    @Autowired private EffciencyAnalysisRepository effciencyAnalysisRepository;
    @Autowired private DataToConversionSvc  dataToConversionSvc;
    @Autowired private EnergyHistoryMinuteSvc energyHistoryMinuteSvc;



    private  final  static String  HEADER_0= "设备名称";
    private  final  static String  HEADER_DEVICE_ID= "deviceId";
    private  final  static  String HEADER_1="createdTime";//创建时间

    private  final  static  String PRE_HISTORY_ENERGY="总耗";//历史能耗 ：
    private  final  static  String AFTER_HISTORY_ENERGY="量";//历史能耗 ：




    @Override
    public List<String> queryEntityByKeysHeader() {
        log.debug("效能分页首页得数据，获取表头接口");
        List<String> strings= new ArrayList<>();
        strings.add(HEADER_0);
        List<DictDeviceGroupPropertyVO>    dictVoList= deviceDictPropertiesSvc.findAllDictDeviceGroupVO(EfficiencyEnums.ENERGY_002.getgName());
        dictVoList.stream().forEach(dataVo->{
            strings.add(getHomeKeyNameOnlyUtilNeW(dataVo));

        });
        dictVoList.stream().forEach(dataVo->{
            strings.add(getHomeKeyNameByUtilNeW(dataVo));

        });
        log.debug("查询历史耗能的表头keys1{}",strings);
        return strings;
    }

    /**
     * 查询历史能耗的表头
     * @return
     */
    @Override
    public List<String> queryEnergyHistoryHeader() {
        log.debug("查询历史耗能的表头");
        List<String> strings= new ArrayList<>();
        strings.add(HEADER_0);
        List<DictDeviceGroupPropertyVO>    dictVoList= deviceDictPropertiesSvc.findAllDictDeviceGroupVO(EfficiencyEnums.ENERGY_002.getgName());
        dictVoList.stream().forEach(dataVo->{
            strings.add(getHomeKeyNameOnlyUtilNeW(dataVo));

        });
        strings.add(HEADER_1);
        log.debug("查询历史耗能的表头keys1{}",strings);
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
//        Map<String,DictDeviceGroupPropertyVO>  mapNameToVo  = deviceDictPropertiesSvc.getMapPropertyVo();
        DeviceEntity deviceInfo =     deviceRepository.findByTenantIdAndId(tenantId.getId(),queryTsKvVo.getDeviceId());
        if(deviceInfo == null)
        {
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"查询不到此设备!");
        }
        String deviceName = deviceInfo.getName();
      return   energyHistoryMinuteSvc.queryTranslateTitle(queryTsKvVo,deviceName,pageLink);

//        //先查询能耗的属性
//        List<String>  keys1=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.ENERGY_002.getgName());
//        queryTsKvVo.setKeys(keys1);
//        Page<Map>  page=  effectHistoryKvRepository.queryEntity(queryTsKvVo,DaoUtil.toPageable(pageLink));
//        List<Map> list = page.getContent();
//        log.debug("查询当前角色下的用户绑定数据list{}",list);
//         if(CollectionUtils.isEmpty(list))
//         {
//             return new PageData<Map>(page.getContent(), page.getTotalPages(), page.getTotalElements(), page.hasNext());
//         }
//        List<Map> mapList =   translateTitle(list, deviceName,mapNameToVo);
//        return new PageData<Map>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }

    @Override
    public PageDataAndTotalValue<AppDeviceCapVo> queryPCCapApp(QueryTsKvVo vo, TenantId tenantId, PageLink pageLink) {
        if(StringUtils.isBlank(vo.getKey()))
        {
            List<String>  nameKey=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.CAPACITY_001.getgName());
            String keyName=  nameKey.get(0);
            log.debug("打印的产能key:{}",keyName);
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
            log.debug("entity:====>"+entity);
            capVo.setValue(getValueByEntity(entity));
            capVo.setDeviceId(entity.getEntityId().toString());
            capVo.setDeviceName(entity.getDeviceName());
            capVo.setFlg(entity.getFlg());
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


    /**
     *
     * @param vo
     * @param tenantId
     * @param pageLink
     * @return
     */
    @Override
    public PageDataAndTotalValue<AppDeviceCapVo> queryPCCapAppNewMethod(QueryTsKvVo vo, TenantId tenantId, PageLink pageLink) {
        log.debug("queryPCCapAppNewMethod打印入参的pc端查询产能接口入参:{}租户id{}",vo,tenantId);
        if(vo.getFactoryId() == null)
        {
            vo.setFactoryId(getFirstFactory(tenantId));
        }
        List<EnergyEffciencyNewEntity> entityList = effciencyAnalysisRepository.queryCapacityALL(vo,pageLink);
        Page<EnergyEffciencyNewEntity> page= PageUtil.createPageFromList(entityList,pageLink);
        List<EnergyEffciencyNewEntity> pageList=  page.getContent();
        //将查询的结果返回原接口返回的对象
        List<AppDeviceCapVo>  appDeviceCapVos =  dataToConversionSvc.resultProcessingByCapacityPc(pageList,tenantId);
     return new PageDataAndTotalValue<AppDeviceCapVo>(dataToConversionSvc.getTotalValue(entityList),appDeviceCapVos, page.getTotalPages(), page.getTotalElements(), page.hasNext());

    }

    @Override
    public PageDataAndTotalValue<Map> queryEntityByKeys(QueryTsKvVo vo, TenantId tenantId, PageLink pageLink) throws JsonProcessingException {
//        log.debug("查询能耗的入参{}租户的id{}",vo,tenantId);
        List<String>  totalValueList = new ArrayList<>();
         List<String>  keys1 = new ArrayList<>();
        List<String>  headerList = new ArrayList<>();
        keys1=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.ENERGY_002.getgName());
        headerList.addAll(keys1);
//        log.debug("打印当前的表头name:{}",headerList);
        List<String>  nameKey=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.CAPACITY_001.getgName());

        Map<String,DictDeviceGroupPropertyVO>  mapNameToVo  = deviceDictPropertiesSvc.getMapPropertyVo();

        if(CollectionUtils.isEmpty(nameKey))
        {
            throw  new CustomException("系统初始化的数据异常!");
        }
        String keyName=  nameKey.get(0);//产能的key
        log.debug("查询包含产能得key:{}",keyName);
        keys1.add(keyName);
        vo.setKeys(keys1);

        if(vo.getFactoryId() == null && vo.getDeviceId() == null )
        {
            vo.setFactoryId(getFirstFactory(tenantId));
        }
        List<EffectTsKvEntity>  effectTsKvEntities =  effectTsKvRepository.queryEntityByKeys(vo,vo.getKeys());
//        log.debug("查询到的数据{}",effectTsKvEntities);
        if(CollectionUtils.isEmpty(effectTsKvEntities))
        {
//            log.debug("查询的能耗数据为空入参为:{}",vo);
            headerList.stream().forEach(s -> {
                DictDeviceGroupPropertyVO dvo=  mapNameToVo.get(s);
                String title =StringUtils.isBlank(dvo.getTitle())?dvo.getName():dvo.getTitle();
                totalValueList.add(title+": "+"0"+" ("+dvo.getUnit()+")");
            });
            return new PageDataAndTotalValue<Map>(totalValueList,new ArrayList<>(), 0, 0,false);
        }

        Map<UUID,List<EffectTsKvEntity>> map = effectTsKvEntities.stream().collect(Collectors.groupingBy(EffectTsKvEntity::getEntityId));
        log.debug("查询到的全部数据转换为设备维度:{}",map);
        Set<UUID> keySet = map.keySet();
        log.debug("打印当前的设备id:{}",keySet);
        List<UUID> entityIdsAll  = keySet.stream().collect(Collectors.toList());
        Page<UUID> page= PageUtil.createPageFromList(entityIdsAll,pageLink);
        Map<UUID,List<EffectTsKvEntity>>  listMap =  new HashMap<>();
        List<UUID>  pageList=page.getContent();
        for(int i=0;i<pageList.size();i++)
        {
            UUID uuid= pageList.get(i);
            listMap.put(uuid,map.get(uuid));
        }

        var dictDeviceIds = effectTsKvEntities.stream().map(EffectTsKvEntity::getDictDeviceId).filter(Objects::nonNull).collect(Collectors.toList());
        HashMap<String, DictDevice> finalMap = new HashMap<>();
        if (!dictDeviceIds.isEmpty()){
            finalMap = DaoUtil.convertDataList(this.dictDeviceRepository.findAllByTenantIdAndIdIn(vo.getTenantId(), dictDeviceIds)).stream()
                    .collect(Collectors.toMap(DictDevice::getId, java.util.function.Function.identity(), (a, b)->a, HashMap::new));
        }
        HashMap<String, DictDevice> finalMap1 = finalMap;

        List<AppDeviceEnergyVo>  vos=   getEntityKeyValue(finalMap1,listMap,tenantId);//包含了总产能的

        List<PcDeviceEnergyVo>  resultList=   unitMap(vos,keyName, headerList);
        log.debug("具体的返回包含单位能耗数据:{}",resultList);
        headerList.stream().forEach(str->{
            log.debug("打印当前的str:{}",str);
            DictDeviceGroupPropertyVO dvo=  mapNameToVo.get(str);
            String title =StringUtils.isBlank(dvo.getTitle())?dvo.getName():dvo.getTitle();
            totalValueList.add(title+": "+getTotalValue(effectTsKvEntities, str)+ " ("+dvo.getUnit()+")");
        });
        return new PageDataAndTotalValue<Map>(totalValueList,todataByList(resultList, mapNameToVo,keys1 ), page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }


    /**
     * pc端的能耗接口
     * @param vo
     * @param tenantId
     * @param pageLink
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public PageDataAndTotalValue<Map> queryEntityByKeysNewMethod(QueryTsKvVo vo, TenantId tenantId, PageLink pageLink) throws JsonProcessingException {
//        log.debug("queryEntityByKeysNewMethod打印入参的pc端查询产能接口入参:{}租户id{}",vo,tenantId);
        if(vo.getFactoryId() == null)
        {
            vo.setFactoryId(getFirstFactory(tenantId));
        }
        Map<String,DictDeviceGroupPropertyVO>  mapNameToVo  = deviceDictPropertiesSvc.getMapPropertyVoByTitle();
        List<EnergyEffciencyNewEntity> entityList = effciencyAnalysisRepository.queryEnergyListAll(vo,pageLink);
        Page<EnergyEffciencyNewEntity> page= PageUtil.createPageFromList(entityList,pageLink);
        List<EnergyEffciencyNewEntity> pageList=  page.getContent();
        //将查询的结果返回原接口返回的对象 包含单位能耗
        List<Map>  appDeviceCapVos =  this.resultProcessingByEnergyPc(pageList,mapNameToVo);
        List<String>  totalValueList = getTotalValueNewMethod(entityList,mapNameToVo);
        return new PageDataAndTotalValue<Map>(totalValueList,appDeviceCapVos, page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }

    /**
     * app的产能接口
     * @return
     */
    @Override
    public ResultCapAppVo queryCapApp(QueryTsKvVo vo, TenantId tenantId) {
        ResultCapAppVo  resultCapAppVo = new ResultCapAppVo();
//        log.debug("app的产能分析接口入参:{}",vo);
        /***************暂时写死的 ***/
        if(StringUtils.isBlank(vo.getKey()))
        {
           List<String>  nameKey=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.CAPACITY_001.getgName());
           String keyName=  nameKey.get(0);
           log.debug("打印的产能key:{}",keyName);
            vo.setKey(keyName);
        }
        if(vo.getFactoryId() == null && vo.getFilterFirstFactory())
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
        log.debug("当前的分页之后的数据:{}",pageList);

        var dictDeviceIds = pageList.stream().map(EffectTsKvEntity::getDictDeviceId).filter(Objects::nonNull).collect(Collectors.toList());
        HashMap<String, DictDevice> finalMap = new HashMap<>();
        if (!dictDeviceIds.isEmpty()){
            finalMap = DaoUtil.convertDataList(this.dictDeviceRepository.findAllByTenantIdAndIdIn(tenantId.getId(), dictDeviceIds)).stream()
                    .collect(Collectors.toMap(DictDevice::getId, java.util.function.Function.identity(), (a, b)->a, HashMap::new));
        }
        HashMap<String, DictDevice> finalMap1 = finalMap;
        log.debug("finalMap1:====>"+finalMap1);

        List<AppDeviceCapVo> appDeviceCapVoList = new ArrayList<>();
        pageList.stream().forEach(entity->{
            AppDeviceCapVo  capVo = new AppDeviceCapVo();

   log.debug("=entity===>"+entity);

            capVo.setPicture(Optional.ofNullable(entity.getPicture()).orElse(Optional.ofNullable(entity.getDictDeviceId()).map(UUID::toString).map(finalMap1::get).map(DictDevice::getPicture).orElse(null)));
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


    @Override
    public ResultCapAppVo queryCapAppNewMethod(QueryTsKvVo queryTsKvVo, TenantId tenantId, PageLink pageLink) {
        ResultCapAppVo  resultCapAppVo = new ResultCapAppVo();
        //app的接口调用Pc端
        PageDataAndTotalValue<AppDeviceCapVo>   pageDataAndTotalValue =   this.queryPCCapAppNewMethod(queryTsKvVo,tenantId,pageLink);
        List<AppDeviceCapVo> data =   pageDataAndTotalValue.getData();
         String totalValue =  pageDataAndTotalValue.getTotalValue().toString();
        resultCapAppVo.setTotalValue(totalValue);
        resultCapAppVo.setAppDeviceCapVoList(dataToConversionSvc.fillDevicePicture(data,tenantId));
        return resultCapAppVo;
    }

    /**
     * App端的能耗接口
     * @param vo
     * @param tenantId
     * @param pageLink
     * @return
     */
    @Override
    public ResultEnergyAppVo queryAppEntityByKeysNewMethod(QueryTsKvVo vo, TenantId tenantId, PageLink pageLink,Boolean flg) {
        ResultEnergyAppVo  result  = new ResultEnergyAppVo();
        log.debug("【APP端】queryAppEntityByKeysNewMethod打印入参的pc端查询产能接口入参:{}租户id{}",vo,tenantId);
        if(vo.getFactoryId() == null && flg)
        {
            vo.setFactoryId(getFirstFactory(tenantId));
        }
        Map<String,DictDeviceGroupPropertyVO>  mapNameToVo  = deviceDictPropertiesSvc.getMapPropertyVoByTitle();
        List<EnergyEffciencyNewEntity> entityList = effciencyAnalysisRepository.queryEnergyListAll(vo,pageLink);
        Page<EnergyEffciencyNewEntity> page= PageUtil.createPageFromList(entityList,pageLink);
        List<EnergyEffciencyNewEntity> pageList=  page.getContent();
        //将查询的结果返回原接口返回的对象 包含单位能耗
        List<AppDeviceEnergyVo>  appDeviceCapVos =  dataToConversionSvc.resultProcessingByEnergyApp(pageList,mapNameToVo,tenantId);
        result.setAppDeviceCapVoList(appDeviceCapVos);
        result.setTotalValue(getTotalValueApp(entityList,mapNameToVo));
        return result;
    }

    /**
     * 能耗的查询
     * @param vo
     * @param tenantId
     *   flg 是否查询第一个工厂
     * @return
     */
    @Override
    public ResultEnergyAppVo    queryEntityByKeys(QueryTsKvVo vo, TenantId tenantId,Boolean flg) {
        log.debug("查询能耗的入参{}租户的id{}",vo,tenantId);
        ResultEnergyAppVo appVo = new  ResultEnergyAppVo();
        Map<String,String> totalValueMap = new HashMap<>();
        List<String>  keys1 = new ArrayList<>();
           keys1=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.ENERGY_002.getgName());
          vo.setKeys(keys1);
        Map<String,DictDeviceGroupPropertyVO>  mapNameToVo  = deviceDictPropertiesSvc.getMapPropertyVo();
        if(vo.getFactoryId() == null && flg)
        {
            vo.setFactoryId(getFirstFactory(tenantId));
        }
        List<EffectTsKvEntity>  effectTsKvEntities =  effectTsKvRepository.queryEntityByKeys(vo,vo.getKeys());
        log.debug("查询到的数据{}",effectTsKvEntities);
        if(CollectionUtils.isEmpty(effectTsKvEntities))
        {
            keys1.stream().forEach(s -> {
                totalValueMap.put(translateAppTitle(mapNameToVo,s),"0"+translateAppUnit(mapNameToVo,s));
            });
            appVo.setTotalValue(totalValueMap);
            return appVo;  //如果查询不到; 应该返回的对应的key 且
        }
        Map<UUID,List<EffectTsKvEntity>> map = effectTsKvEntities.stream().collect(Collectors.groupingBy(EffectTsKvEntity::getEntityId));
        log.debug("查询到的数据转换为设备维度:{}",map);
        Set<UUID> keySet = map.keySet();
        log.debug("打印当前的设备id:{}",keySet);
        List<UUID> entityIdsAll  = keySet.stream().collect(Collectors.toList());
        List<UUID>  pageList =  entityIdsAll.stream().skip((vo.getPage())*vo.getPageSize()).limit(vo.getPageSize()).
                collect(Collectors.toList());
        Map<UUID,List<EffectTsKvEntity>>  listMap =  new HashMap<>();


        for(int i=0;i<pageList.size();i++)
        {
            UUID uuid= pageList.get(i);
            listMap.put(uuid,map.get(uuid));
        }

        var dictDeviceIds = effectTsKvEntities.stream().map(EffectTsKvEntity::getDictDeviceId).filter(Objects::nonNull).collect(Collectors.toList());
        HashMap<String, DictDevice> finalMap = new HashMap<>();
        if (!dictDeviceIds.isEmpty()){
            finalMap = DaoUtil.convertDataList(this.dictDeviceRepository.findAllByTenantIdAndIdIn(tenantId.getId(), dictDeviceIds)).stream()
                    .collect(Collectors.toMap(DictDevice::getId, java.util.function.Function.identity(), (a, b)->a, HashMap::new));
        }
        HashMap<String, DictDevice> finalMap1 = finalMap;

        List<AppDeviceEnergyVo>  vos=   getEntityKeyValue(finalMap1,listMap,tenantId);
        appVo.setAppDeviceCapVoList(translateListAppTitle(vos,mapNameToVo));
        keys1.stream().forEach(str->{
            totalValueMap.put(translateAppTitle(mapNameToVo,str),getTotalValue(effectTsKvEntities,str)+translateAppUnit(mapNameToVo,str));
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
        log.debug("查询当前设备的运行状态入参:{}租户id{}",vo,tenantId.getId());

        List<DeviceDictionaryPropertiesVo>  propertiesVos=   queryDictDevice(vo.getDeviceId(),tenantId);
        List<String> keyNames  = vo.getKeyNames();
        if(CollectionUtils.isEmpty(vo.getKeyNames())) {
            List<String>    keyNames0 = propertiesVos.stream().map(DeviceDictionaryPropertiesVo::getName).collect(Collectors.toList());
             keyNames =   keyNames0.stream().limit(3).collect(Collectors.toList());
//          log.debug("打印前三个属性:{}",keyNames);
        }

        log.debug("查询到的当前设备{}的配置的keyNames属性:{}",vo.getDeviceId(),keyNames);
        List<TsKvDictionary> kvDictionaries= dictionaryRepository.findAllByKeyIn(keyNames);
        log.debug("查询到的当前设备{}的配置的kvDictionaries属性:{}",vo.getDeviceId(),kvDictionaries);
        List<Integer> keys=   kvDictionaries.stream().map(TsKvDictionary::getKeyId).collect(Collectors.toList());
        Map<Integer, String> mapDict  = kvDictionaries.stream().collect(Collectors.toMap(TsKvDictionary::getKeyId,TsKvDictionary::getKey));
//        log.debug("查询到的当前设备{}的配置的keys属性:{}###mapDict:{}",vo.getDeviceId(),keys,mapDict);
        List<TsKvEntity> entities= tsKvRepository.findAllByKeysAndEntityIdAndStartTimeAndEndTime(vo.getDeviceId(),keys,vo.getStartTime(),vo.getEndTime());
//        log.debug("查询到的当前设备{}的配置的entities属性:{}",vo.getDeviceId(),entities);
        List<TsKvEntry> tsKvEntries  = new ArrayList<>();
        entities.stream().forEach(tsKvEntity -> {
            tsKvEntity.setStrKey(mapDict.get(tsKvEntity.getKey()));
            tsKvEntries.add(tsKvEntity.toData());
        });
        List<ResultRunStatusByDeviceVo>  voList = new ArrayList<>();
        voList =  tsKvEntries.stream().map(TsKvEntry ->{
            ResultRunStatusByDeviceVo byDeviceVo= new ResultRunStatusByDeviceVo();
            byDeviceVo.setKeyName(TsKvEntry.getKey());
            byDeviceVo.setValue(StringUtilToll.roundUp(TsKvEntry.getValue().toString()));
            byDeviceVo.setTime(TsKvEntry.getTs());
            return     byDeviceVo;
        }).collect(Collectors.toList());
        Map<String,List<ResultRunStatusByDeviceVo>> map = voList.stream().collect(Collectors.groupingBy(ResultRunStatusByDeviceVo::getKeyName));
        log.debug("查询到的当前的数据:{}",map);
        return  keyNameNotFound(keyNames,map);
    }


    /**
     * dictDeviceId
     * @param vo
     * @param tenantId
     * @return
     */
    @Override
    public Map<String, List<ResultRunStatusByDeviceVo>> queryTheRunningStatusByDevice(AppQueryRunningStatusVo vo, TenantId tenantId,PageLink pageLink) throws ThingsboardException {
             log.debug("查询当前设备的运行状态入参:{}租户id{}",vo,tenantId.getId());

        List<DeviceDictionaryPropertiesVo>  propertiesVos=   queryDictDevice(vo.getDeviceId(),tenantId);
             log.debug("查询到的当前设备{}的配置的属性条数:{}",vo.getDeviceId(),propertiesVos.size());

        Map<String, DeviceDictionaryPropertiesVo> translateMap = propertiesVos.stream().collect(Collectors.toMap(DeviceDictionaryPropertiesVo::getName, a -> a,(k1,k2)->k1));
        List<String> keyNames=  vo.getKeyNames();
        List<String> keyPages = new ArrayList<>();
        if(CollectionUtils.isEmpty(keyNames)) {
            List<String>   keyNames01= propertiesVos.stream().map(DeviceDictionaryPropertiesVo::getName).collect(Collectors.toList());
            keyPages =   keyNames01.stream().limit(3).collect(Collectors.toList());
        }else {
            keyPages=  keyNames.stream().skip((vo.getPage())*vo.getPageSize()).limit(vo.getPageSize()).collect(Collectors.toList());

        }

           List<TsKvDictionary> kvDictionaries= dictionaryRepository.findAllByKeyIn(keyPages);
             log.debug("查询到的当前设备{}的配置的kvDictionaries属性:{}",vo.getDeviceId(),kvDictionaries);
           List<Integer> keys=   kvDictionaries.stream().map(TsKvDictionary::getKeyId).collect(Collectors.toList());
           Map<Integer, String> mapDict  = kvDictionaries.stream().collect(Collectors.toMap(TsKvDictionary::getKeyId,TsKvDictionary::getKey));
                     log.debug("查询到的当前设备{}的配置的keys属性:{}###mapDict:{}",vo.getDeviceId(),keys,mapDict);
           List<TsKvEntity> entities= tsKvRepository.findAllByKeysAndEntityIdAndStartTimeAndEndTime(vo.getDeviceId(),keys,vo.getStartTime(),vo.getEndTime());
                log.debug("查询到的当前设备{}的配置的entities属性:{}",vo.getDeviceId(),entities);
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
                      byDeviceVo.setValue(StringUtilToll.roundUp(TsKvEntry.getValue().toString()));
                      byDeviceVo.setTime(TsKvEntry.getTs());
                      byDeviceVo.setTitle(trnaslateVo != null?trnaslateVo.getTitle():"");
                      byDeviceVo.setUnit(trnaslateVo != null?trnaslateVo.getUnit():"");
                return     byDeviceVo;
            }).collect(Collectors.toList());
       Map<String,List<ResultRunStatusByDeviceVo>> map = voList.stream().collect(Collectors.groupingBy(ResultRunStatusByDeviceVo::getKeyName));
        keyPages.stream().forEach(str->{
            List<ResultRunStatusByDeviceVo> voList1 = map.get(str);
           if(CollectionUtils.isEmpty(voList1))
           {
               map.put(str,getDefaultValue(translateMap,str));
           }
        });
      log.debug("查询到的当前的数据:{}",map);
        log.debug("查询到的当前设备{}的配置的keyNames属性:{}",vo.getDeviceId(),keyNames);

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
        dictDeviceDataVos.stream().forEach(m1->{
            if (StringUtils.isBlank(m1.getTitle())) {
                m1.setTitle(m1.getName());
            }});
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
        log.debug("查询到的结果dictDeviceDataVos：{}",dictDeviceDataVos);
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
     * 昨天 今天 历史的产能接口
     * @param vo
     * @return
     */
    @Override
    public ResultHomeCapAppVo queryThreePeriodsCapacity(TsSqlDayVo vo) {
        ResultHomeCapAppVo  resultVO = new ResultHomeCapAppVo();
        if(vo.getStartTime() ==  null)  //如果有值，则是看板的调用
        {
            vo.setStartTime(CommonUtils.getYesterdayZero());
        }
        List<CensusSqlByDayEntity>  entities =  effciencyAnalysisRepository.queryCensusSqlByDay(vo,true);
        Map<LocalDate, CensusSqlByDayEntity> appleMap = entities.stream().collect(Collectors.toMap(CensusSqlByDayEntity::getDate, a -> a,(k1, k2)->k1));
        LocalDate   localDate=  LocalDate.now();
        LocalDate yesterday = localDate.plusDays(-1);
        CensusSqlByDayEntity  data01 = appleMap.get(yesterday);
        if(data01 != null)
        {
            resultVO.setYesterdayValue(StringUtilToll.roundUp(data01.getIncrementCapacity()));
            resultVO.setHistory(StringUtilToll.roundUp(data01.getHistoryCapacity()));
        }
        CensusSqlByDayEntity  nowDate = appleMap.get(localDate);
        if(nowDate != null)
        {
            resultVO.setTodayValue(StringUtilToll.roundUp(nowDate.getIncrementCapacity()));
            resultVO.setHistory(StringUtilToll.roundUp(nowDate.getHistoryCapacity()));
        }
        return resultVO;
    }


    /**
     * 今天 昨天 历史的 能耗  app
     * @param vo
     * @return
     */
    @Override
    public ResultHomeEnergyAppVo queryAppThreePeriodsEnergy(TsSqlDayVo vo) {
        ResultHomeEnergyAppVo  resultHomeEnergyAppVo  = new  ResultHomeEnergyAppVo();
        Map<String,String> yesterdayMap  = new HashMap<>();
        Map<String,String> todayMap  = new HashMap<>();
        Map<String,String> historyMap  = new HashMap<>();

        if(vo.getStartTime() ==  null)  //如果有值，则是看板的调用
        {
            vo.setStartTime(CommonUtils.getYesterdayZero());
        }
        List<CensusSqlByDayEntity>  entities =  effciencyAnalysisRepository.queryCensusSqlByDay(vo,false);
        Map<LocalDate, CensusSqlByDayEntity> appleMap = entities.stream().collect(Collectors.toMap(CensusSqlByDayEntity::getDate, a -> a,(k1, k2)->k1));
        log.debug("queryAppThreePeriodsEnergy.appleMap:{}",appleMap);

        LocalDate   localDate=  LocalDate.now();
        LocalDate yesterday = localDate.plusDays(-1);
        CensusSqlByDayEntity  data01 = appleMap.get(yesterday);
//        if(data01 != null)
//        {
                yesterdayMap.put(KeyTitleEnums.key_water.getgName(),(data01 != null?StringUtilToll.roundUp(data01.getIncrementWater()):"0"));
                yesterdayMap.put(KeyTitleEnums.key_cable.getgName(),(data01 != null ? StringUtilToll.roundUp(data01.getIncrementElectric()):"0"));
                yesterdayMap.put(KeyTitleEnums.key_gas.getgName(),(data01 != null ?StringUtilToll.roundUp(data01.getIncrementGas()):"0"));

               historyMap.put(KeyTitleEnums.key_water.getgName(),(data01 != null ?StringUtilToll.roundUp(data01.getHistoryWater()):"0"));
               historyMap.put(KeyTitleEnums.key_cable.getgName(),(data01 != null ?StringUtilToll.roundUp(data01.getHistoryElectric()):"0"));
               historyMap.put(KeyTitleEnums.key_gas.getgName(),(data01 != null ?StringUtilToll.roundUp(data01.getHistoryGas()):"0"));
//        }
        CensusSqlByDayEntity  nowDate = appleMap.get(localDate);
//        if(nowDate != null)
//        {
       todayMap.put(KeyTitleEnums.key_water.getgName(),(nowDate != null?StringUtilToll.roundUp(nowDate.getIncrementWater()):"0"));
       todayMap.put(KeyTitleEnums.key_cable.getgName(),(nowDate != null ? StringUtilToll.roundUp(nowDate.getIncrementElectric()):"0"));
       todayMap.put(KeyTitleEnums.key_gas.getgName(),(nowDate != null ?StringUtilToll.roundUp(nowDate.getIncrementGas()):"0"));


        historyMap.put(KeyTitleEnums.key_water.getgName(),(nowDate != null ?StringUtilToll.roundUp(nowDate.getHistoryWater()):"0"));
            historyMap.put(KeyTitleEnums.key_cable.getgName(),(nowDate != null ?StringUtilToll.roundUp(nowDate.getHistoryElectric()):"0"));
            historyMap.put(KeyTitleEnums.key_gas.getgName(),(nowDate != null ?StringUtilToll.roundUp(nowDate.getHistoryGas()):"0"));
//        }
        resultHomeEnergyAppVo.setHistory(historyMap);
        resultHomeEnergyAppVo.setTodayValue(todayMap);
        resultHomeEnergyAppVo.setYesterdayValue(yesterdayMap);
        return resultHomeEnergyAppVo;
    }

    /**
     * 获取当前租户的第一个工厂id
     * @param tenantId 当前登录人的租户
     * @return
     */
    public  UUID  getFirstFactory(TenantId  tenantId)
    {
        FactoryEntity  factory = factoryDao.findFactoryByTenantIdFirst(tenantId.getId());
        log.debug("查询当前租户{}的第一个工厂{}",tenantId.getId(),factory);
        if(factory == null)
        {
            log.error("查询当前租户{}没有工厂,不能查询效能分析之产能数据");
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"当前租户没有工厂");

        }
        return factory.getId();
    }







    private  String  getValueByEntity(EffectTsKvEntity entity)
    {
//        if(entity.getSubtractDouble()>0)
//        {
//            return  entity.getSubtractDouble().toString();
//        }
//        if(entity.getSubtractLong()>0)
//        {
//            return  entity.getSubtractLong().toString();
//
//        }
        return entity.getValueLast2();
    }


    private  String getTotalValue(List<EffectTsKvEntity> effectTsKvEntities)
    {

//        BigDecimal invoiceAmount = effectTsKvEntities.stream().filter(m->m.getFlg().equals(true)).map(EffectTsKvEntity::getValueLast2).map(BigDecimal::new).reduce(BigDecimal.ZERO,
//                BigDecimal::add);
        BigDecimal invoiceAmount = effectTsKvEntities.stream().map(EffectTsKvEntity::getValueLast2).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                BigDecimal::add);
        return   StringUtilToll.roundUp(invoiceAmount.stripTrailingZeros().toPlainString());
    }


    private  String getTotalValue(List<EffectTsKvEntity> effectTsKvEntities,String key)
    {
        BigDecimal invoiceAmount = effectTsKvEntities.stream().filter(entity -> (StringUtils.isNotBlank(entity.getKeyName())&&entity.getKeyName().equals(key))).map(EffectTsKvEntity::getValueLast2).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                BigDecimal::add);
        return  StringUtilToll.roundUp(invoiceAmount.stripTrailingZeros().toPlainString());
    }

 /**
     * @param listMap
     * @return
     */
    public  List<AppDeviceEnergyVo>  getEntityKeyValue(HashMap<String, DictDevice> finalMap1,Map<UUID,List<EffectTsKvEntity>> listMap,TenantId tenantId)
    {
        List<AppDeviceEnergyVo> appList  = new ArrayList<>();

        listMap.forEach((key,value)->{
            AppDeviceEnergyVo appDeviceEnergyVo  = new  AppDeviceEnergyVo();
            Map<String,String> mapValue = new HashMap<>();
            Map<String,Long> timeValueMap1= new HashMap<>();
            Map<String,Long> timeValueMap = new HashMap<>();


            appDeviceEnergyVo.setDeviceId(key.toString());
            EffectTsKvEntity  entity1 =value.get(0);
            if(entity1 != null) {
              //  appDeviceEnergyVo.setPicture(entity1.getPicture());
                appDeviceEnergyVo.setPicture(Optional.ofNullable(entity1.getPicture()).orElse(Optional.ofNullable(entity1.getDictDeviceId()).map(UUID::toString).map(finalMap1::get).map(DictDevice::getPicture).orElse(null)));
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
                    if(effectTsKvEntity.getKeyName() != null) {
                        mapValue.put(effectTsKvEntity.getKeyName(), effectTsKvEntity.getValueLast2());
                        timeValueMap.put(effectTsKvEntity.getKeyName(), effectTsKvEntity.getTs2());
                        timeValueMap1.put(effectTsKvEntity.getKeyName(), effectTsKvEntity.getTs());
                    }

                });
                appDeviceEnergyVo.setMapValue(mapValue);
                appDeviceEnergyVo.setTimeValueMap(timeValueMap);
                appDeviceEnergyVo.setTimeValueMap1(timeValueMap1);
            }

            appList.add(appDeviceEnergyVo);
            log.debug("appList:====>{}",appList);

//            try{
//                ObjectMapper mapper=new ObjectMapper();
//               String   jsonStr=mapper.writeValueAsString(appList);
//               log.debug("josn数据:{}",jsonStr);
//            }catch (Exception e)
//            {
//                e.printStackTrace();
//
//            }

        });

        return  appList;
    }


    /**
     *
     * @param vos
     * @param keyName 产能key
     */
    private  List<PcDeviceEnergyVo> unitMap(List<AppDeviceEnergyVo>  vos, String  keyName,List<String>  headerList)
    {
        List<PcDeviceEnergyVo> resultList = new ArrayList<>();
        vos.stream().forEach(energyVo->{
            PcDeviceEnergyVo  vo = new  PcDeviceEnergyVo();
            vo.setDeviceId(energyVo.getDeviceId());
            vo.setDeviceName(energyVo.getDeviceName());
            vo.setProductionName(energyVo.getProductionName());
            vo.setWorkshopName(energyVo.getWorkshopName());
            Map<String,String>  mapOld =    energyVo.getMapValue();
            if(CollectionUtils.isEmpty(mapOld))
            {
                headerList.stream().forEach(str->{
                        mapOld.put(str,"0");
                });
            }
            log.debug("headerList:====>headerList{}",headerList);
            log.debug("mapOld:====>mapOld{}",mapOld);

            String   keyNameValue1 =   mapOld.get(keyName);

            String   keyNameValue =(StringUtils.isEmpty(keyNameValue1)?"0":keyNameValue1);
         log.debug("当前设备的总产能:{}",keyNameValue);

         Map<String,Long> timeValueMap = energyVo.getTimeValueMap();
            Map<String,Long> timeValueMap1 = energyVo.getTimeValueMap1();
           Long time001 =  timeValueMap.get(keyName);


            Map<String,String>  map1 =  new HashMap<>();
            Map<String,String>  map2 =  new HashMap<>();

            mapOld.forEach((key,value1)->{
              if(!key.equals(keyName))
              {
                 if(StringUtilToll.isZero(value1))
                 {
                     map2.put(key,"0");
                 } else {

                     //计算公式：总产能/总能耗/分钟数
                     map1.put(key, value1);
                     Long t01 = timeValueMap1.get(key);
                     Long t02 = timeValueMap.get(key);
                     log.debug("=====>t01{},t02{}", t01, t02);
                     Long t3 = (t02 - t01) / 60000;

                     String aDouble = StringUtilToll.div(keyNameValue, value1, t3.toString());
                     map2.put(key, aDouble.toString());
                 }
              }
            });

            vo.setMapValue(map1);
            vo.setMapUnitValue(map2);
            resultList.add(vo);
        });


        return  resultList;

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
     *
     * @param resultList 返回的的数据
     * @param mapNameToVo
     * @return
     */
    private  List<Map>  todataByList(List<PcDeviceEnergyVo>  resultList,Map<String,DictDeviceGroupPropertyVO>  mapNameToVo,List<String>  keys1 )
    {
        List<Map>  mapList = new ArrayList<>();
        resultList.stream().forEach(vo->{
            Map   map = new HashMap();
            map.put(HEADER_0,vo.getDeviceName());
            map.put(HEADER_DEVICE_ID,vo.getDeviceId());
            Map<String,String> mapData = vo.getMapValue();

             getDefaultMap(keys1,mapData);


            mapData.forEach((k1,v1)->{
                DictDeviceGroupPropertyVO dictVO=  mapNameToVo.get(k1);
                map.put(getHomeKeyNameOnlyUtilNeW(dictVO),v1);
            });
            Map<String,String> mapData1 = vo.getMapUnitValue();
            getDefaultMap(keys1,mapData1);
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
        log.debug("打印获取设备部件的属性:{}",componentPropertyEntities);
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


    private   List<Map> translateTitle(List<Map> list,String deviceName ,Map<String,DictDeviceGroupPropertyVO>  mapNameToVo )
    {
        List<Map> mapList = new ArrayList<>();

        for(Map m:list)
        {
            Map  map1 = new HashMap();
            m.forEach((k,v)->{
                map1.put("设备名称",deviceName);
                if(k.equals("ts"))
                {
                    map1.put("createdTime",v);
                }
                DictDeviceGroupPropertyVO dictVO=  mapNameToVo.get(k);
                if(dictVO != null) {
                    map1.put(getHomeKeyNameOnlyUtilNeW(dictVO), v);
                }
            });
            mapList.add(map1);
        }

        return mapList;

    }


    private String translateAppTitle(Map<String,DictDeviceGroupPropertyVO>  mapNameToVo,String key)
    {
        DictDeviceGroupPropertyVO dictVO=  mapNameToVo.get(key);
        if(dictVO != null) {
            String title =StringUtils.isBlank(dictVO.getTitle())?dictVO.getName():dictVO.getTitle();
            return  title;
        }
        return  key;

    }

    private String translateAppUnit(Map<String,DictDeviceGroupPropertyVO>  mapNameToVo,String key)
    {
//        DictDeviceGroupPropertyVO dictVO=  mapNameToVo.get(key);
//        if(dictVO != null) {
//            return  " ("+dictVO.getUnit()+")";
//        }
        return  "";

    }


    private List<AppDeviceEnergyVo> translateListAppTitle(List<AppDeviceEnergyVo>  vos,Map<String,DictDeviceGroupPropertyVO>  mapNameToVo)
    {
        List<AppDeviceEnergyVo>    voList = new ArrayList<>();
        vos.stream().forEach(vo1->{
         Map<String,String> mapOld =    vo1.getMapValue();
            Map<String,String> mapnew = new HashMap<>();
            mapOld.forEach((key1,value1)->{
                mapnew.put(translateAppTitle(mapNameToVo,key1),value1+translateAppUnit(mapNameToVo,key1));
            });
            vo1.setMapValue(mapnew);
            voList.add(vo1);

        });

        return  voList;


    }


    /**
     * 返回默认的
     * 耗水量: 0 (T)
     * 耗电量: 0 (KWH)
     * 耗气量: 0 (T)
     * @return
     */
    private  Map  getDefaultMap(List<String>  keys, Map<String,String> mapData01)
    {
//        Map<String,String> mapData  = new HashMap<>();
        keys.stream().forEach(str->{
            if(StringUtils.isBlank(mapData01.get(str))) {
                mapData01.put(str, "0");
            }
        });
        return  mapData01;

    }


    /*****
     * PC端的能耗列表接口
     * @param resultList     返回的结果
     * @param mapNameToVo  能耗的title  和具体的能耗的信息
     * @return
     */
    private  List<Map>  resultProcessingByEnergyPc(List<EnergyEffciencyNewEntity>  resultList,Map<String,DictDeviceGroupPropertyVO>  mapNameToVo)
    {
        List<Map>  mapList = new ArrayList<>();
        resultList.stream().forEach(vo->{
            Map   map = new HashMap();
            map.put(HEADER_0,vo.getDeviceName());
            map.put(HEADER_DEVICE_ID,vo.getEntityId());
            map.put(setKeyTitle(mapNameToVo,KeyTitleEnums.key_water,true),StringUtilToll.roundUp(vo.getWaterAddedValue()));//耗水量 (T)
            map.put(setKeyTitle(mapNameToVo,KeyTitleEnums.key_cable,true),StringUtilToll.roundUp(vo.getElectricAddedValue()));//耗电量 (KWH)
            map.put(setKeyTitle(mapNameToVo,KeyTitleEnums.key_gas,true),StringUtilToll.roundUp(vo.getGasAddedValue()));//耗气量 (T)

            String   capacityValue =vo.getCapacityAddedValue();
            //
            map.put(setKeyTitle(mapNameToVo, KeyTitleEnums.key_water,false),
                    computeUnitEnergyConsumption(capacityValue,vo.getWaterAddedValue(),vo.getWaterLastTime(),vo.getWaterFirstTime()));
            map.put(setKeyTitle(mapNameToVo, KeyTitleEnums.key_cable,false),
                    computeUnitEnergyConsumption(capacityValue,vo.getElectricAddedValue(),vo.getElectricLastTime(),vo.getElectricFirstTime()));
            map.put(setKeyTitle(mapNameToVo, KeyTitleEnums.key_gas,false),
                    computeUnitEnergyConsumption(capacityValue,vo.getGasAddedValue(),vo.getGasLastTime(),vo.getGasFirstTime()));
           mapList.add(map);
        });
        return mapList;
    }

    /**
     *
     * @param mapNameToVo
     * @param enums
     * @param type  true表示:  耗气量 (T)
     *              false 表示: 单位能耗耗气量 (T)
     * @return
     */
    private  String setKeyTitle(Map<String,DictDeviceGroupPropertyVO>  mapNameToVo,KeyTitleEnums  enums,Boolean  type)
    {
        DictDeviceGroupPropertyVO  groupPropertyVO =   mapNameToVo.get(enums.getgName());
        if(type )
        {
          return   getHomeKeyNameOnlyUtilNeW(groupPropertyVO);
        }
        return   getHomeKeyNameByUtilNeW(groupPropertyVO);
    }


    /**
     * 计算单位能耗
     * @return
     */
    private  String  computeUnitEnergyConsumption(String capacityValue,String value1,Long lastTime,Long firstTime)
    {
     if(lastTime == null)
     {
         return "0";
     }
     if(firstTime == null)  //一般不存在
     {
         firstTime =0L;
     }
        Long t3 = (lastTime - firstTime) / 60000;
        String aDouble = StringUtilToll.div(capacityValue, value1, t3.toString());
        return  aDouble;
    }


    /**
     *Pc能耗的返回
     * @param pageList
     * @return
     */
    private   List<String>  getTotalValueNewMethod(List<EnergyEffciencyNewEntity> pageList,Map<String,DictDeviceGroupPropertyVO>  mapNameToVo)
    {
        List<String>  totalValueList  = new ArrayList<>();
        BigDecimal invoiceAmount = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getWaterAddedValue()))
                .map(EnergyEffciencyNewEntity::getWaterAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                BigDecimal::add);
        String waterTotalValue= StringUtilToll.roundUp(invoiceAmount.stripTrailingZeros().toPlainString());
        totalValueList.add(addTotalValueList(mapNameToVo,KeyTitleEnums.key_water,waterTotalValue));

        BigDecimal invoiceAmount02 = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getElectricAddedValue()))
                .map(EnergyEffciencyNewEntity::getElectricAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                BigDecimal::add);
        String electricTotalValue= StringUtilToll.roundUp(invoiceAmount02.stripTrailingZeros().toPlainString());
        totalValueList.add(addTotalValueList(mapNameToVo,KeyTitleEnums.key_cable,electricTotalValue));


        BigDecimal invoiceAmount03 = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getGasAddedValue()))
                .map(EnergyEffciencyNewEntity::getGasAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                BigDecimal::add);
        String value03= StringUtilToll.roundUp(invoiceAmount03.stripTrailingZeros().toPlainString());
        totalValueList.add(addTotalValueList(mapNameToVo,KeyTitleEnums.key_gas,value03));
      return  totalValueList;

    }


    /**
     *APP能耗的返回
     * @param pageList
     * @return
     */
    private   Map<String,String>  getTotalValueApp(List<EnergyEffciencyNewEntity> pageList,Map<String,DictDeviceGroupPropertyVO>  mapNameToVo)
    {
        Map<String,String>  resultMap  = new HashMap<>();
        BigDecimal invoiceAmount = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getWaterAddedValue()))
                .map(EnergyEffciencyNewEntity::getWaterAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        String waterTotalValue= StringUtilToll.roundUp(invoiceAmount.stripTrailingZeros().toPlainString());
        resultMap.put(KeyTitleEnums.key_water.getgName(),waterTotalValue);

        BigDecimal invoiceAmount02 = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getElectricAddedValue()))
                .map(EnergyEffciencyNewEntity::getElectricAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        String electricTotalValue= StringUtilToll.roundUp(invoiceAmount02.stripTrailingZeros().toPlainString());
        resultMap.put(KeyTitleEnums.key_cable.getgName(),electricTotalValue);


        BigDecimal invoiceAmount03 = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getGasAddedValue()))
                .map(EnergyEffciencyNewEntity::getGasAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        String value03= StringUtilToll.roundUp(invoiceAmount03.stripTrailingZeros().toPlainString());
        resultMap.put(KeyTitleEnums.key_gas.getgName(),value03);
        return  resultMap;

    }

    private  String  addTotalValueList(Map<String,DictDeviceGroupPropertyVO>  mapNameToVo,KeyTitleEnums  enums,String value )
    {
        DictDeviceGroupPropertyVO dvo=  mapNameToVo.get(enums.getgName());
        String title =StringUtils.isBlank(dvo.getTitle())?dvo.getName():dvo.getTitle();
         return (title+": "+value+ " ("+dvo.getUnit()+")");
    }


    /**
     * App运行状态如果没有查询到
     * 返回默认值
     * @return
     */
    private  List<ResultRunStatusByDeviceVo>  getDefaultValue(Map<String, DeviceDictionaryPropertiesVo> translateMap,String  str)
    {
        List<ResultRunStatusByDeviceVo>  resultList = new ArrayList<>();
        DeviceDictionaryPropertiesVo  properties =   translateMap.get(str);
        ResultRunStatusByDeviceVo  vo = new ResultRunStatusByDeviceVo();
        vo.setTitle(properties.getTitle());
        vo.setKeyName(properties.getName());
        vo.setValue("0");
        vo.setUnit(properties.getUnit());
        resultList.add(vo);
        return  resultList;

    }








}
