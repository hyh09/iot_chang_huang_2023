package org.thingsboard.server.dao.sql.role.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.DeviceCapacityVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.TsSqlDayVo;
import org.thingsboard.server.common.data.vo.device.DeviceRatingValueVo;
import org.thingsboard.server.common.data.vo.enums.EfficiencyEnums;
import org.thingsboard.server.common.data.vo.enums.KeyTitleEnums;
import org.thingsboard.server.common.data.vo.tskv.ConsumptionTodayVo;
import org.thingsboard.server.common.data.vo.tskv.TrendVo;
import org.thingsboard.server.common.data.vo.tskv.consumption.ConsumptionVo;
import org.thingsboard.server.common.data.vo.tskv.consumption.TkTodayVo;
import org.thingsboard.server.common.data.vo.tskv.consumption.TrendLineVo;
import org.thingsboard.server.common.data.vo.tskv.parameter.TrendParameterVo;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.hs.dao.DictDeviceRepository;
import org.thingsboard.server.dao.hs.dao.DictDeviceStandardPropertyRepository;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupVO;
import org.thingsboard.server.dao.hs.service.DeviceDictPropertiesSvc;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.dao.model.sqlts.dictionary.TsKvDictionary;
import org.thingsboard.server.dao.sql.device.DeviceRepository;
import org.thingsboard.server.dao.sql.role.dao.BoardTrendChartRepositoryNewMethon;
import org.thingsboard.server.dao.sql.role.dao.EffciencyAnalysisRepository;
import org.thingsboard.server.dao.sql.role.dao.EffectTsKvRepository;
import org.thingsboard.server.dao.sql.role.dao.tool.DataToConversionSvc;
import org.thingsboard.server.dao.sql.role.entity.CensusSqlByDayEntity;
import org.thingsboard.server.dao.sql.role.entity.EffectTsKvEntity;
import org.thingsboard.server.dao.sql.role.entity.EnergyChartOfBoardEntity;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;
import org.thingsboard.server.dao.sqlts.dictionary.TsKvDictionaryRepository;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.dao.util.StringUtilToll;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: thingsboard
 * @description: ?????????????????????
 * @author: HU.YUNHUI
 * @create: 2021-12-07 10:52
 **/
@Slf4j
@Data
@Service
public class BulletinBoardImpl implements BulletinBoardSvc {


    @Autowired private DeviceDictPropertiesSvc deviceDictPropertiesSvc;
    @Autowired private EffectTsKvRepository effectTsKvRepository;
    @Autowired private EfficiencyStatisticsSvc efficiencyStatisticsSvc;
    @Autowired private DictDeviceService dictDeviceService;
    @Autowired private FactoryDao factoryDao;
    @Autowired private EffciencyAnalysisRepository effciencyAnalysisRepository;

    @Autowired  private BoardTrendChartRepositoryNewMethon boardTrendChartRepositoryNewMethon;
    @Autowired  private  DictDeviceRepository dictDeviceRepository;
    // ????????????????????????Repository
    @Autowired  private DictDeviceStandardPropertyRepository standardPropertyRepository;
    @Autowired  private DeviceRepository deviceRepository;
    @Autowired  private TsKvDictionaryRepository tsKvDictionaryRepository;
    @Autowired private DataToConversionSvc dataToConversionSvc;


    private final  static   String ONE_HOURS="1800000";//

    /**
     *????????????????????????(?????? ?????????)
     * @param vo
     * @return
     */
    @Override
    public TrendVo energyConsumptionTrend(TrendParameterVo vo)  {
        TrendVo resultResults = new TrendVo();

        try {
//            log.info("????????????????????????????????? ????????????????????????????????????vo???{}", vo);
            List<EnergyChartOfBoardEntity> solidLineData = boardTrendChartRepositoryNewMethon.getSolidTrendLine(vo);
            List<Long> longs = CommonUtils.getTwoTimePeriods(vo.getStartTime(), vo.getEndTime());
            print("????????????longs?????????", longs);
            resultResults.setSolidLine(getSolidLineData(vo,solidLineData,longs));
            resultResults.setDottedLine(getDottedLineData(vo,solidLineData,longs));
            return resultResults;
        }catch (Exception e)
        {
            e.printStackTrace();
            resultResults.setCode("0");
            log.error("????????????????????????(?????? ?????????)??????{}",e);
            return  resultResults;
        }
    }


    @Override
    public List<ConsumptionVo> totalEnergyConsumption(QueryTsKvVo v1, TenantId tenantId) {
        List<ConsumptionVo>  result = new ArrayList<>();
        TsSqlDayVo vo = TsSqlDayVo.constructionByQueryTsKvVo(v1,tenantId);
         vo.setStartTime(v1.getStartTime());
         vo.setEndTime(v1.getEndTime());
        List<CensusSqlByDayEntity>  entities =  effciencyAnalysisRepository.queryCensusSqlByDay(vo,false);
        Map<String,DictDeviceGroupPropertyVO>  titleMapToVo  = deviceDictPropertiesSvc.getMapPropertyVoByTitle();
        result.add(calculationTotal(entities,KeyTitleEnums.key_water,titleMapToVo));
        result.add(calculationTotal(entities,KeyTitleEnums.key_cable,titleMapToVo));
        result.add(calculationTotal(entities,KeyTitleEnums.key_gas,titleMapToVo));
        return result;
    }

    /**
     * ??????????????????????????????
     * @param vo
     * @param tenantId
     * @return
     */
    @Override
    public ConsumptionTodayVo energyConsumptionToday(QueryTsKvVo vo, TenantId tenantId) {
        List<EnergyEffciencyNewEntity> entityList = effciencyAnalysisRepository.queryEnergy(vo);
        print("???????????????????????????",entityList);
        return  dataToConversionSvc.resultProcessByEntityList(entityList,tenantId);
    }

    @Override
    public ConsumptionTodayVo todayUnitEnergy(QueryTsKvVo vo, TenantId tenantId) {
        List<EnergyEffciencyNewEntity> entityList = effciencyAnalysisRepository.queryEnergy(vo);
        print("???????????????????????????001",entityList);
//        List<EnergyEffciencyNewEntity>  entityList1 =  dataToConversionSvc.groupEntityIdSum(entityList);
        return  dataToConversionSvc.todayUntiEnergyByEntityList(entityList,tenantId,vo);
    }

    @Override
    public  Map<UUID,String> queryCapacityValueByDeviceIdAndTime(List<DeviceCapacityVo> deviceCapacityVoList) {
        Map<UUID,String> resultMap = new HashMap<>();
        print("?????????????????????????????????????????????????????????:",deviceCapacityVoList);
        if(CollectionUtils.isEmpty(deviceCapacityVoList)){
            return  resultMap;
        }
        List<String> keys1=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.CAPACITY_001.getgName());
        print("??????????????????keyName:",keys1);
        String key =CollectionUtils.isEmpty(keys1)? "capacities":keys1.get(0);
        Optional<TsKvDictionary> tsKvDictionary =  tsKvDictionaryRepository.findByKey(key);
        int keyId = tsKvDictionary.isPresent()?tsKvDictionary.get().getKeyId():76;
        deviceCapacityVoList.forEach(vo->{
          String valueToMap =   boardTrendChartRepositoryNewMethon.getCapacityValueByDeviceIdAndInTime(vo,keyId);
          resultMap.put(vo.getId(),valueToMap);
        });
        return resultMap;
    }

    /**
     * @param listMap
     * @return
     */
    public  ConsumptionTodayVo getEntityKeyValue(Map<UUID,List<EffectTsKvEntity>> listMap, UUID tenantId,
                                                 Map<String, DictDeviceGroupPropertyVO>  mapNameToVo)
    {
        ConsumptionTodayVo appVo = new  ConsumptionTodayVo();
        List<TkTodayVo> waterList = new ArrayList<>();
        List<TkTodayVo> electricList = new ArrayList<>();
        List<TkTodayVo> gasList = new ArrayList<>();

        listMap.forEach((entityId,value)->{


            EffectTsKvEntity  entity1 =value.get(0);


                value.stream().forEach(effectTsKvEntity -> {
                    DictDeviceGroupPropertyVO dictVO=  mapNameToVo.get(effectTsKvEntity.getKeyName());
                    if(dictVO != null){
                        TkTodayVo  tkTodayVo  =  new TkTodayVo();
                        tkTodayVo.setDeviceId(entityId.toString());
                        if(entity1 != null) {
                            tkTodayVo.setDeviceName(entity1.getDeviceName());
                            tkTodayVo.setFactoryId(entity1.getFactoryId());
                            if (entity1.getFactoryId() != null) {
                                Factory factory = factoryDao.findById(entity1.getFactoryId());
                                tkTodayVo.setFactoryName(factory != null ? factory.getName() : "");
                            }
                        }


                            if(dictVO.getTitle().equals(KeyTitleEnums.key_cable.getgName()))
                            {
                                tkTodayVo.setValue(effectTsKvEntity.getValueLast2());
                                tkTodayVo.setTotalValue(effectTsKvEntity.getLocalValue());
                                tkTodayVo.setTs(effectTsKvEntity.getTs());
                                electricList.add(tkTodayVo);
                            }
                            if(dictVO.getTitle().equals(KeyTitleEnums.key_gas.getgName()))
                            {
                                tkTodayVo.setValue(effectTsKvEntity.getValueLast2());
                                tkTodayVo.setTotalValue(effectTsKvEntity.getLocalValue());
                                tkTodayVo.setTs(effectTsKvEntity.getTs());
                                gasList.add(tkTodayVo);
                            }
                            if(dictVO.getTitle().equals(KeyTitleEnums.key_water.getgName()))
                            {
                                tkTodayVo.setValue(effectTsKvEntity.getValueLast2());
                                tkTodayVo.setTotalValue(effectTsKvEntity.getLocalValue());
                                tkTodayVo.setTs(effectTsKvEntity.getTs());
                                waterList.add(tkTodayVo);
                            }
                    }
                });



        });
        appVo.setElectricList(compareToMaxToMin(electricList));
        appVo.setWaterList(compareToMaxToMin(waterList));
        appVo.setGasList(compareToMaxToMin(gasList));
        return  appVo;
    }


    /**?????????*/
    public static List<TkTodayVo> compareToMaxToMin(List<TkTodayVo> list){
        return list.stream().sorted((s1, s2) -> new BigDecimal(s2.getTotalValue()).compareTo(new BigDecimal(s1.getTotalValue()))).collect(Collectors.toList());
    }










    private  String getKeyNameBy(String title) throws ThingsboardException {
        String title1 = KeyTitleEnums.getNameByCode(title);
        if(title1 == null)
        {
            throw  new ThingsboardException("?????????key???????????????,", ThingsboardErrorCode.FAIL_VIOLATION);
        }
        List<DictDeviceGroupVO>  dictDeviceGroupVOS  = dictDeviceService.getDictDeviceGroupInitData();
        for(DictDeviceGroupVO  vo:dictDeviceGroupVOS)
        {
            List<DictDeviceGroupPropertyVO>  voList=  vo.getGroupPropertyList();
            if(!CollectionUtils.isEmpty(voList))
            {
                for(DictDeviceGroupPropertyVO  m1:voList)
                {
                    if(m1.getTitle().equals(title1))
                    {
                        return m1.getName();
                    }

                }
            }

        }
        return "";
    }











    private  ConsumptionVo calculationTotal (List<CensusSqlByDayEntity> entities,KeyTitleEnums enums,Map<String,DictDeviceGroupPropertyVO>  mapNameToVo )
    {
        ConsumptionVo  consumptionVo   = new  ConsumptionVo();
        String value  = "0";
        if(enums ==KeyTitleEnums.key_water ) {
            BigDecimal water0 = entities.stream()
                    .filter(m1->StringUtils.isNotEmpty(m1.getIncrementWater()))
                    .map(CensusSqlByDayEntity::getIncrementWater).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                            BigDecimal::add);
             value =  water0.stripTrailingZeros().toPlainString();
        }else  if(enums == KeyTitleEnums.key_cable ){
            BigDecimal electric1 = entities.stream()
                    .filter(m1->StringUtils.isNotEmpty(m1.getIncrementElectric()))
                    .map(CensusSqlByDayEntity::getIncrementElectric).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                            BigDecimal::add);
            value =  electric1.stripTrailingZeros().toPlainString();

        }else {
            BigDecimal gas2 = entities.stream()
                    .filter(m1->StringUtils.isNotEmpty(m1.getIncrementGas()))
                    .map(CensusSqlByDayEntity::getIncrementGas).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                            BigDecimal::add);
            value =  gas2.stripTrailingZeros().toPlainString();

        }
        consumptionVo.setValue(value);
        consumptionVo.setTitle(enums.getgName());
        DictDeviceGroupPropertyVO dict =     mapNameToVo.get(enums.getgName());
        consumptionVo.setUnit(dict.getUnit());
        return  consumptionVo;
    }


    /**************************  */

    /**
     * ???????????????????????????
     * @param vo
     * @return
     */
    private List<TrendLineVo> getSolidLineData(TrendParameterVo vo, List<EnergyChartOfBoardEntity> solidLineData,   List<Long> longs  )
    {
        print("?????????????????????", solidLineData);
        Map<Long, List<EnergyChartOfBoardEntity>> map = solidLineData.stream().collect(Collectors.groupingBy(EnergyChartOfBoardEntity::getTs));
        print("?????????????????????map", map);
        Map<Long, String> longStringMap = solid(map, vo.getKey());
        print("?????????????????????", longStringMap);
        return   fillReturnData(longs,longStringMap);
    }


    /**
     *
     * @param vo
     * @param solidLineData
     * @param longs ??????
     * @return
     */
    private  List<TrendLineVo> getDottedLineData(TrendParameterVo vo, List<EnergyChartOfBoardEntity> solidLineData,List<Long> longs) throws ThingsboardException {
        String keyName = getKeyNameBy(vo.getKey());
        //????????????????????????????????????
        List<UUID> entityIds = solidLineData.stream().map(EnergyChartOfBoardEntity::getEntityId).distinct().collect(Collectors.toList());
        print("????????????id???",entityIds);
        List<DeviceRatingValueVo>  deviceRatingValueVos  =  deviceRepository.queryDeviceIdAndValue(entityIds,keyName);
        print("?????????????????????????????????",deviceRatingValueVos);
        Map<UUID, String> IdMappingContentMap = deviceRatingValueVos.stream().collect(Collectors.toMap(DeviceRatingValueVo::getId, DeviceRatingValueVo::getContent));
        print("??????????????????????????????IdMappingContentMap???",IdMappingContentMap);
        Map<Long, List<EnergyChartOfBoardEntity>> map = solidLineData.stream().collect(Collectors.groupingBy(EnergyChartOfBoardEntity::getTs));
        //????????? ??????????????????
        Map<Long,String>  TimeToValueMap = dashedData(vo.getKey(),map,IdMappingContentMap);
        return   fillReturnData(longs,TimeToValueMap);
    }


    private  void print(String str,Object   obj)  {
        try {
            ObjectMapper mapper=new ObjectMapper();
            String jsonStr=mapper.writeValueAsString(obj);
            log.info("[json]"+str+jsonStr);
        }catch (Exception e)
        {
            log.info(str+obj);
        }
    }

    /**
     * ?????????????????????
     * @param map
     * @param key
     * @return
     */
    private  Map<Long,String> solid(Map<Long,List<EnergyChartOfBoardEntity>> map, String  key)
    {
        KeyTitleEnums enums = KeyTitleEnums.getEnumsByCode(key);
        Map<Long,String> mapValue = new HashMap<>();
        map.forEach((k1,v2)->{
            mapValue.put(k1,getTotalValue(v2,enums));

        });
        return  mapValue;

    }


    /**
     * ????????? ???????????????
     * @param pageList
     * @param enums
     * @return
     */
    public String getTotalValue(List<EnergyChartOfBoardEntity> pageList, KeyTitleEnums enums) {

        if(enums ==KeyTitleEnums.key_water ) {
            BigDecimal invoiceAmount = pageList.stream()
                    .filter(m1 -> StringUtils.isNotEmpty(m1.getWaterAddedValue()))
                    .map(EnergyChartOfBoardEntity::getWaterAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                            BigDecimal::add);
            String waterTotalValue = StringUtilToll.roundUp(invoiceAmount.stripTrailingZeros().toPlainString());
            return waterTotalValue;
        }

        if(enums == KeyTitleEnums.key_cable) {

            BigDecimal invoiceAmount02 = pageList.stream()
                    .filter(m1 -> StringUtils.isNotEmpty(m1.getElectricAddedValue()))
                    .map(EnergyChartOfBoardEntity::getElectricAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                            BigDecimal::add);
            String electricTotalValue = StringUtilToll.roundUp(invoiceAmount02.stripTrailingZeros().toPlainString());
            return electricTotalValue;
        }

        BigDecimal invoiceAmount03 = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getGasAddedValue()))
                .map(EnergyChartOfBoardEntity::getGasAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        String value03= StringUtilToll.roundUp(invoiceAmount03.stripTrailingZeros().toPlainString());
        return  value03;
    }




    private  Map dashedData(String key,Map<Long, List<EnergyChartOfBoardEntity>> map,Map<UUID, String> IdMappingContentMap)
    {
        Map<Long,String> resultMap = new HashMap<>();
        KeyTitleEnums enums = KeyTitleEnums.getEnumsByCode(key);
        map.forEach((k1,v1)->{
            resultMap.put(k1,getDashedPointData(v1,IdMappingContentMap,enums));
        });
        return  resultMap;
    }


    private  String getDashedPointData(List<EnergyChartOfBoardEntity> entities,Map<UUID, String> IdMappingContentMap,KeyTitleEnums enums)
    {

        List<String> finalValueList = new ArrayList<>();
        entities.stream().forEach(m1->{
            Long t2 =0L;
            Long t1 =0L;
            if(enums == KeyTitleEnums.key_water)
            {
                 t2 =   m1.getWaterLastTime();
                 t1 =   m1.getWaterFirstTime();

            }
            if(enums == KeyTitleEnums.key_cable)
            {
                 t2 =   m1.getElectricLastTime();
                 t1 =   m1.getElectricFirstTime();

            }
            if(enums == KeyTitleEnums.key_gas)
            {
                 t2 =   m1.getGasLastTime();
                 t1 =   m1.getGasFirstTime();

            }
            t2 =(t2==null)?0L:t2;
            t1 =(t1==null)?0L:t1;
            String  t3 = StringUtilToll.sub(t2.toString(),t1.toString());
            String  hours =   StringUtilToll.div(t3.toString(),ONE_HOURS);
            String setValue =  IdMappingContentMap.get(m1.getEntityId());//????????????
            String  va =   StringUtilToll.div(setValue,"2");
            String  finalValue=   StringUtilToll.mul(hours,va);
//            log.info("====>?????????????????????:????????????{}??????????????????{}d???????????????{};?????????????????????30?????????{}?????????????????????{}???????????????{}"
//                                                 ,t2,t1,t3,hours,va,finalValue       );

            finalValueList.add(finalValue);

        });
        return  StringUtilToll.accumulator(finalValueList);

    }


    private   List<TrendLineVo> fillReturnData(List<Long> longs,Map<Long, String> longStringMap ){
        List<TrendLineVo> trendLineVos = longs.stream().map(ts -> {
            TrendLineVo trendLineVo = new TrendLineVo();
            trendLineVo.setTime(ts);
            String value = longStringMap.get(ts);
            trendLineVo.setValue(StringUtils.isEmpty(value) ? "0" : value);
            return trendLineVo;
        }).collect(Collectors.toList());
        return  trendLineVos;
    }



}
