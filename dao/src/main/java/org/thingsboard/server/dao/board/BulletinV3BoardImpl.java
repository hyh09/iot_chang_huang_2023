package org.thingsboard.server.dao.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.BoardV3DeviceDictionaryVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.TsSqlDayVo;
import org.thingsboard.server.common.data.vo.bodrd.DashboardV3Vo;
import org.thingsboard.server.common.data.vo.bodrd.UnitEnergyVo;
import org.thingsboard.server.common.data.vo.enums.KeyTitleEnums;
import org.thingsboard.server.common.data.vo.tskv.TrendChart02Vo;
import org.thingsboard.server.common.data.vo.tskv.consumption.TrendLineVo;
import org.thingsboard.server.common.data.vo.tskv.parameter.TrendParameterVo;
import org.thingsboard.server.dao.board.repository.BoardV3DeviceDictionaryReposutory;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.hs.dao.DictDeviceStandardPropertyEntity;
import org.thingsboard.server.dao.hs.dao.DictDeviceStandardPropertyRepository;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.hs.service.DeviceDictPropertiesSvc;
import org.thingsboard.server.dao.sql.role.dao.BoardTrendChartRepositoryNewMethon;
import org.thingsboard.server.dao.sql.role.dao.EffciencyAnalysisRepository;
import org.thingsboard.server.dao.sql.role.entity.BoardV3DeviceDitEntity;
import org.thingsboard.server.dao.sql.role.entity.EnergyChartOfBoardEntity;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.dao.util.StringUtilToll;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2022-03-07 11:19
 **/
@Slf4j
@Service
public class BulletinV3BoardImpl implements  BulletinV3BoardVsSvc{


    @Autowired  private BoardV3DeviceDictionaryReposutory boardV3DeviceDictionaryReposutory;
    @Autowired  private DeviceService deviceService;
    @Autowired  private DictDeviceStandardPropertyRepository dictDeviceStandardPropertyRepository;
    @Autowired private DeviceDictPropertiesSvc deviceDictPropertiesSvc;
    @Autowired private EffciencyAnalysisRepository effciencyAnalysisRepository;
    @Autowired  private BoardTrendChartRepositoryNewMethon boardTrendChartRepositoryNewMethon;




    /**
     *
     * @param tsSqlDayVo
     * @return
     */
    @Override
    public List<BoardV3DeviceDitEntity> queryDeviceDictionaryByEntityVo(TsSqlDayVo tsSqlDayVo) {
        try {
            return boardV3DeviceDictionaryReposutory.queryDeviceDictionaryByEntityVo(tsSqlDayVo);
        }catch (Exception e)
        {
             log.info("queryDeviceDictionaryByEntityVo.??????:{}",e);
            return  null;
        }
    }

    @Override
    public List<DashboardV3Vo> queryDashboardValue(BoardV3DeviceDictionaryVo vo,TenantId  tenantId) {
        List<DashboardV3Vo>  dashboardV3Vos  = new ArrayList<>();

        log.info("??????:BulletinV3BoardImpl.queryDashboardValue?????????:{}",vo);
        Device  device = new  Device();
        device.setDictDeviceId(vo.getId());
        Map<String, DictDeviceGroupPropertyVO>  map = deviceDictPropertiesSvc.getMapPropertyVoByTitle();
        List<String> keyName= map.entrySet().stream().map(e->e.getValue().getName()).collect(Collectors.toList());
        List<DictDeviceStandardPropertyEntity>   dictDeviceStandardPropertyEntities= dictDeviceStandardPropertyRepository.findAllByInContentAndDictDataId(vo.getId(),keyName);
        Map<String,String> stringStringMap= dictDeviceStandardPropertyEntities.stream().collect(Collectors.toMap(DictDeviceStandardPropertyEntity::getName,DictDeviceStandardPropertyEntity::getContent));
        log.info("??????:BulletinV3BoardImpl.queryDashboardValue???????????????stringStringMap??????:{}",dictDeviceStandardPropertyRepository);
        TotalCalculationVo  totalCalculationVo=   totalCalculation(vo,tenantId);
        dashboardV3Vos.add(getResultList(stringStringMap,KeyTitleEnums.key_water,map,totalCalculationVo.getWater()));
        dashboardV3Vos.add(getResultList(stringStringMap,KeyTitleEnums.key_cable,map,totalCalculationVo.getElectric()));
        dashboardV3Vos.add(getResultList(stringStringMap,KeyTitleEnums.key_gas,map,totalCalculationVo.getGas()));

        return dashboardV3Vos;
    }


    /**
     * ?????????
     * @param vo
     * @param tenantId
     * @return
     */
    @Override
    public TrendChart02Vo trendChart(TrendParameterVo vo, TenantId tenantId) {
        TrendChart02Vo  trendVo = new TrendChart02Vo();
        List<EnergyChartOfBoardEntity> solidLineData = boardTrendChartRepositoryNewMethon.getSolidTrendLine(vo);
        print("????????????solidLineData:{} ",solidLineData);
        Map<String, DictDeviceGroupPropertyVO>  map = deviceDictPropertiesSvc.getMapPropertyVoByTitle();
        String namekey = map.get(KeyTitleEnums.getNameByCode(vo.getKey())).getName();
        List<String> name = new ArrayList<String>();
        name.add(namekey);
        List<DictDeviceStandardPropertyEntity>   entityList= dictDeviceStandardPropertyRepository.findAllByInContentAndDictDataId(vo.getDictDeviceId(),name);
        trendVo.setSolidLine(getSolidLineData(vo,solidLineData));
        trendVo.setDottedLine(entityList.stream().findFirst().orElse(new DictDeviceStandardPropertyEntity()).getContent());
        return trendVo;
    }

    private DashboardV3Vo getResultList(Map<String,String> stringStringMap, KeyTitleEnums  enums, Map<String, DictDeviceGroupPropertyVO> map , String actulValue)
    {
        DashboardV3Vo  vo1 = new DashboardV3Vo();
        DictDeviceGroupPropertyVO  dictVo =  map.get(enums.getgName());
        vo1.setName(enums.getAbbreviationName());
        vo1.setStandardValue(stringStringMap.get(dictVo.getName()));
        vo1.setActualValue(actulValue);
        vo1.setUnit(dictVo.getUnit());
        vo1.setKey(enums.getCode());
       return  vo1;

    }


    /**
     * KeyTitleEnums
     *  1  ???
     *  2  ???
     *  3  ???
     * @param vo
     * @return
     */
    private  TotalCalculationVo  totalCalculation(BoardV3DeviceDictionaryVo vo,TenantId tenantId)
    {
        long  deviceCount = deviceService.countAllByDictDeviceIdAndTenantId(vo.getId(),tenantId.getId());

        TotalCalculationVo  resultVo = new  TotalCalculationVo();
        QueryTsKvVo  queryTsKvVo = new QueryTsKvVo();
        queryTsKvVo.setStartTime(vo.getStartTime());
        queryTsKvVo.setEndTime(vo.getEndTime());
        queryTsKvVo.setDictDeviceId(vo.getId());
        queryTsKvVo.setFactoryId(vo.getFactoryId());
        List<EnergyEffciencyNewEntity> entityList = effciencyAnalysisRepository.queryEnergy(queryTsKvVo);
      List<UnitEnergyVo>  unitEnergyVos=   entityList.stream().map(e->{
            UnitEnergyVo  vo1  = new UnitEnergyVo();
            vo1.setWaterUnit(StringUtilToll.div(e.getWaterAddedValue(),e.getCapacityAddedValue()));
            vo1.setElectricUnit(StringUtilToll.div(e.getElectricAddedValue(),e.getCapacityAddedValue()));
            vo1.setGasUnit(StringUtilToll.div(e.getGasAddedValue(),e.getCapacityAddedValue()));
            return  vo1;
        }).collect(Collectors.toList());



//        String  gasValue=  StringUtilToll.accumulator(entityList.stream().map(EnergyEffciencyNewEntity::getGasAddedValue).collect(Collectors.toList())) ;
//        String  waterValue=  StringUtilToll.accumulator(entityList.stream().map(EnergyEffciencyNewEntity::getWaterAddedValue).collect(Collectors.toList())) ;
//        String  electriValue=  StringUtilToll.accumulator(entityList.stream().map(EnergyEffciencyNewEntity::getElectricAddedValue).collect(Collectors.toList())) ;
//        String  value=  StringUtilToll.accumulator(entityList.stream().map(EnergyEffciencyNewEntity::getCapacityAddedValue).collect(Collectors.toList())) ;

          String  value = String.valueOf(deviceCount);
        resultVo.setWater(StringUtilToll.div( StringUtilToll.accumulator(unitEnergyVos.stream().map(UnitEnergyVo::getWaterUnit).collect(Collectors.toList())),value));
        resultVo.setElectric(StringUtilToll.div(StringUtilToll.accumulator(unitEnergyVos.stream().map(UnitEnergyVo::getElectricUnit).collect(Collectors.toList())),value));
        resultVo.setGas(StringUtilToll.div(StringUtilToll.accumulator(unitEnergyVos.stream().map(UnitEnergyVo::getGasUnit).collect(Collectors.toList())),value));
        log.info("????????????????????????:{}",resultVo);
        return  resultVo;
    }


    /**
     * ????????????
     */
    /**
     * ???????????????????????????
     * @param vo
     * @return
     */
    private List<TrendLineVo> getSolidLineData(TrendParameterVo vo, List<EnergyChartOfBoardEntity> solidLineData)
    {
        long  deviceCount = deviceService.countAllByDictDeviceIdAndTenantId(vo.getDictDeviceId(),vo.getTenantId());
        log.info("????????????????????????{}",deviceCount);
        List<Long> longs = CommonUtils.getTwoTimePeriods(vo.getStartTime(), vo.getEndTime(), Calendar.HOUR,1);
        Map<Long, List<EnergyChartOfBoardEntity>> map = solidLineData.stream().collect(Collectors.groupingBy(EnergyChartOfBoardEntity::getTs));
        Map<Long, String> longStringMap = solid(map, vo.getKey(),deviceCount);
        print("longStringMap?????????===>",longStringMap);
        return   fillReturnData(longs,longStringMap);
    }



    private  void print(String str,Object   obj)  {
        try {
            ObjectMapper mapper=new ObjectMapper();
            String jsonStr=mapper.writeValueAsString(obj);
            log.debug("[json]"+str+jsonStr);
        }catch (Exception e)
        {
            log.info(str+obj);
        }
    }




    /**
     * ?????????????????????
     * @param map
     * @param key
     * @param deviceCount ?????????
     * @return  ?????? ??? ????????????
     */
    private  Map<Long,String> solid(Map<Long,List<EnergyChartOfBoardEntity>> map, String  key, long  deviceCount)
    {
        KeyTitleEnums enums = KeyTitleEnums.getEnumsByCode(key);
        Map<Long,String> mapValue = new HashMap<>();
        map.forEach((k1,v2)->{
            mapValue.put(k1,getTotalValue(v2,enums,deviceCount));

        });
        return  mapValue;

    }



    /**
     * ????????? ????????????????????? /?????????????????????
     * @param enums
     *  @param deviceCount
     * @return
     */
    public String getTotalValue(List<EnergyChartOfBoardEntity> pageList, KeyTitleEnums enums,long deviceCount) {

//        String capValue =  StringUtilToll.accumulator(pageList.stream().map(EnergyChartOfBoardEntity::getCapacityAddedValue).collect(Collectors.toList()));
        String value =  StringUtilToll.accumulator(pageList.stream().map(m1->{
            if(enums== KeyTitleEnums.key_water)
            {
                return StringUtilToll.div(m1.getWaterAddedValue(),m1.getCapacityAddedValue());//m1.getWaterAddedValue();
            }else if(enums == KeyTitleEnums.key_gas) {
                return StringUtilToll.div(m1.getGasAddedValue(),m1.getCapacityAddedValue());
            }
            return StringUtilToll.div(m1.getElectricAddedValue(),m1.getCapacityAddedValue());
        }).collect(Collectors.toList()));
        String deviceCountStr=String.valueOf(deviceCount);
        return  StringUtilToll.div(value,deviceCountStr);
    }

    private   List<TrendLineVo> fillReturnData(List<Long> longs,Map<Long, String> longStringMap ){
        HashMap<String,String> stringHashMap = new HashMap<>();
        stringHashMap.put("firstValue","0");

        List<TrendLineVo> trendLineVos = longs.stream().map(ts -> {
            TrendLineVo trendLineVo = new TrendLineVo();
            trendLineVo.setTime(ts);
            String value = longStringMap.get(ts);
          String nowValue =  StringUtils.isEmpty(value) ? "0" : value;
          if(StringUtilToll.isNotZero(nowValue))
          {
              if(StringUtils.isEmpty(stringHashMap.get("firstData")))
              {
                  stringHashMap.put("firstData",nowValue);
              }
              stringHashMap.put("firstValue",nowValue);
              trendLineVo.setValue(nowValue);
          }else {
              trendLineVo.setValue(stringHashMap.get("firstValue"));
          }
            return trendLineVo;
        }).collect(Collectors.toList());
        trendLineVos.stream().forEach(m1->{
            if(StringUtilToll.isZero(m1.getValue()))
            {
                m1.setValue(StringUtils.isNotEmpty(stringHashMap.get("firstData"))?stringHashMap.get("firstData"):"0");
            }
        });

        print("??????map",stringHashMap);
        return  trendLineVos;
    }



}
