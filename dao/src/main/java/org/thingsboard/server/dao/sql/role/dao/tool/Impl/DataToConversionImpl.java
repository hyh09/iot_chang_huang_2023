package org.thingsboard.server.dao.sql.role.dao.tool.Impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.enums.KeyTitleEnums;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.AppDeviceEnergyVo;
import org.thingsboard.server.common.data.vo.tskv.ConsumptionTodayVo;
import org.thingsboard.server.common.data.vo.tskv.consumption.TkTodayVo;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.hs.dao.DictDeviceRepository;
import org.thingsboard.server.dao.hs.dao.DictDeviceStandardPropertyEntity;
import org.thingsboard.server.dao.hs.dao.DictDeviceStandardPropertyRepository;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.hs.service.DeviceDictPropertiesSvc;
import org.thingsboard.server.dao.model.sql.ProductionLineEntity;
import org.thingsboard.server.dao.model.sql.WorkshopEntity;
import org.thingsboard.server.dao.sql.productionline.ProductionLineRepository;
import org.thingsboard.server.dao.sql.role.dao.tool.DataToConversionSvc;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;
import org.thingsboard.server.dao.sql.tskv.entity.EnergyHistoryMinuteEntity;
import org.thingsboard.server.dao.sql.workshop.WorkshopRepository;
import org.thingsboard.server.dao.util.StringUtilToll;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: thingsboard
 * @description: 效能分析的对象转换接口
 * @author: HU.YUNHUI
 * @create: 2021-12-22 15:03
 **/
@Service
@Slf4j
public class DataToConversionImpl implements DataToConversionSvc {

    @Autowired private FactoryDao factoryDao;
    @Autowired private WorkshopRepository workshopRepository;
    @Autowired private ProductionLineRepository productionLineRepository;
    @Autowired private   DictDeviceRepository dictDeviceRepository;
    @Autowired private DeviceDictPropertiesSvc deviceDictPropertiesSvc;
    @Autowired  private DictDeviceStandardPropertyRepository dictDeviceStandardPropertyRepository;




    /**
     * pc端的产能列表的数据返回的处理
     * @param entityList
     * @return
     */
    @Override
    public List<AppDeviceCapVo> resultProcessingByCapacityPc(List<EnergyEffciencyNewEntity> entityList, TenantId tenantId) {
        List<AppDeviceCapVo> appDeviceCapVoList = new ArrayList<>();
        entityList.stream().forEach(entity->{
            AppDeviceCapVo  capVo = new AppDeviceCapVo();
            capVo.setValue(StringUtils.isEmpty(entity.getCapacityAddedValue())?"0":entity.getCapacityAddedValue());
            capVo.setDeviceId(entity.getEntityId().toString());
            capVo.setDeviceName(entity.getDeviceName());
            capVo.setDictDeviceId(entity.getDictDeviceId());
            capVo.setFlg(entity.getFlg());
            capVo.setWorkshopName(getWorkShopName(entity.getWorkshopId(),tenantId));
            capVo.setProductionName(getProductionLineNameById(entity.getProductionLineId(),tenantId));
            appDeviceCapVoList.add(capVo);
        });

        return  appDeviceCapVoList;
    }

    /**
     * 结果的数据返回处理 看板的今日排行数据
     * @param entityList
     * @param tenantId
     * @return
     */
    @Override
    public ConsumptionTodayVo resultProcessByEntityList(List<EnergyEffciencyNewEntity> entityList, TenantId tenantId) {
        ConsumptionTodayVo  resultVo = new ConsumptionTodayVo();
        List<TkTodayVo> waterList = new ArrayList<>();
        List<TkTodayVo> electricList = new ArrayList<>();
        List<TkTodayVo> gasList = new ArrayList<>();

        Map<UUID,String> mapFactoryCache = new HashMap<>();
        entityList.stream().forEach(m1->{

            waterList.add(returnTheData(m1,mapFactoryCache,m1.getWaterAddedValue(),m1.getWaterValue()));
            electricList.add(returnTheData(m1,mapFactoryCache,m1.getElectricAddedValue(),m1.getElectricValue()));
            gasList.add(returnTheData(m1,mapFactoryCache,m1.getGasAddedValue(),m1.getGasValue()));

        });

        resultVo.setWaterList(compareToMaxToMin(waterList));
        resultVo.setElectricList(compareToMaxToMin(electricList));
        resultVo.setGasList(compareToMaxToMin(gasList));
        return resultVo;
    }

    @Override
    public ConsumptionTodayVo todayUntiEnergyByEntityList(List<EnergyEffciencyNewEntity> entityList, TenantId tenantId, QueryTsKvVo vo) {
        ConsumptionTodayVo  resultVo = new ConsumptionTodayVo();
        List<TkTodayVo> waterList = new ArrayList<>();
        List<TkTodayVo> electricList = new ArrayList<>();
        List<TkTodayVo> gasList = new ArrayList<>();
         String waterValue=   this.queryStandardEnergyValue(vo.getDictDeviceId(),KeyTitleEnums.key_water);
        String gasValue=   this.queryStandardEnergyValue(vo.getDictDeviceId(),KeyTitleEnums.key_gas);
        String  cableValue=   this.queryStandardEnergyValue(vo.getDictDeviceId(),KeyTitleEnums.key_cable);

        Map<UUID,String> mapFactoryCache = new HashMap<>();
        entityList.stream().forEach(m1->{

            waterList.add(toDayreturnTheData(m1,mapFactoryCache,m1.getWaterAddedValue(),m1.getWaterValue() ,waterValue));
            electricList.add(toDayreturnTheData(m1,mapFactoryCache,m1.getElectricAddedValue(),m1.getElectricValue(),gasValue));
            gasList.add(toDayreturnTheData(m1,mapFactoryCache,m1.getGasAddedValue(),m1.getGasValue(),cableValue));

        });

        resultVo.setWaterList(compareToMinToMax(waterList));
        resultVo.setElectricList(compareToMinToMax(electricList));
        resultVo.setGasList(compareToMinToMax(gasList));
        return resultVo;
    }


    @Override
    public List<EnergyEffciencyNewEntity> groupEntityIdSum(List<EnergyEffciencyNewEntity> entityList) {
        if(CollectionUtils.isEmpty(entityList))
        {
            return  entityList;
        }
      Map<UUID,List<EnergyEffciencyNewEntity>> entityListGroup= entityList.stream().collect(Collectors.groupingBy(EnergyEffciencyNewEntity::getEntityId));
        List<EnergyEffciencyNewEntity>  entityList1 = new ArrayList<>();
        entityListGroup.forEach((k1,v1)->
        {
            EnergyEffciencyNewEntity  ev = new EnergyEffciencyNewEntity();
            ev.setEntityId(k1);
            if(!CollectionUtils.isEmpty(v1))
            {
                EnergyEffciencyNewEntity  entity=   v1.stream().findFirst().orElse(new EnergyEffciencyNewEntity());
                ev.setDictDeviceId(entity.getDictDeviceId());
                ev.setDeviceName(entity.getDeviceName());
                ev.setFactoryId(entity.getFactoryId());
                ev.setWaterAddedValue(StringUtilToll.accumulator(v1.stream().map(EnergyEffciencyNewEntity::getWaterAddedValue).collect(Collectors.toList())));
                ev.setWaterValue(StringUtilToll.getMaxSum(v1.stream().map(EnergyEffciencyNewEntity::getWaterValue).collect(Collectors.toList())));
                ev.setGasAddedValue(StringUtilToll.accumulator(v1.stream().map(EnergyEffciencyNewEntity::getGasAddedValue).collect(Collectors.toList())));
                ev.setGasValue(StringUtilToll.accumulator(v1.stream().map(EnergyEffciencyNewEntity::getWaterValue).collect(Collectors.toList())));
                ev.setElectricAddedValue(StringUtilToll.accumulator(v1.stream().map(EnergyEffciencyNewEntity::getElectricAddedValue).collect(Collectors.toList())));
                ev.setElectricValue(StringUtilToll.accumulator(v1.stream().map(EnergyEffciencyNewEntity::getElectricValue).collect(Collectors.toList())));
                ev.setCapacityAddedValue(StringUtilToll.accumulator(v1.stream().map(EnergyEffciencyNewEntity::getCapacityAddedValue).collect(Collectors.toList())));
                ev.setCapacityValue(StringUtilToll.accumulator(v1.stream().map(EnergyEffciencyNewEntity::getCapacityValue).collect(Collectors.toList())));
                entityList1.add(ev);
           }

        });
        return entityList1;
    }

    /**
     * 计算总产能的
     * @param effectTsKvEntities
     * @return
     */
    @Override
    public String getTotalValue(List<EnergyEffciencyNewEntity> effectTsKvEntities) {
        if(CollectionUtils.isEmpty(effectTsKvEntities))
        {
            return "0";
        }
        BigDecimal invoiceAmount = effectTsKvEntities.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getCapacityAddedValue()))
                .map(EnergyEffciencyNewEntity::getCapacityAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                BigDecimal::add);
        return   StringUtilToll.roundUp(invoiceAmount.stripTrailingZeros().toPlainString());
    }


    @Override
    public List<AppDeviceCapVo> fillDevicePicture(List<AppDeviceCapVo>  appDeviceCapVoList,TenantId tenantId) {
        var dictDeviceIds = appDeviceCapVoList.stream().map(AppDeviceCapVo::getDictDeviceId).filter(Objects::nonNull).collect(Collectors.toList());
//        HashMap<String, DictDevice> finalMap = new HashMap<>();
//        if (!dictDeviceIds.isEmpty()){
//            finalMap = DaoUtil.convertDataList(this.dictDeviceRepository.findAllByTenantIdAndIdIn(tenantId.getId(), dictDeviceIds)).stream()
//                    .collect(Collectors.toMap(DictDevice::getId, java.util.function.Function.identity(), (a, b)->a, HashMap::new));
//        }
        HashMap<String, DictDevice> finalMap1 = getDictionariesMap(dictDeviceIds,tenantId);
        appDeviceCapVoList.stream().forEach(m1->{
            m1.setPicture(Optional.ofNullable(m1.getPicture()).orElse(Optional.ofNullable(m1.getDictDeviceId()).map(UUID::toString).map(finalMap1::get).map(DictDevice::getPicture).orElse(null)));

        });
        return  appDeviceCapVoList;

    }

    @Override
    public List<AppDeviceEnergyVo> resultProcessingByEnergyApp(List<EnergyEffciencyNewEntity> pageList, Map<String,
                                                                DictDeviceGroupPropertyVO> mapNameToVo,TenantId tenantId
                                                                )
    {
        List<AppDeviceEnergyVo>  appDeviceEnergyVos = new ArrayList<>();
        var dictDeviceIds = pageList.stream().map(EnergyEffciencyNewEntity::getDictDeviceId).filter(Objects::nonNull).collect(Collectors.toList());
        HashMap<String, DictDevice> finalMap1 = getDictionariesMap(dictDeviceIds,tenantId);

        pageList.stream().forEach(m1->{
            AppDeviceEnergyVo  vo = new AppDeviceEnergyVo();
            vo.setDeviceId(m1.getEntityId().toString());
            vo.setDeviceName(m1.getDeviceName());
            vo.setWorkshopName(getWorkShopName(m1.getWorkshopId(),tenantId));
            vo.setProductionName(getProductionLineNameById(m1.getProductionLineId(),tenantId));
            vo.setPicture(Optional.ofNullable(m1.getPicture()).orElse(Optional.ofNullable(m1.getDictDeviceId()).map(UUID::toString).map(finalMap1::get).map(DictDevice::getPicture).orElse(null)));
            vo.setTime(m1.getTs());
            vo.setMapValue(getAppEnergyVarMap(m1));
            appDeviceEnergyVos.add(vo);
        });


        return appDeviceEnergyVos;
    }


    /**
     * 效能的历史的返回
     * @param energyHistoryMinuteEntities
     * @return
     */
    @Override
    public List<Map> resultProcessByEnergyHistoryMinuteEntity(List<EnergyHistoryMinuteEntity> energyHistoryMinuteEntities,String deviceName) {
        if(CollectionUtils.isEmpty(energyHistoryMinuteEntities))
        {
            return new ArrayList<>();
        }
         //返回标题-能耗的
        Map<String,DictDeviceGroupPropertyVO>  mapNameToVo  = deviceDictPropertiesSvc.getMapPropertyVoByTitle();
        List<Map> mapList = new ArrayList<>();
        for(EnergyHistoryMinuteEntity m:energyHistoryMinuteEntities)
        {
            Map  map1 = new HashMap();
            map1.put("设备名称",deviceName);
            map1.put("createdTime",m.getTs());
            map1.put(getHomeKeyNameOnlyUtilNeW(KeyTitleEnums.key_water,mapNameToVo),m.getWaterValue());
            map1.put(getHomeKeyNameOnlyUtilNeW(KeyTitleEnums.key_gas,mapNameToVo),m.getGasValue());
            map1.put(getHomeKeyNameOnlyUtilNeW(KeyTitleEnums.key_cable,mapNameToVo),m.getCapacityValue());
            mapList.add(map1);
        }

        return mapList;

    }

    @Override
    public String queryStandardEnergyValue(UUID dictDeviceId, KeyTitleEnums enums) {
        Map<String, DictDeviceGroupPropertyVO>  map = deviceDictPropertiesSvc.getMapPropertyVoByTitle();
        String namekey = map.get(enums.getgName()).getName();
        List<String> name = new ArrayList<String>();
        name.add(namekey);
        List<DictDeviceStandardPropertyEntity>   entityList= dictDeviceStandardPropertyRepository.findAllByInContentAndDictDataId(dictDeviceId,name);
        return  (entityList.stream().findFirst().orElse(new DictDeviceStandardPropertyEntity()).getContent());
    }

    /**
     * 翻译工厂的名称
     * @param factoryId
     * @param mapFactoryCache 局部map
     * @return
     */
    private  String  getFactoryNameById(UUID factoryId,Map<UUID,String> mapFactoryCache)
    {
         if(factoryId == null){
             return  "";
         }
        String mapName = mapFactoryCache.get(factoryId);
        if(StringUtils.isNotEmpty(mapName))
        {
            return mapName;
        }
        Factory factory = factoryDao.findById(factoryId);
        String  factoryName=StringUtils.isNotEmpty(factory.getName())?factory.getName():"";
        mapFactoryCache.put(factoryId,factoryName);
        return factoryName;
    }


    /**
     * 翻译车间的名称
     * @param workShopId
     * @param tenantId
     * @return
     */
    private  String  getWorkShopName(UUID workShopId,TenantId tenantId)
    {
        if(workShopId != null) {
            Optional<WorkshopEntity> workshop = workshopRepository.findByTenantIdAndId(tenantId.getId(),workShopId);
            return  (workshop.isPresent()?workshop.get().getName():"");
        }
        return "";
    }


    /**
     * 翻译产线的名称
     * @param productionLineId
     * @param tenantId
     * @return
     */
    private  String  getProductionLineNameById(UUID productionLineId,TenantId tenantId)
    {
        if(productionLineId != null) {
            Optional<ProductionLineEntity> productionLine = productionLineRepository.findByTenantIdAndId(tenantId.getId(), productionLineId);
            return  (productionLine.isPresent()?productionLine.get().getName():"");
        }
        return "";
    }


    /**
     * 获取设备字典 id  和设备字典对象 map
     *    用于图片
     * @param dictDeviceIds
     * @param tenantId
     * @return
     */
    public  HashMap<String, DictDevice>  getDictionariesMap(List<UUID> dictDeviceIds,TenantId tenantId)
    {
        HashMap<String, DictDevice> finalMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(dictDeviceIds)){
            finalMap = DaoUtil.convertDataList(this.dictDeviceRepository.findAllByTenantIdAndIdIn(tenantId.getId(), dictDeviceIds)).stream()
                    .collect(Collectors.toMap(DictDevice::getId, java.util.function.Function.identity(), (a, b)->a, HashMap::new));
        }
        return  finalMap;

    }

    /**
     * 返回app能耗的  数据
     */
    private  Map  getAppEnergyVarMap(EnergyEffciencyNewEntity entity)
    {
        Map  map = new HashMap();
        map.put(KeyTitleEnums.key_water.getgName(),(StringUtils.isNotEmpty(entity.getWaterAddedValue())?entity.getWaterAddedValue():"0"));
        map.put(KeyTitleEnums.key_gas.getgName(),(StringUtils.isNotEmpty(entity.getGasAddedValue())?entity.getGasAddedValue():"0"));
        map.put(KeyTitleEnums.key_cable.getgName(),(StringUtils.isNotEmpty(entity.getElectricAddedValue())?entity.getElectricAddedValue():"0"));

        return  map;

    }


    /**
     * 过滤空的value 默认为0
     * @param str
     * @return
     */
    private String filterEmpty(String str)
    {
      return   StringUtils.isNotEmpty(str)?str:"0";
    }


    /**大到小*/
    public static List<TkTodayVo> compareToMaxToMin(List<TkTodayVo> list){
        return list.stream().sorted((s1, s2) -> new BigDecimal(s2.getValue()).compareTo(new BigDecimal(s1.getValue()))).collect(Collectors.toList());
    }

    public static List<TkTodayVo> compareToMinToMax(List<TkTodayVo> list){
        return list.stream().sorted((s1, s2) -> new BigDecimal(s1.getValue()).compareTo(new BigDecimal(s2.getValue()))).collect(Collectors.toList());
    }


    private  TkTodayVo  returnTheData(EnergyEffciencyNewEntity m1, Map<UUID,String> mapFactoryCache,String  value,String historyValue )
    {
        TkTodayVo  todayVo = new TkTodayVo();
        todayVo.setDeviceId(m1.getEntityId().toString());
        todayVo.setDeviceName(m1.getDeviceName());
        todayVo.setTs(m1.getTs());
        todayVo.setFactoryName(getFactoryNameById(m1.getFactoryId(),mapFactoryCache));
        todayVo.setValue(filterEmpty(value));
        todayVo.setTotalValue(filterEmpty(historyValue));
        return  todayVo;
    }


    /**
     * 单位能耗的计算
     */
    private  TkTodayVo  toDayreturnTheData(EnergyEffciencyNewEntity m1, Map<UUID,String> mapFactoryCache,String  value,String historyValue,String pipValue )
    {
        TkTodayVo  todayVo = new TkTodayVo();
        todayVo.setDeviceId(m1.getEntityId().toString());
        todayVo.setDeviceName(m1.getDeviceName());
        todayVo.setTs(m1.getTs());
        todayVo.setFactoryName(getFactoryNameById(m1.getFactoryId(),mapFactoryCache));
        String  value1 = StringUtilToll.div(value,m1.getCapacityAddedValue());
        todayVo.setValue(filterEmpty(value1));
        todayVo.setFlg(StringUtilToll.compareTo(value1,pipValue));
        todayVo.setTotalValue(filterEmpty(historyValue));
        return  todayVo;

    }



    /**
     * 能耗分析表头方法
     * @param enums 定义的枚举类
     * @param mapNameToVo 初始化数据
     * @return
     */
    private  String getHomeKeyNameOnlyUtilNeW(KeyTitleEnums enums, Map<String,DictDeviceGroupPropertyVO> mapNameToVo)
    {
        DictDeviceGroupPropertyVO  dataVo= mapNameToVo.get(enums.getgName());
        String title =StringUtils.isBlank(dataVo.getTitle())?dataVo.getName():dataVo.getTitle();
        return ""+title+" ("+dataVo.getUnit()+")";
    }




}
