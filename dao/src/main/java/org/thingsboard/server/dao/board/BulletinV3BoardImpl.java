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
             log.info("queryDeviceDictionaryByEntityVo.异常:{}",e);
            return  null;
        }
    }

    @Override
    public List<DashboardV3Vo> queryDashboardValue(BoardV3DeviceDictionaryVo vo) {
        List<DashboardV3Vo>  dashboardV3Vos  = new ArrayList<>();

        log.info("方法:BulletinV3BoardImpl.queryDashboardValue的入参:{}",vo);
        Device  device = new  Device();
        device.setDictDeviceId(vo.getId());
        Map<String, DictDeviceGroupPropertyVO>  map = deviceDictPropertiesSvc.getMapPropertyVoByTitle();
        List<String> keyName= map.entrySet().stream().map(e->e.getValue().getName()).collect(Collectors.toList());
        List<DictDeviceStandardPropertyEntity>   dictDeviceStandardPropertyEntities= dictDeviceStandardPropertyRepository.findAllByInContentAndDictDataId(vo.getId(),keyName);
        Map<String,String> stringStringMap= dictDeviceStandardPropertyEntities.stream().collect(Collectors.toMap(DictDeviceStandardPropertyEntity::getName,DictDeviceStandardPropertyEntity::getContent));
        log.info("方法:BulletinV3BoardImpl.queryDashboardValue的查询到的stringStringMap出参:{}",dictDeviceStandardPropertyRepository);
        TotalCalculationVo  totalCalculationVo=   totalCalculation(vo);
        dashboardV3Vos.add(getResultList(stringStringMap,KeyTitleEnums.key_water,map,totalCalculationVo.getWater()));
        dashboardV3Vos.add(getResultList(stringStringMap,KeyTitleEnums.key_cable,map,totalCalculationVo.getElectric()));
        dashboardV3Vos.add(getResultList(stringStringMap,KeyTitleEnums.key_gas,map,totalCalculationVo.getGas()));

        return dashboardV3Vos;
    }


    /**
     * 趋势图
     * @param vo
     * @param tenantId
     * @return
     */
    @Override
    public TrendChart02Vo trendChart(TrendParameterVo vo, TenantId tenantId) {
        TrendChart02Vo  trendVo = new TrendChart02Vo();
        List<EnergyChartOfBoardEntity> solidLineData = boardTrendChartRepositoryNewMethon.getSolidTrendLine(vo);
        print("打印日志solidLineData:{} ",solidLineData);
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
     *  1  水
     *  2  气
     *  3  电
     * @param vo
     * @return
     */
    private  TotalCalculationVo  totalCalculation(BoardV3DeviceDictionaryVo vo)
    {
        TotalCalculationVo  resultVo = new  TotalCalculationVo();
        QueryTsKvVo  queryTsKvVo = new QueryTsKvVo();
        queryTsKvVo.setStartTime(vo.getStartTime());
        queryTsKvVo.setEndTime(vo.getEndTime());
        queryTsKvVo.setDictDeviceId(vo.getId());
        queryTsKvVo.setFactoryId(vo.getFactoryId());
        List<EnergyEffciencyNewEntity> entityList = effciencyAnalysisRepository.queryEnergy(queryTsKvVo);
        String  gasValue=  StringUtilToll.accumulator(entityList.stream().map(EnergyEffciencyNewEntity::getGasAddedValue).collect(Collectors.toList())) ;
        String  waterValue=  StringUtilToll.accumulator(entityList.stream().map(EnergyEffciencyNewEntity::getWaterAddedValue).collect(Collectors.toList())) ;
        String  electriValue=  StringUtilToll.accumulator(entityList.stream().map(EnergyEffciencyNewEntity::getElectricAddedValue).collect(Collectors.toList())) ;
        String  value=  StringUtilToll.accumulator(entityList.stream().map(EnergyEffciencyNewEntity::getCapacityAddedValue).collect(Collectors.toList())) ;

        resultVo.setWater(StringUtilToll.div(waterValue,value));
        resultVo.setElectric(StringUtilToll.div(electriValue,value));
        resultVo.setGas(StringUtilToll.div(gasValue,value));
        log.info("打印的输出的结果:{}",resultVo);
        return  resultVo;
    }


    /**
     * 实线部分
     */
    /**
     * 优化后的实线的逻辑
     * @param vo
     * @return
     */
    private List<TrendLineVo> getSolidLineData(TrendParameterVo vo, List<EnergyChartOfBoardEntity> solidLineData)
    {
        List<Long> longs = CommonUtils.getTwoTimePeriods(vo.getStartTime(), vo.getEndTime(), Calendar.HOUR,1);
        Map<Long, List<EnergyChartOfBoardEntity>> map = solidLineData.stream().collect(Collectors.groupingBy(EnergyChartOfBoardEntity::getTs));
        Map<Long, String> longStringMap = solid(map, vo.getKey());
        print("longStringMap打印的===>",longStringMap);
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
     * 处理实线的数据
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
     * 计算值 实线的数据
     * @param pageList
     * @param enums
     * @return
     */
    public String getTotalValue(List<EnergyChartOfBoardEntity> pageList, KeyTitleEnums enums) {

        String capValue =  StringUtilToll.accumulator(pageList.stream().map(EnergyChartOfBoardEntity::getCapacityAddedValue).collect(Collectors.toList()));
        String value =  StringUtilToll.accumulator(pageList.stream().map(m1->{
            if(enums== KeyTitleEnums.key_water)
            {
                return m1.getWaterAddedValue();
            }else if(enums == KeyTitleEnums.key_gas) {
                return m1.getGasAddedValue();
            }
            return m1.getElectricAddedValue();
        }).collect(Collectors.toList()));
        return  StringUtilToll.div(value,capValue);
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
