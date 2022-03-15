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
import org.thingsboard.server.common.data.vo.*;
import org.thingsboard.server.common.data.vo.bodrd.TodaySectionHistoryVo;
import org.thingsboard.server.common.data.vo.device.DictDeviceDataVo;
import org.thingsboard.server.common.data.vo.device.RunningStateVo;
import org.thingsboard.server.common.data.vo.device.input.InputRunningSateVo;
import org.thingsboard.server.common.data.vo.device.out.OutOperationStatusChartDataVo;
import org.thingsboard.server.common.data.vo.device.out.OutOperationStatusChartTsKvDataVo;
import org.thingsboard.server.common.data.vo.device.out.OutRunningStateVo;
import org.thingsboard.server.common.data.vo.device.out.app.OutAppOperationStatusChartDataVo;
import org.thingsboard.server.common.data.vo.device.out.app.OutAppRunnigStateVo;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.common.data.vo.enums.EfficiencyEnums;
import org.thingsboard.server.common.data.vo.enums.KeyTitleEnums;
import org.thingsboard.server.common.data.vo.home.ResultHomeCapAppVo;
import org.thingsboard.server.common.data.vo.home.ResultHomeEnergyAppVo;
import org.thingsboard.server.common.data.vo.parameter.PcTodayEnergyRaningVo;
import org.thingsboard.server.common.data.vo.pc.ResultEnergyTopTenVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.CapacityHistoryVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.common.data.vo.resultvo.devicerun.ResultRunStatusByDeviceVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.AppDeviceEnergyVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.ResultEnergyAppVo;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.PageUtil;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.hs.dao.*;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGraphPropertyVO;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGraphVO;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.hs.service.DeviceDictPropertiesSvc;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.FactoryEntity;
import org.thingsboard.server.dao.model.sqlts.dictionary.TsKvDictionary;
import org.thingsboard.server.dao.model.sqlts.ts.TsKvEntity;
import org.thingsboard.server.dao.sql.device.DeviceRepository;
import org.thingsboard.server.dao.sql.productionline.ProductionLineRepository;
import org.thingsboard.server.dao.sql.role.dao.EffciencyAnalysisRepository;
import org.thingsboard.server.dao.sql.role.dao.EffectHistoryKvRepository;
import org.thingsboard.server.dao.sql.role.dao.EffectTsKvRepository;
import org.thingsboard.server.dao.sql.role.dao.tool.DataToConversionSvc;
import org.thingsboard.server.dao.sql.role.entity.CensusSqlByDayEntity;
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
        deviceDictPropertiesSvc.findAllDictDeviceGroupVO(EfficiencyEnums.ENERGY_002.getgName());
        dictVoList.stream().forEach(dataVo->{
            strings.add(getHomeKeyNameOnlyUtilNeW(dataVo));

        });

        List<DictDeviceGroupPropertyVO>    capList= deviceDictPropertiesSvc.findAllDictDeviceGroupVO(EfficiencyEnums.CAPACITY_001.getgName());
        capList.stream().forEach(dataVo->{
            dataVo.setTitle(KeyTitleEnums.key_capacity.getAbbreviationName());
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
        Map<String,DictDeviceGroupPropertyVO>  mapNameToVo  = deviceDictPropertiesSvc.getMapPropertyVo();
        DeviceEntity deviceInfo =     deviceRepository.findByTenantIdAndId(tenantId.getId(),queryTsKvVo.getDeviceId());
        if(deviceInfo == null)
        {
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"查询不到此设备!");
        }
        String deviceName = deviceInfo.getName();
        //先查询能耗的属性
        List<String>  keys1=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.ENERGY_002.getgName());
        queryTsKvVo.setKeys(keys1);
        Page<Map>  page=  effectHistoryKvRepository.queryEntity(queryTsKvVo, DaoUtil.toPageable(pageLink));
        List<Map> list = page.getContent();
        log.debug("查询当前角色下的用户绑定数据list{}",list);
         if(CollectionUtils.isEmpty(list))
         {
             return new PageData<Map>(page.getContent(), page.getTotalPages(), page.getTotalElements(), page.hasNext());
         }
        List<Map> mapList =   translateTitle(list, deviceName,mapNameToVo);
        return new PageData<Map>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }

    /**
     * 查询产能历史
     * @param queryTsKvVo
     * @param tenantId
     * @param pageLink
     * @return
     */
    @Override
    public  PageData<CapacityHistoryVo> queryCapacityHistory(QueryTsKvHisttoryVo queryTsKvVo, TenantId tenantId, PageLink pageLink) {
//        DeviceEntity deviceInfo =     deviceRepository.findByTenantIdAndId(tenantId.getId(),queryTsKvVo.getDeviceId());
//        if(deviceInfo == null)
//        {
//            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"查询不到此设备!");
//        }
//        String deviceName = deviceInfo.getName();
//        PageData<EnergyHistoryMinuteEntity>  page =  energyHistoryMinuteSvc.queryByDeviceIdAndTs(queryTsKvVo,pageLink);
//        List<EnergyHistoryMinuteEntity> list =   page.getData();
//        List<CapacityHistoryVo> capacityHistoryVos=  EnergyHistoryMinuteEntity.toCapacityHistoryVo(list,deviceName);
//        return new PageData<CapacityHistoryVo>(capacityHistoryVos, page.getTotalPages(), page.getTotalElements(), page.hasNext());

        Map<String,DictDeviceGroupPropertyVO>  mapNameToVo  = deviceDictPropertiesSvc.getMapPropertyVo();
        DeviceEntity deviceInfo =     deviceRepository.findByTenantIdAndId(tenantId.getId(),queryTsKvVo.getDeviceId());
        if(deviceInfo == null)
        {
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"查询不到此设备!");
        }
        String deviceName = deviceInfo.getName();
        //先查询能耗的属性
        List<String>  keys1=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.CAPACITY_001.getgName());
        queryTsKvVo.setKeys(keys1);
        Page<Map>  page=  effectHistoryKvRepository.queryEntity(queryTsKvVo, DaoUtil.toPageable(pageLink));
        List<Map> list = page.getContent();
        log.debug("查询当前角色下的用户绑定数据list{}",list);
        List<CapacityHistoryVo> mapList =   translateTitleCap02(list, deviceName,mapNameToVo);
        return new PageData<CapacityHistoryVo>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext());
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
     * PC端的运行状态接口数据返回
     * @param parameterVo 入参
     * @param tenantId
     * @return key: 遥测数据的key
     */
    @Override
    public List<OutRunningStateVo> queryPcTheRunningStatusByDevice(InputRunningSateVo parameterVo, TenantId tenantId) throws Exception {
        log.debug("查询当前设备的运行状态入参:{}租户id{}",parameterVo,tenantId.getId());
        List<OutRunningStateVo>  resultVo = new ArrayList<>();
        List<RunningStateVo>  runningStateVoList =  parameterVo.getAttributeParameterList();
//         if( CollectionUtils.isEmpty(runningStateVoList))
//         {
//             List<RunningStateVo>  propertiesVos=   queryDictDevice(parameterVo.getDeviceId(),tenantId);
//             runningStateVoList =   propertiesVos.stream().limit(3).collect(Collectors.toList());
//         }
         Map<String,DictDeviceGraphVO> chartIdToKeyNameMap = new HashMap<>();
         List<String>  keyNames = getKeyNameByVoList(runningStateVoList,tenantId,chartIdToKeyNameMap);
       log.debug("查询到的当前设备{}的配置的keyNames属性:{}",parameterVo.getDeviceId(),keyNames);
        List<TsKvDictionary> kvDictionaries= dictionaryRepository.findAllByKeyIn(keyNames);
        log.debug("查询到的当前设备id{}的配置的kvDictionaries属性:{}",parameterVo.getDeviceId(),kvDictionaries);
        List<Integer> keys=   kvDictionaries.stream().map(TsKvDictionary::getKeyId).collect(Collectors.toList());
        Map<Integer, String> mapDict  = kvDictionaries.stream().collect(Collectors.toMap(TsKvDictionary::getKeyId,TsKvDictionary::getKey));
        List<TsKvEntity> entities= tsKvRepository.findAllByKeysAndEntityIdAndStartTimeAndEndTime(parameterVo.getDeviceId(),keys,parameterVo.getStartTime(),parameterVo.getEndTime());
        List<TsKvEntry> tsKvEntries  = new ArrayList<>();
        entities.stream().forEach(tsKvEntity -> {
            tsKvEntity.setStrKey(mapDict.get(tsKvEntity.getKey()));
            tsKvEntries.add(tsKvEntity.toData());
        });
        return  getRunningStatusResults(tsKvEntries,parameterVo,keyNames,chartIdToKeyNameMap);
    }


    /**
     * App端运行状态接口数据返回的封装
     * @param parameterVo
     * @param tenantId
     * @param pageLink
     * @return
     * @throws Exception
     */
    @Override
    public List<OutAppRunnigStateVo> queryAppTheRunningStatusByDevice(AppQueryRunningStatusVo parameterVo, TenantId tenantId, PageLink pageLink) throws Exception {
        //1.优化将app端的入参转换pc端入参;
        InputRunningSateVo  runningSateVo =   new  InputRunningSateVo().toInputRunningSateVoByAppQuery(parameterVo);
        if(CollectionUtils.isEmpty(parameterVo.getAttributes()))
        {
             //首次加载的时候
            List<RunningStateVo>  propertiesVos=   queryDictDevice(parameterVo.getDeviceId(),tenantId);
            propertiesVos =   propertiesVos.stream().limit(3).collect(Collectors.toList());
            runningSateVo.setAttributeParameterList(propertiesVos);
        }
        if(CollectionUtils.isEmpty(runningSateVo.getAttributeParameterList()))
        {
            //分页取不到了;
            return  new ArrayList<>();
        }
        List<OutRunningStateVo>  pcResultVo= queryPcTheRunningStatusByDevice(runningSateVo,tenantId);
        return  pcResultVoToApp(pcResultVo);
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

        List<RunningStateVo>  propertiesVos=   queryDictDevice(vo.getDeviceId(),tenantId);
             log.debug("查询到的当前设备{}的配置的属性条数:{}",vo.getDeviceId(),propertiesVos.size());

        Map<String, RunningStateVo> translateMap = propertiesVos.stream().collect(Collectors.toMap(RunningStateVo::getName, a -> a,(k1,k2)->k1));
        List<String> keyNames= null;// vo.getKeyNames();
        List<String> keyPages = new ArrayList<>();
        if(CollectionUtils.isEmpty(keyNames)) {
            List<String>   keyNames01= propertiesVos.stream().map(RunningStateVo::getName).collect(Collectors.toList());
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
                RunningStateVo trnaslateVo=   translateMap.get(keyName);
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
        List<DictDeviceGraphVO>  graphVOS =  this.dictDeviceService.listDictDeviceGraphs(tenantId, deviceInfo.getDictDeviceId());
//        log.debug("【app运行状态参数列表】查询的数据结果graphVOS：{}",graphVOS);
        List<DictDeviceDataVo>  chartDataList =  conversionOfChartObjects(graphVOS);
        List<DictDeviceDataVo>  chartShowList= chartDataList.stream().filter(s1->s1.getEnable()).collect(Collectors.toList());
        Map<String,List<DictDeviceDataVo>> map  = new HashMap<>();
        if(!CollectionUtils.isEmpty(chartShowList))
        {
            map.put("图表",chartShowList);
        }
        List<DictDeviceDataVo> dictDeviceDataVos = deviceDictPropertiesSvc.findGroupNameAndName(deviceInfo.getDictDeviceId());
        dictDeviceDataVos.stream().forEach(m1->{
            if (StringUtils.isBlank(m1.getTitle())) {
                m1.setTitle(m1.getName());
            }});
        List<DictDeviceDataVo>  partsList = getParts( tenantId,deviceInfo.getDictDeviceId());
        List<DictDeviceDataVo>  devicePropertiesList = filterAlreadyExistsInTheChart(chartDataList,dictDeviceDataVos);//过滤设备的属性
        Map<String,List<DictDeviceDataVo>> map1 = devicePropertiesList.stream().collect(Collectors.groupingBy(DictDeviceDataVo::getGroupName));
        map.putAll(map1);
        map.put("部件",filterAlreadyExistsInTheChart(chartDataList,partsList));
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
    public List<RunningStateVo>  queryDictDevice(UUID deviceId, TenantId tenantId) throws ThingsboardException {
        List<RunningStateVo>    deviceDictionaryPropertiesVos= new ArrayList<>();
        DeviceEntity deviceInfo =     deviceRepository.findByTenantIdAndId(tenantId.getId(),deviceId);
        if(deviceInfo == null )
        {
            throw  new ThingsboardException("查询不到此设备!", ThingsboardErrorCode.FAIL_VIOLATION);
        }
        List<DictDeviceGraphVO>  graphVOS =  this.dictDeviceService.listDictDeviceGraphs(tenantId, deviceInfo.getDictDeviceId());
//        log.debug("查询的数据结果graphVOS：{}",graphVOS);
        List<DictDeviceDataVo> dictDeviceDataVos = deviceDictPropertiesSvc.findGroupNameAndName(deviceInfo.getDictDeviceId());
//        log.debug("查询到的结果dictDeviceDataVos：{}",dictDeviceDataVos);
        if(CollectionUtils.isEmpty(dictDeviceDataVos))
        {
            return deviceDictionaryPropertiesVos;
        }
        List<DictDeviceDataVo>  partsList  =  getParts(tenantId,deviceInfo.getDictDeviceId());
        dictDeviceDataVos.addAll(partsList);
        List<RunningStateVo>  resultList=filterOutSaved(dictDeviceDataVos,graphVOS);
        return resultList;
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
        }
        CensusSqlByDayEntity  nowDate = appleMap.get(localDate);
        if(nowDate != null)
        {
            resultVO.setTodayValue(StringUtilToll.roundUp(nowDate.getIncrementCapacity()));
        }
        resultVO.setHistory(effciencyAnalysisRepository.queryHistoricalTelemetryData(vo,true,KeyTitleEnums.key_capacity.getCode()));
        return resultVO;
    }


    @Override
    public TodaySectionHistoryVo todaySectionHistory(TsSqlDayVo vo) {
        TodaySectionHistoryVo  resultVO = new TodaySectionHistoryVo();
        resultVO.setTodayValue(todayValueOfOutput(vo));
        resultVO.setSectionValue(sectionValueOfOutput(vo));
        resultVO.setHistoryValue(effciencyAnalysisRepository.queryHistoricalTelemetryData(vo,true,KeyTitleEnums.key_capacity.getCode()));
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
          yesterdayMap.put(KeyTitleEnums.key_water.getgName(),(data01 != null?StringUtilToll.roundUp(data01.getIncrementWater()):"0"));
          yesterdayMap.put(KeyTitleEnums.key_cable.getgName(),(data01 != null ? StringUtilToll.roundUp(data01.getIncrementElectric()):"0"));
          yesterdayMap.put(KeyTitleEnums.key_gas.getgName(),(data01 != null ?StringUtilToll.roundUp(data01.getIncrementGas()):"0"));


        CensusSqlByDayEntity  nowDate = appleMap.get(localDate);

       todayMap.put(KeyTitleEnums.key_water.getgName(),(nowDate != null?StringUtilToll.roundUp(nowDate.getIncrementWater()):"0"));
       todayMap.put(KeyTitleEnums.key_cable.getgName(),(nowDate != null ? StringUtilToll.roundUp(nowDate.getIncrementElectric()):"0"));
       todayMap.put(KeyTitleEnums.key_gas.getgName(),(nowDate != null ?StringUtilToll.roundUp(nowDate.getIncrementGas()):"0"));

        resultHomeEnergyAppVo.setHistory(getEnergyHistroyMap(vo));
        resultHomeEnergyAppVo.setTodayValue(todayMap);
        resultHomeEnergyAppVo.setYesterdayValue(yesterdayMap);

        return resultHomeEnergyAppVo;
    }


    @Override
    public List<ResultEnergyTopTenVo> queryPcResultEnergyTopTenVo(PcTodayEnergyRaningVo vo) {
        List<CensusSqlByDayEntity>  entities =  effciencyAnalysisRepository.queryTodayEffceency(vo);
        List<ResultEnergyTopTenVo>  resultEnergyTopTenVoList=   dataVoToResultEnergyTopTenVo(entities,vo);
        return  ResultEnergyTopTenVo.compareToMaxToMin(resultEnergyTopTenVoList);
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


    private   List<CapacityHistoryVo> translateTitleCap02(List<Map> list,String deviceName ,Map<String,DictDeviceGroupPropertyVO>  mapNameToVo )
    {
        List<CapacityHistoryVo> mapList = new ArrayList<CapacityHistoryVo>();

        for(Map m:list)
        {
            CapacityHistoryVo  vo1= new CapacityHistoryVo();
            m.forEach((k,v)->{
                vo1.setDeviceName(deviceName);
                if(k.equals("ts"))
                {
                    vo1.setCreatedTime(v.toString());
                }
                DictDeviceGroupPropertyVO dictVO=  mapNameToVo.get(k);
                if(dictVO != null) {
                    vo1.setValue(v.toString());
                }
            });
            mapList.add(vo1);
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


            map.put(setKeyTitle(mapNameToVo,KeyTitleEnums.key_capacity,true),StringUtilToll.roundUp(vo.getCapacityAddedValue()));//耗气量 (T)

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
        if(enums == KeyTitleEnums.key_capacity)
        {
            groupPropertyVO.setTitle(enums.getAbbreviationName());
        }
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
       // Long t3 = (lastTime - firstTime) / 60000;
        Long t3=1L;
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
    private  List<ResultRunStatusByDeviceVo>  getDefaultValue(Map<String, RunningStateVo> translateMap,String  str)
    {
        List<ResultRunStatusByDeviceVo>  resultList = new ArrayList<>();
        RunningStateVo  properties =   translateMap.get(str);
        ResultRunStatusByDeviceVo  vo = new ResultRunStatusByDeviceVo();
        vo.setTitle(properties.getTitle());
        vo.setKeyName(properties.getName());
        vo.setValue("0");
        vo.setUnit(properties.getUnit());
        resultList.add(vo);
        return  resultList;

    }




    private  List<ResultEnergyTopTenVo>  dataVoToResultEnergyTopTenVo(List<CensusSqlByDayEntity>  entities ,PcTodayEnergyRaningVo vo)
    {
        if(CollectionUtils.isEmpty(entities))
        {
            return  new ArrayList<>();
        }
           KeyTitleEnums enums = KeyTitleEnums.getEnumsByPCCode(vo.getKeyNum());
        return    entities.stream().map(m1 ->{
            ResultEnergyTopTenVo  vo1= new ResultEnergyTopTenVo();
            vo1.setDeviceId(m1.getEntityId());
            vo1.setDeviceName(m1.getDeviceName());
            if(vo.getType().equals("0")){
                vo1.setValue(StringUtils.isNotEmpty(m1.getCapacityAddedValue()) ? m1.getCapacityAddedValue() : "0");
            }else {

                if (enums == KeyTitleEnums.key_water) {
                    vo1.setValue(StringUtils.isNotEmpty(m1.getWaterAddedValue()) ? m1.getWaterAddedValue() : "0");
                }
                if (enums == KeyTitleEnums.key_cable) {
                    vo1.setValue(StringUtils.isNotEmpty(m1.getElectricAddedValue()) ? m1.getElectricAddedValue() : "0");
                }
                if (enums == KeyTitleEnums.key_gas) {
                    vo1.setValue(StringUtils.isNotEmpty(m1.getGasAddedValue()) ? m1.getGasAddedValue() : "0");
                }
            }
            return  vo1;
        }).collect(Collectors.toList());


    }


    /**
     * 运行状态
     *  将图表中的属性 在下拉框中剔除
     */
    private  List<RunningStateVo>   filterOutSaved(List<DictDeviceDataVo> dictDeviceDataVos, List<DictDeviceGraphVO>  graphVOS)
    {
        List<RunningStateVo>   resultList = new ArrayList<>();
        List<RunningStateVo>  runningStateVoList =  dictDeviceDataVos.stream().map(m0->{
            return   RunningStateVo.toDataByDictDeviceDataVo(m0);
        }).collect(Collectors.toList());

        if(CollectionUtils.isEmpty(graphVOS))
        {
           return runningStateVoList;

        }
         Map<String,String> attributesInChartMap = new HashMap<>();
            graphVOS.stream().forEach(m1->{

                    RunningStateVo  vo =  toRunningStateVoByDictDeviceVo(m1);
                    String unit="";

                    List<DictDeviceGraphPropertyVO> dictDeviceGraphPropertyVOList = m1.getProperties();
                    if (!CollectionUtils.isEmpty(dictDeviceGraphPropertyVOList)) {
                      List<String> stringList=  dictDeviceGraphPropertyVOList.stream().map(DictDeviceGraphPropertyVO::getName).collect(Collectors.toList());
                      vo.setAttributeNames(stringList);
                        for (DictDeviceGraphPropertyVO v1 : dictDeviceGraphPropertyVOList) {
                            if(StringUtils.isEmpty(unit))
                            {
                                unit=v1.getUnit();
                            }
                            attributesInChartMap.put(v1.getName(),m1.getName());
                        }
                    }
                    vo.setUnit(unit);
                if(m1.getEnable()) {
                    resultList.add(vo);
                }
            });
        runningStateVoList.stream().forEach(m1->{
            if(StringUtils.isEmpty(attributesInChartMap.get(m1.getName())))
            {
                resultList.add(m1);
            }
        });
        return  resultList;
    }


    private  RunningStateVo  toRunningStateVoByDictDeviceVo(DictDeviceGraphVO vo)
    {
        RunningStateVo  runningStateVo = new RunningStateVo();
        runningStateVo.setTitle(vo.getName());//图表的名称
//        runningStateVo.setName(vo.getName());
        runningStateVo.setChartId(vo.getId()!= null ?vo.getId().toString():"");
         return  runningStateVo;

    }


    /**
     * 获取入参下的keyName
     *  1. 如果 chartId 不为空;就取图表下的属性;  #改为这种方式
     *  2. 如果  attributeNames  为当前的图表下的属性;  ##目前采用这种方式  下拉框的keyName返回的不规范

     * @param voList
     * @return
     */
    private  List<String> getKeyNameByVoList(List<RunningStateVo>  voList,TenantId tenantId,Map<String,DictDeviceGraphVO> chartIdToKeyNameMap)
    {
        List<String>  keyNames = new ArrayList<>();
        voList.stream().forEach(m1 ->{
            if(StringUtils.isNotBlank(m1.getChartId()))
            {
                    //查询图表下的属性
                    UUID  uuid = UUID.fromString(m1.getChartId());
                 try {
                        DictDeviceGraphVO  dictDeviceGraphVO  =  this.dictDeviceService.getDictDeviceGraphDetail(tenantId, uuid);
                        chartIdToKeyNameMap.put(m1.getChartId(),dictDeviceGraphVO);
                         List<DictDeviceGraphPropertyVO>  dictDeviceGraphPropertyVOS =  dictDeviceGraphVO.getProperties();
                        if(!CollectionUtils.isEmpty(dictDeviceGraphPropertyVOS))
                        {
                          List<String> strings =  dictDeviceGraphPropertyVOS.stream().map(DictDeviceGraphPropertyVO::getName).collect(Collectors.toList());
                            keyNames.addAll(strings);
                        }

                    } catch (ThingsboardException e) {
                        e.printStackTrace();
                        log.error("图表id查询的属性异常:{}",e);
                    }
            }else{
                keyNames.add(m1.getName());
            }

        });
        return keyNames;

    }


    /**
     *
     * @param tsKvEntries
     * @param parameterVo   入参的对象
     * @param keyNames
     * @param chartIdToKeyNameMap 图表id 对应的 属性keyName
     * @return
     */
    private  List<OutRunningStateVo>   getRunningStatusResults(List<TsKvEntry> tsKvEntries ,
                                                               InputRunningSateVo parameterVo,
                                                               List<String> keyNames,
                                                               Map<String,DictDeviceGraphVO>  chartIdToKeyNameMap )
    {
        List<OutRunningStateVo>  outRunningStateVos = new ArrayList<>();
        log.debug("封装返回数据");
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
        Map<String,List<ResultRunStatusByDeviceVo>>   map1 =   keyNameNotFound(keyNames,map);

        List<RunningStateVo>   runningStateVoList =   parameterVo.getAttributeParameterList();//入参
        logInfoJson("打印【runningStateVoList】",runningStateVoList);
        runningStateVoList.stream().forEach(m1 ->{
            OutRunningStateVo  outRunningStateVo = new OutRunningStateVo();
            outRunningStateVo.setTableName(m1.getTitle());//如果是属性就是属性的名称
            outRunningStateVo.setKeyName(m1.getName());
            List<OutOperationStatusChartDataVo> properties = new ArrayList<>();
                //代表属性
                if(StringUtils.isBlank(m1.getChartId())) {
                    OutOperationStatusChartDataVo  vo = new  OutOperationStatusChartDataVo();
                    vo.setTitle(m1.getTitle());
                    vo.setUnit(m1.getUnit());
                    List<OutOperationStatusChartTsKvDataVo> tsKvs = new ArrayList<>();
                    List<ResultRunStatusByDeviceVo> runStatusByDeviceVos = map1.get(m1.getName());
                    tsKvs = runStatusByDeviceVos.stream().map(m2 -> {
                        outRunningStateVo.setKeyName(m2.getKeyName());
                        vo.setName(m2.getKeyName());
                        OutOperationStatusChartTsKvDataVo tsKvDataVo = new OutOperationStatusChartTsKvDataVo();
                        tsKvDataVo.setTs(m2.getTime());
                        tsKvDataVo.setValue(m2.getValue());
                        return tsKvDataVo;
                    }).collect(Collectors.toList());
                    vo.setTsKvs(tsKvs);
                    properties.add(vo);
                    outRunningStateVo.setProperties(properties);
                }else {
                    DictDeviceGraphVO graphVO = chartIdToKeyNameMap.get(m1.getChartId());
                    if(graphVO != null ) {
                        outRunningStateVo.setTableName(graphVO.getName());
                        outRunningStateVo.setChartId(m1.getChartId());
                        List<OutOperationStatusChartDataVo> rrlist2 = getTheDataOfTheChart(graphVO, map1);
                        logInfoJson("====最后的结构获取该图表下的属性resultList", rrlist2);
                        outRunningStateVo.setProperties(rrlist2);
                    }
                }
          outRunningStateVos.add(outRunningStateVo);

        });

        return  outRunningStateVos;

    }


    private   List<OutOperationStatusChartDataVo> getTheDataOfTheChart( DictDeviceGraphVO graphVO, Map<String,List<ResultRunStatusByDeviceVo>>   map1)
    {
        List<OutOperationStatusChartDataVo>  resultList = new ArrayList<>();

        List<DictDeviceGraphPropertyVO>  dictDeviceGraphPropertyVOS =  graphVO.getProperties();
        logInfoJson("获取该图表下的属性",dictDeviceGraphPropertyVOS);
        logInfoJson("获取该图表下的属性Map<String,List<ResultRunStatusByDeviceVo>>",map1);

        if(!CollectionUtils.isEmpty(dictDeviceGraphPropertyVOS))
            {
            dictDeviceGraphPropertyVOS.stream().forEach(m1->{
                OutOperationStatusChartDataVo  v2 = new OutOperationStatusChartDataVo();

                List<ResultRunStatusByDeviceVo>  resultRunStatusByDeviceVos =    map1.get(m1.getName());
                logInfoJson("获取该图表下的属性MapresultRunStatusByDeviceVos",resultRunStatusByDeviceVos);

                List<OutOperationStatusChartTsKvDataVo> list3=    resultRunStatusByDeviceVos.stream().map(m2 -> {
                    OutOperationStatusChartTsKvDataVo tsKvDataVo = new OutOperationStatusChartTsKvDataVo();
                    tsKvDataVo.setTs(m2.getTime());
                    tsKvDataVo.setValue(m2.getValue());
                    return tsKvDataVo;
                }).collect(Collectors.toList());
                v2.setTitle(m1.getTitle());
                v2.setName(m1.getName());
                v2.setUnit(m1.getUnit());
                v2.setTsKvs(list3);
                logInfoJson("v2获取该图表下的属性resultList",v2);

                resultList.add(v2);
                logInfoJson("111获取该图表下的属性resultList",resultList);



            });

        }
        logInfoJson("最后的结构获取该图表下的属性resultList",resultList);
        return  resultList;

    }


    /**
     * 将图表的对象转换接口返回的对象 [app]
     */
    private  List<DictDeviceDataVo>  conversionOfChartObjects(List<DictDeviceGraphVO>  graphVOS)
    {
        List<DictDeviceDataVo>  targetObjectList = graphVOS.stream().map(source1 ->{
            DictDeviceDataVo  targetObject = new  DictDeviceDataVo();
            targetObject.setTitle(source1.getName());//图表的名称
            targetObject.setChartId(source1.getId()!=null ?source1.getId().toString():"" );//图表id
            targetObject.setEnable(source1.getEnable());
            List<DictDeviceGraphPropertyVO>  dictDeviceGraphPropertyVOList =  source1.getProperties();
            if(!CollectionUtils.isEmpty(dictDeviceGraphPropertyVOList))
            {
                List<String> attributeNames =dictDeviceGraphPropertyVOList.stream().map(DictDeviceGraphPropertyVO::getName).collect(Collectors.toList());
                targetObject.setAttributeNames(attributeNames);
           String unit=   dictDeviceGraphPropertyVOList.stream().filter(s1->StringUtils.isNotEmpty(s1.getUnit())).findFirst().orElse(new DictDeviceGraphPropertyVO()).getUnit();
                targetObject.setUnit(unit);
            }
           return  targetObject;

        }).collect(Collectors.toList());
        return targetObjectList;
    }


    /**
     * 过滤掉已经在图表中存在的属性
     * @param chartDataList  图表的属性 attributeNames
     * @param dictDeviceDataVos  属性 或者 部件的; 取name
     */
    private  List<DictDeviceDataVo>  filterAlreadyExistsInTheChart(List<DictDeviceDataVo>  chartDataList,List<DictDeviceDataVo> dictDeviceDataVos )
    {
        if(CollectionUtils.isEmpty(chartDataList))
        {
          return dictDeviceDataVos;
        }
        Map<String,String> chartMap= new HashMap<>();
        chartDataList.stream().forEach(s1->{
            List<String>  list =  s1.getAttributeNames();
            if(!CollectionUtils.isEmpty(list))
            {
               list.stream().forEach(str->{
                   chartMap.put(str,str);
               });

            }
        });
        logInfoJson("chartMap打印的输入",chartMap);
        if(CollectionUtils.isEmpty(chartMap))
        {
            return  dictDeviceDataVos;
        }
        List<DictDeviceDataVo>  targetList = new ArrayList<>();
        dictDeviceDataVos.stream().forEach(s2->
        {
            if(StringUtils.isEmpty(chartMap.get(s2.getName())))
            {
                targetList.add(s2);
            }
        });
        return  targetList;
    }


    /**
     * 打印的日志
     * @param str
     * @param obj
     */
    private  void logInfoJson(String str,Object obj)
    {

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(obj);
            log.debug("打印【"+str+"】数据结果:"+json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }


    private  List<OutAppRunnigStateVo>  pcResultVoToApp(List<OutRunningStateVo>  pcResultVo)
    {
        List<OutAppRunnigStateVo>  outAppRunnigStateVos=
        pcResultVo.stream().map(sourceVo -> {
            OutAppRunnigStateVo aapVo = new OutAppRunnigStateVo();
            aapVo.setChartId(sourceVo.getChartId());
            aapVo.setTableName(sourceVo.getTableName());
            List<OutOperationStatusChartDataVo> attributeSourceList = sourceVo.getProperties();
            String chartUnit = attributeSourceList.stream().filter(s1 -> StringUtils.isNotEmpty(s1.getUnit())).findFirst().orElse(new OutOperationStatusChartDataVo()).getUnit();
            aapVo.setChartUnit(chartUnit);//图表的单位

            List<OutAppOperationStatusChartDataVo> propertiesAppList =  attributeSourceList.stream().map(s1->{
                OutAppOperationStatusChartDataVo  t1 = new  OutAppOperationStatusChartDataVo();
                t1.setName(s1.getName());
                t1.setTitle(s1.getTitle());
                t1.setUnit(s1.getUnit());
                List<OutOperationStatusChartTsKvDataVo>  tskvList =   s1.getTsKvs();
                List<List<Object>>  chartTsKv = new ArrayList<>();
                tskvList.stream().forEach(m1->{
                    List<Object>  strings = new ArrayList<>();
                    strings.add(m1.getTs());
                    strings.add(m1.getValue());
                    chartTsKv.add(strings);
                });
                t1.setTsKvs(chartTsKv);
                return  t1;
            }).collect(Collectors.toList());

            aapVo.setProperties(propertiesAppList);


            return aapVo;
        }).collect(Collectors.toList());
      return  outAppRunnigStateVos;
    }


    private  Map<String,String> getEnergyHistroyMap(TsSqlDayVo vo)
    {
        Map<String,String> historyMap  = new HashMap<>();
        setHistoryMapValue(vo,historyMap,KeyTitleEnums.key_water);
        setHistoryMapValue(vo,historyMap,KeyTitleEnums.key_cable);
        setHistoryMapValue(vo,historyMap,KeyTitleEnums.key_gas);
     return  historyMap;
    }

    private void setHistoryMapValue(TsSqlDayVo vo,Map<String,String> historyMap,KeyTitleEnums enums)
    {
        historyMap.put(enums.getgName(),effciencyAnalysisRepository.queryHistoricalTelemetryData(vo,false,enums.getCode()));
    }


    /**
     * 今天的总产能
     * @param vo
     * @return
     */
    private  String  todayValueOfOutput(TsSqlDayVo  vo)
    {
        String value ="0";

        TsSqlDayVo  vo1 = new TsSqlDayVo();
        vo1.setFactoryId(vo.getFactoryId());
        vo1.setTenantId(vo.getTenantId());
        vo1.setWorkshopId(vo.getWorkshopId());
        vo1.setProductionLineId(vo.getProductionLineId());
        vo1.setStartTime(CommonUtils.getZero());
        List<CensusSqlByDayEntity>  entities =  effciencyAnalysisRepository.queryCensusSqlByDay(vo1,true);
        if(CollectionUtils.isEmpty(entities))
        {
           return  value;
        }

        for(CensusSqlByDayEntity m1:entities)
        {
           return StringUtilToll.roundUp(m1.getIncrementCapacity());

        }
       return  value;
    }


    /**
     *
     * @param vo
     * @return
     */
    private  String  sectionValueOfOutput(TsSqlDayVo  vo)
    {
        List<CensusSqlByDayEntity>  entities =  effciencyAnalysisRepository.queryCensusSqlByDay(vo,true);
        if(CollectionUtils.isEmpty(entities))
        {
            return "0";
        }
        List<String> nameList = entities.stream().map(CensusSqlByDayEntity::getIncrementCapacity).collect(Collectors.toList());
        return  StringUtilToll.accumulator(nameList);
    }















}
