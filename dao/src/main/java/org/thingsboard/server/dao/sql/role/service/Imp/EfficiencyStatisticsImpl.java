package org.thingsboard.server.dao.sql.role.service.Imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.effciency.EfficiencyEntityInfo;
import org.thingsboard.server.common.data.effciency.data.EfficiencyHistoryDataVo;
import org.thingsboard.server.common.data.effciency.total.EfficiencyTotalValue;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.page.PageDataAndTotalValue;
import org.thingsboard.server.common.data.page.PageDataWithNextPage;
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
import org.thingsboard.server.dao.attribute.AttributeCullingSvc;
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
import org.thingsboard.server.dao.repository.KanbanInervalCapacityRepository;
import org.thingsboard.server.dao.sql.device.DeviceRepository;
import org.thingsboard.server.dao.sql.productionline.ProductionLineRepository;
import org.thingsboard.server.dao.sql.role.dao.EffciencyAnalysisRepository;
import org.thingsboard.server.dao.sql.role.dao.EffectHistoryKvRepository;
import org.thingsboard.server.dao.sql.role.dao.EffectTsKvRepository;
import org.thingsboard.server.dao.sql.role.dao.PerformanceAnalysisListSvc;
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
 * @description: ???????????????????????????
 * @author: HU.YUNHUI
 * @create: 2021-11-09 11:16
 **/
@Service
@Slf4j
public class EfficiencyStatisticsImpl implements EfficiencyStatisticsSvc {

    @Autowired
    private EffectTsKvRepository effectTsKvRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private FactoryDao factoryDao;
    @Autowired
    private WorkshopRepository workshopRepository;
    @Autowired
    private ProductionLineRepository productionLineRepository;
    @Autowired
    private TsKvRepository tsKvRepository;
    @Autowired
    private DictDeviceService dictDeviceService;
    @Autowired
    private TsKvDictionaryRepository dictionaryRepository;
    @Autowired
    private EffectHistoryKvRepository effectHistoryKvRepository;
    @Autowired
    private DeviceDictPropertiesSvc deviceDictPropertiesSvc;
    @Autowired
    private DictDeviceComponentPropertyRepository componentPropertyRepository;
    @Autowired
    private DictDataRepository dictDataRepository;//????????????
    // ????????????Repository
    @Autowired
    DictDeviceRepository dictDeviceRepository;
    @Autowired
    ClientService clientService;


    @Autowired
    private EffciencyAnalysisRepository effciencyAnalysisRepository;
    @Autowired
    private DataToConversionSvc dataToConversionSvc;
    @Autowired
    private EnergyHistoryMinuteSvc energyHistoryMinuteSvc;
    @Autowired
    private KanbanInervalCapacityRepository kanbanInervalCapacityRepository;
    @Autowired
    private PerformanceAnalysisListSvc performanceAnalysisListSvc;
    @Autowired
    private AttributeCullingSvc attributeCullingSvc;


    private final static String HEADER_0 = "????????????";
    private final static String HEADER_DEVICE_ID = "deviceId";
    private final static String HEADER_1 = "createdTime";//????????????

    private final static String PRE_HISTORY_ENERGY = "??????";//???????????? ???
    private final static String AFTER_HISTORY_ENERGY = "???";//???????????? ???


    /**
     * ????????????????????????????????????????????? ?????????
     *
     * @return
     */
    @Override
    public List<String> queryEntityByKeysHeader() {
        log.debug("????????????????????????????????????????????????");
        List<String> strings = new ArrayList<>();
        strings.add(HEADER_0);
        List<DictDeviceGroupPropertyVO> dictVoList = deviceDictPropertiesSvc.findAllDictDeviceGroupVO(EfficiencyEnums.ENERGY_002.getgName());
        deviceDictPropertiesSvc.findAllDictDeviceGroupVO(EfficiencyEnums.ENERGY_002.getgName());
        dictVoList.stream().forEach(dataVo -> {
            strings.add(getHomeKeyNameOnlyUtilNeW(dataVo));

        });

        List<DictDeviceGroupPropertyVO> capList = deviceDictPropertiesSvc.findAllDictDeviceGroupVO(EfficiencyEnums.CAPACITY_001.getgName());
        capList.stream().forEach(dataVo -> {
            dataVo.setTitle(KeyTitleEnums.key_capacity.getAbbreviationName());
            strings.add(getHomeKeyNameOnlyUtilNeW(dataVo));

        });


        dictVoList.stream().forEach(dataVo -> {
            strings.add(getHomeKeyNameByUtilNeW(dataVo));

        });
        log.debug("???????????????????????????keys1{}", strings);
        return strings;
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    @Override
    public List<String> queryEnergyHistoryHeader() {
        log.debug("???????????????????????????");
        List<String> strings = new ArrayList<>();
        strings.add(HEADER_0);
        List<DictDeviceGroupPropertyVO> dictVoList = deviceDictPropertiesSvc.findAllDictDeviceGroupVO(EfficiencyEnums.ENERGY_002.getgName());
        dictVoList.stream().forEach(dataVo -> {
            strings.add(getHomeKeyNameOnlyUtilNeW(dataVo));

        });
        strings.add(HEADER_1);
        log.debug("???????????????????????????keys1{}", strings);
        return strings;
    }

    /**
     * ??????????????????
     *
     * @param queryTsKvVo
     * @param tenantId
     * @param pageLink
     * @return
     */
    @Override
    public Object queryEnergyHistory(QueryTsKvHisttoryVo queryTsKvVo, TenantId tenantId, PageLink pageLink) {
        Map<String, DictDeviceGroupPropertyVO> mapNameToVo = deviceDictPropertiesSvc.getMapPropertyVo();
        DeviceEntity deviceInfo = deviceRepository.findByTenantIdAndId(tenantId.getId(), queryTsKvVo.getDeviceId());
        if (deviceInfo == null) {
            throw new CustomException(ActivityException.FAILURE_ERROR.getCode(), "?????????????????????!");
        }
        String deviceName = deviceInfo.getRename();
        //????????????????????????
        List<String> keys1 = deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.ENERGY_002.getgName());
        queryTsKvVo.setKeys(keys1);
        Page<Map> page = effectHistoryKvRepository.queryEntity(queryTsKvVo, DaoUtil.toPageable(pageLink));
        List<Map> list = page.getContent();
        log.debug("??????????????????????????????????????????list{}", list);
        if (CollectionUtils.isEmpty(list)) {
            return new PageDataWithNextPage<Map>(page.getContent(), page.getTotalPages(), page.getTotalElements(), page.hasNext(), null);
        }
        List<Map> mapList = translateTitle(list, deviceName, mapNameToVo);
        if (page.hasNext()) {
            Page<Map> page1 = effectHistoryKvRepository.queryEntity(queryTsKvVo, DaoUtil.toPageable(pageLink.nextPageLink()));
            List<Map> mapList1 = page1.getContent();
            List<Map> mapList2 = translateTitle(mapList1, deviceName, mapNameToVo);
            Map map = mapList2.stream().findFirst().orElse(null);
            return new PageDataWithNextPage<Map>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext(), map);
        }
        return new PageDataWithNextPage<Map>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext(), null);
    }

    @Override
    public PageDataWithNextPage<EfficiencyHistoryDataVo> queryEnergyHistoryNew(QueryTsKvHisttoryVo queryTsKvVo, TenantId tenantId, PageLink pageLink) {
        DeviceEntity deviceInfo = deviceRepository.findByTenantIdAndId(tenantId.getId(), queryTsKvVo.getDeviceId());
        if (deviceInfo == null) {
            throw new CustomException(ActivityException.FAILURE_ERROR.getCode(), "?????????????????????!");
        }
        String deviceName = deviceInfo.getRename();
        //????????????????????????
        List<String> keys1 = deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.ENERGY_002.getgName());
        queryTsKvVo.setKeys(keys1);
        Page<Map> page = effectHistoryKvRepository.queryEntity(queryTsKvVo, DaoUtil.toPageable(pageLink));
        List<Map> list = page.getContent();
        log.debug("??????????????????????????????????????????list{}", list);
        if (CollectionUtils.isEmpty(list)) {
            return new PageDataWithNextPage<EfficiencyHistoryDataVo>(new ArrayList<EfficiencyHistoryDataVo>(), page.getTotalPages(), page.getTotalElements(), page.hasNext(), null);
        }
        List<EfficiencyHistoryDataVo> mapList = translateTitleNew(list, deviceName);
        if (page.hasNext()) {
            Page<Map> page1 = effectHistoryKvRepository.queryEntity(queryTsKvVo, DaoUtil.toPageable(pageLink.nextPageLink()));
            List<Map> mapList1 = page1.getContent();
            List<EfficiencyHistoryDataVo> mapList2 = translateTitleNew(mapList1, deviceName);
            EfficiencyHistoryDataVo map = mapList2.stream().findFirst().orElse(null);
            return new PageDataWithNextPage<EfficiencyHistoryDataVo>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext(), map);
        }
        return new PageDataWithNextPage<EfficiencyHistoryDataVo>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext(), null);
    }


    /**
     * ??????????????????
     *
     * @param queryTsKvVo
     * @param tenantId
     * @param pageLink
     * @return
     */
    @Override
    public PageDataWithNextPage<CapacityHistoryVo> queryCapacityHistory(QueryTsKvHisttoryVo queryTsKvVo, TenantId tenantId, PageLink pageLink) {
        Map<String, DictDeviceGroupPropertyVO> mapNameToVo = deviceDictPropertiesSvc.getMapPropertyVo();
        DeviceEntity deviceInfo = deviceRepository.findByTenantIdAndId(tenantId.getId(), queryTsKvVo.getDeviceId());
        if (deviceInfo == null) {
            throw new CustomException(ActivityException.FAILURE_ERROR.getCode(), "?????????????????????!");
        }
        String deviceName = deviceInfo.getRename();
        //????????????????????????
        List<String> keys1 = deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.CAPACITY_001.getgName());
        queryTsKvVo.setKeys(keys1);
        Page<Map> page = effectHistoryKvRepository.queryEntity(queryTsKvVo, DaoUtil.toPageable(pageLink));
        List<Map> list = page.getContent();
        log.debug("??????????????????????????????????????????list{}", list);
        List<CapacityHistoryVo> mapList = translateTitleCap02(list, deviceName, mapNameToVo);
        if (page.hasNext()) {
            Pageable pageable = DaoUtil.toPageable(pageLink.nextPageLink());
            Page<Map> pageNext = effectHistoryKvRepository.queryEntity(queryTsKvVo, pageable);
            List<Map> list1Next = pageNext.getContent();
            List<CapacityHistoryVo> mapListNext = translateTitleCap02(list1Next, deviceName, mapNameToVo);
            CapacityHistoryVo vo = mapListNext.stream().findFirst().orElse(null);
            return new PageDataWithNextPage<CapacityHistoryVo>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext(), vo);
        }
        return new PageDataWithNextPage<CapacityHistoryVo>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext(), null);
    }


    /**
     * @param vo
     * @param tenantId
     * @param pageLink
     * @return
     */
    @Override
    public PageDataAndTotalValue<AppDeviceCapVo> queryPCCapAppNewMethod(QueryTsKvVo vo, TenantId tenantId, PageLink pageLink) {
        log.debug("queryPCCapAppNewMethod???????????????pc???????????????????????????:{}??????id{}", vo, tenantId);
        if (vo.getFactoryId() == null) {
            vo.setFactoryId(getFirstFactory(tenantId));
        }
        List<EnergyEffciencyNewEntity> entityList1 = effciencyAnalysisRepository.queryCapacityALL(vo, pageLink);
        List<EnergyEffciencyNewEntity> entityList = orderByCapPacityValue(entityList1);
        Page<EnergyEffciencyNewEntity> page = PageUtil.createPageFromList(entityList, pageLink);
        List<EnergyEffciencyNewEntity> pageList = page.getContent();
        //????????????????????????????????????????????????
        List<AppDeviceCapVo> appDeviceCapVos = dataToConversionSvc.resultProcessingByCapacityPc(pageList, tenantId);
        return new PageDataAndTotalValue<AppDeviceCapVo>(dataToConversionSvc.getTotalValue(entityList), appDeviceCapVos, page.getTotalPages(), page.getTotalElements(), page.hasNext());

    }


    /**
     * @param vo
     * @param tenantId
     * @param pageLink
     * @return
     */
    @Override
    public PageDataAndTotalValue<AppDeviceCapVo> queryCapacityOnSecondLeve(QueryTsKvVo vo, TenantId tenantId, PageLink pageLink) {
        if (vo.getFactoryId() == null) {
            vo.setFactoryId(getFirstFactory(tenantId));
        }
        List<EnergyEffciencyNewEntity> entityList1 = performanceAnalysisListSvc.yieldList(vo);
        List<EnergyEffciencyNewEntity> entityList = orderByCapPacityValue(entityList1);
        Page<EnergyEffciencyNewEntity> page = PageUtil.createPageFromList(entityList, pageLink);
        List<EnergyEffciencyNewEntity> pageList = page.getContent();
        //????????????????????????????????????????????????
        List<AppDeviceCapVo> appDeviceCapVos = dataToConversionSvc.resultProcessingByCapacityPc(pageList, tenantId);
        return new PageDataAndTotalValue<AppDeviceCapVo>(dataToConversionSvc.getTotalValue(entityList), appDeviceCapVos, page.getTotalPages(), page.getTotalElements(), page.hasNext());

    }

    private List<EnergyEffciencyNewEntity> orderByCapPacityValue(List<EnergyEffciencyNewEntity> entityList1) {
        //?????????????????????0;
        return entityList1.stream().sorted((s1, s2) -> strToBigDecimal(s2.getCapacityAddedValue()).compareTo(strToBigDecimal(s1.getCapacityAddedValue()))).collect(Collectors.toList());

    }


    private List<EnergyEffciencyNewEntity> orderByAllValue(List<EnergyEffciencyNewEntity> entityList1) {
        //?????????????????????0;
        return entityList1.stream()
                .sorted((s1, s2) -> strToBigDecimal(s2.getCapacityAddedValue()).compareTo(strToBigDecimal(s1.getCapacityAddedValue())))
                .sorted((s1, s2) -> strToBigDecimal(s2.getGasAddedValue()).compareTo(strToBigDecimal(s1.getGasAddedValue())))
                .sorted((s1, s2) -> strToBigDecimal(s2.getElectricAddedValue()).compareTo(strToBigDecimal(s1.getElectricAddedValue())))
                .sorted((s1, s2) -> strToBigDecimal(s2.getWaterAddedValue()).compareTo(strToBigDecimal(s1.getWaterAddedValue())))
                .collect(Collectors.toList());

    }


    private BigDecimal strToBigDecimal(String str) {
        if (StringUtils.isEmpty(str)) {
            return new BigDecimal("0");
        }
        BigDecimal value = new BigDecimal(str);
        if (value.signum() == -1) {
            return new BigDecimal("0");
        }
        return new BigDecimal(str);
    }

    /**
     * pc??????????????????
     *
     * @param vo
     * @param tenantId
     * @param pageLink
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public PageDataAndTotalValue<Map> queryEntityByKeysNewMethod(QueryTsKvVo vo, TenantId tenantId, PageLink pageLink) throws JsonProcessingException {
//        log.debug("queryEntityByKeysNewMethod???????????????pc???????????????????????????:{}??????id{}",vo,tenantId);
        if (vo.getFactoryId() == null) {
            vo.setFactoryId(getFirstFactory(tenantId));
        }
        Map<String, DictDeviceGroupPropertyVO> mapNameToVo = deviceDictPropertiesSvc.getMapPropertyVoByTitle();
        List<EnergyEffciencyNewEntity> entityList = effciencyAnalysisRepository.queryEnergyListAll(vo, pageLink);
        Page<EnergyEffciencyNewEntity> page = PageUtil.createPageFromList(entityList, pageLink);
        List<EnergyEffciencyNewEntity> pageList = page.getContent();
        //???????????????????????????????????????????????? ??????????????????
        List<Map> appDeviceCapVos = this.resultProcessingByEnergyPc(pageList, mapNameToVo);
        List<String> totalValueList = getTotalValueNewMethod(entityList, mapNameToVo);
        return new PageDataAndTotalValue<Map>(totalValueList, appDeviceCapVos, page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }


    @Override
    public PageDataAndTotalValue<EfficiencyEntityInfo> queryEntityByKeysNew(QueryTsKvVo queryTsKvVo, TenantId tenantId, PageLink pageLink) throws JsonProcessingException {
        if (queryTsKvVo.getFactoryId() == null) {
            queryTsKvVo.setFactoryId(getFirstFactory(tenantId));
        }
        //????????????????????????
        List<EnergyEffciencyNewEntity> entityList = effciencyAnalysisRepository.queryEnergyListAll(queryTsKvVo, pageLink);
        //??????
        Page<EnergyEffciencyNewEntity> page = PageUtil.createPageFromList(entityList, pageLink);
        //???????????????
        List<EnergyEffciencyNewEntity> pageList = page.getContent();
        List<EfficiencyEntityInfo> efficiencyEntityInfoList = this.resultProcessingByEnergyPcNew(pageList);
        EfficiencyTotalValue efficiencyTotalValue = getTotalValueNewMethodnew(entityList);
        return new PageDataAndTotalValue<EfficiencyEntityInfo>(efficiencyTotalValue, efficiencyEntityInfoList, page.getTotalPages(), page.getTotalElements(), page.hasNext());

    }

    @Override
    public PageDataAndTotalValue<EfficiencyEntityInfo> queryEntityByKeysNewOnSecondLeve(QueryTsKvVo queryTsKvVo, TenantId tenantId, PageLink pageLink) throws JsonProcessingException {
        if (queryTsKvVo.getFactoryId() == null) {
            queryTsKvVo.setFactoryId(getFirstFactory(tenantId));
        }
        List<EnergyEffciencyNewEntity> entityList1 = performanceAnalysisListSvc.queryEnergyListAll(queryTsKvVo);
        List<EnergyEffciencyNewEntity> entityList = orderByAllValue(entityList1);
        Page<EnergyEffciencyNewEntity> page = PageUtil.createPageFromList(entityList, pageLink);
        List<EnergyEffciencyNewEntity> pageList = page.getContent();
        List<EfficiencyEntityInfo> efficiencyEntityInfoList = this.resultProcessingByEnergyPcNew(pageList);
        //????????????????????????????????????????????????
        EfficiencyTotalValue efficiencyTotalValue = getTotalValueNewMethodnew(entityList1);
        return new PageDataAndTotalValue<EfficiencyEntityInfo>(efficiencyTotalValue, efficiencyEntityInfoList, page.getTotalPages(), page.getTotalElements(), page.hasNext());

    }

    @Override
    public ResultCapAppVo queryCapAppNewMethod(QueryTsKvVo queryTsKvVo, TenantId tenantId, PageLink pageLink) {
        ResultCapAppVo resultCapAppVo = new ResultCapAppVo();
        //app???????????????Pc???
        PageDataAndTotalValue<AppDeviceCapVo> pageDataAndTotalValue = this.queryPCCapAppNewMethod(queryTsKvVo, tenantId, pageLink);
        List<AppDeviceCapVo> data = pageDataAndTotalValue.getData();
        String totalValue = pageDataAndTotalValue.getTotalValue().toString();
        resultCapAppVo.setTotalValue(totalValue);
        resultCapAppVo.setAppDeviceCapVoList(dataToConversionSvc.fillDevicePicture(data, tenantId));
        return resultCapAppVo;
    }

    /**
     * App??????????????????
     *
     * @param vo
     * @param tenantId
     * @param pageLink
     * @return
     */
    @Override
    public ResultEnergyAppVo queryAppEntityByKeysNewMethod(QueryTsKvVo vo, TenantId tenantId, PageLink pageLink, Boolean flg) {
        ResultEnergyAppVo result = new ResultEnergyAppVo();
        log.debug("???APP??????queryAppEntityByKeysNewMethod???????????????pc???????????????????????????:{}??????id{}", vo, tenantId);
        if (vo.getFactoryId() == null && flg) {
            vo.setFactoryId(getFirstFactory(tenantId));
        }
        Map<String, DictDeviceGroupPropertyVO> mapNameToVo = deviceDictPropertiesSvc.getMapPropertyVoByTitle();
        List<EnergyEffciencyNewEntity> entityList = effciencyAnalysisRepository.queryEnergyListAll(vo, pageLink);
        Page<EnergyEffciencyNewEntity> page = PageUtil.createPageFromList(entityList, pageLink);
        List<EnergyEffciencyNewEntity> pageList = page.getContent();
        //???????????????????????????????????????????????? ??????????????????
        List<AppDeviceEnergyVo> appDeviceCapVos = dataToConversionSvc.resultProcessingByEnergyApp(pageList, mapNameToVo, tenantId);
        result.setAppDeviceCapVoList(appDeviceCapVos);
        result.setTotalValue(getTotalValueApp(entityList, mapNameToVo));
        return result;
    }


    /**
     * PC????????????????????????????????????
     *
     * @param parameterVo ??????
     * @param tenantId
     * @return key: ???????????????key
     */
    @Override
    public List<OutRunningStateVo> queryPcTheRunningStatusByDevice(InputRunningSateVo parameterVo, TenantId tenantId,boolean isFactoryUser) throws CustomException, ThingsboardException {
        log.debug("???????????????????????????????????????:{}??????id{}", parameterVo, tenantId.getId());
        List<OutRunningStateVo> resultVo = new ArrayList<>();
        List<RunningStateVo> runningStateVoList = parameterVo.getAttributeParameterList();
        if (CollectionUtils.isEmpty(runningStateVoList)) {
            List<RunningStateVo> propertiesVos = queryDictDevice(parameterVo.getDeviceId(), tenantId,isFactoryUser);
            runningStateVoList = propertiesVos.stream().limit(1).collect(Collectors.toList());
            parameterVo.setAttributeParameterList(runningStateVoList);
        }
        Map<String, DictDeviceGraphVO> chartIdToKeyNameMap = new HashMap<>();
        List<String> keyNames = getKeyNameByVoList(runningStateVoList, tenantId, chartIdToKeyNameMap);
        log.debug("????????????????????????{}????????????keyNames??????:{}", parameterVo.getDeviceId(), keyNames);
        List<TsKvDictionary> kvDictionaries = dictionaryRepository.findAllByKeyIn(keyNames);
        log.debug("????????????????????????id{}????????????kvDictionaries??????:{}", parameterVo.getDeviceId(), kvDictionaries);
        List<Integer> keys = kvDictionaries.stream().map(TsKvDictionary::getKeyId).collect(Collectors.toList());
        Map<Integer, String> mapDict = kvDictionaries.stream().collect(Collectors.toMap(TsKvDictionary::getKeyId, TsKvDictionary::getKey));
        long cout = tsKvRepository.countByKeysAndEntityIdAndStartTimeAndEndTime(parameterVo.getDeviceId(), keys, parameterVo.getStartTime(), parameterVo.getEndTime());
        if (cout > DataConstants.MAX_QUERY_COUNT) {
            throw new CustomException(ActivityException.MAX_QUERY_ERROR.getCode(), ActivityException.MAX_QUERY_ERROR.getMessage());
        }
        List<TsKvEntity> entities = tsKvRepository.findAllByKeysAndEntityIdAndStartTimeAndEndTime(parameterVo.getDeviceId(), keys, parameterVo.getStartTime(), parameterVo.getEndTime());
        logInfoJson("entities???????????????json{}", entities);
        List<TsKvEntity> entities1 = getExcludeZero(entities);
        List<TsKvEntry> tsKvEntries = new ArrayList<>();
        entities1.stream().forEach(tsKvEntity -> {
            tsKvEntity.setStrKey(mapDict.get(tsKvEntity.getKey()));
            tsKvEntries.add(tsKvEntity.toData());
        });
        logInfoJson("entities???????????????jsontsKvEntries{}", tsKvEntries);
        return getRunningStatusResults(tsKvEntries, parameterVo, keyNames, chartIdToKeyNameMap);
    }


    /**
     * App??????????????????????????????????????????
     *
     * @param parameterVo
     * @param tenantId
     * @param pageLink
     * @return
     * @throws Exception
     */
    @Override
    public List<OutAppRunnigStateVo> queryAppTheRunningStatusByDevice(AppQueryRunningStatusVo parameterVo, TenantId tenantId, PageLink pageLink,boolean isFactoryUser) throws Exception {
        //1.?????????app??????????????????pc?????????;
        InputRunningSateVo runningSateVo = new InputRunningSateVo().toInputRunningSateVoByAppQuery(parameterVo);
        if (CollectionUtils.isEmpty(parameterVo.getAttributes())) {
            //?????????????????????
            List<RunningStateVo> propertiesVos = queryDictDevice(parameterVo.getDeviceId(), tenantId,isFactoryUser);
            propertiesVos = propertiesVos.stream().limit(1).collect(Collectors.toList());
            runningSateVo.setAttributeParameterList(propertiesVos);
        }
        if (CollectionUtils.isEmpty(runningSateVo.getAttributeParameterList())) {
            //??????????????????;
            return new ArrayList<>();
        }
        List<OutRunningStateVo> pcResultVo = queryPcTheRunningStatusByDevice(runningSateVo, tenantId,isFactoryUser);
        return pcResultVoToApp(pcResultVo);
    }

    /**
     * dictDeviceId
     *
     * @param vo
     * @param tenantId
     * @return
     */
    @Override
    public Map<String, List<ResultRunStatusByDeviceVo>> queryTheRunningStatusByDevice(AppQueryRunningStatusVo vo, TenantId tenantId, PageLink pageLink,boolean isFactoryUser) throws ThingsboardException {
        log.debug("???????????????????????????????????????:{}??????id{}", vo, tenantId.getId());

        List<RunningStateVo> propertiesVos = queryDictDevice(vo.getDeviceId(), tenantId,isFactoryUser);
        log.debug("????????????????????????{}????????????????????????:{}", vo.getDeviceId(), propertiesVos.size());

        Map<String, RunningStateVo> translateMap = propertiesVos.stream().collect(Collectors.toMap(RunningStateVo::getName, a -> a, (k1, k2) -> k1));
        List<String> keyNames = null;// vo.getKeyNames();
        List<String> keyPages = new ArrayList<>();
        if (CollectionUtils.isEmpty(keyNames)) {
            List<String> keyNames01 = propertiesVos.stream().map(RunningStateVo::getName).collect(Collectors.toList());
            keyPages = keyNames01.stream().limit(3).collect(Collectors.toList());
        } else {
            keyPages = keyNames.stream().skip((vo.getPage()) * vo.getPageSize()).limit(vo.getPageSize()).collect(Collectors.toList());

        }

        List<TsKvDictionary> kvDictionaries = dictionaryRepository.findAllByKeyIn(keyPages);
        log.debug("????????????????????????{}????????????kvDictionaries??????:{}", vo.getDeviceId(), kvDictionaries);
        List<Integer> keys = kvDictionaries.stream().map(TsKvDictionary::getKeyId).collect(Collectors.toList());
        Map<Integer, String> mapDict = kvDictionaries.stream().collect(Collectors.toMap(TsKvDictionary::getKeyId, TsKvDictionary::getKey));
        log.debug("????????????????????????{}????????????keys??????:{}###mapDict:{}", vo.getDeviceId(), keys, mapDict);
        List<TsKvEntity> entities = tsKvRepository.findAllByKeysAndEntityIdAndStartTimeAndEndTime(vo.getDeviceId(), keys, vo.getStartTime(), vo.getEndTime());
        log.debug("????????????????????????{}????????????entities??????:{}", vo.getDeviceId(), entities);
        List<TsKvEntry> tsKvEntries = new ArrayList<>();
        entities.stream().forEach(tsKvEntity -> {
            tsKvEntity.setStrKey(mapDict.get(tsKvEntity.getKey()));
            tsKvEntries.add(tsKvEntity.toData());
        });
        List<ResultRunStatusByDeviceVo> voList = new ArrayList<>();
        voList = tsKvEntries.stream().map(TsKvEntry -> {
            ResultRunStatusByDeviceVo byDeviceVo = new ResultRunStatusByDeviceVo();
            String keyName = TsKvEntry.getKey();
            RunningStateVo trnaslateVo = translateMap.get(keyName);
            byDeviceVo.setKeyName(keyName);
            byDeviceVo.setValue(StringUtilToll.roundUp(TsKvEntry.getValue().toString()));
            byDeviceVo.setTime(TsKvEntry.getTs());
            byDeviceVo.setTitle(trnaslateVo != null ? trnaslateVo.getTitle() : "");
            byDeviceVo.setUnit(trnaslateVo != null ? trnaslateVo.getUnit() : "");
            return byDeviceVo;
        }).collect(Collectors.toList());
        Map<String, List<ResultRunStatusByDeviceVo>> map = voList.stream().collect(Collectors.groupingBy(ResultRunStatusByDeviceVo::getKeyName));
        keyPages.stream().forEach(str -> {
            List<ResultRunStatusByDeviceVo> voList1 = map.get(str);
            if (CollectionUtils.isEmpty(voList1)) {
                map.put(str, getDefaultValue(translateMap, str));
            }
        });
        log.debug("???????????????????????????:{}", map);
        log.debug("????????????????????????{}????????????keyNames??????:{}", vo.getDeviceId(), keyNames);

        return map;
    }

    /**
     * ???????????????????????????
     *
     * @param deviceId
     * @return
     */
    @Override
    public Object queryGroupDict(UUID deviceId, TenantId tenantId,boolean isFactoryUser) {
        DeviceEntity deviceInfo = deviceRepository.findByTenantIdAndId(tenantId.getId(), deviceId);
        if (deviceInfo == null) {
            throw new CustomException(ActivityException.FAILURE_ERROR.getCode(), "?????????????????????!");

        }
        List<DictDeviceGraphVO> graphVOS = this.dictDeviceService.listDictDeviceGraphs(tenantId, deviceInfo.getDictDeviceId());
//        log.debug("???app????????????????????????????????????????????????graphVOS???{}",graphVOS);
        List<DictDeviceDataVo> chartDataList = conversionOfChartObjects(graphVOS);
        List<DictDeviceDataVo> chartShowList = chartDataList.stream().filter(s1 -> s1.getEnable()).collect(Collectors.toList());
        Map<String, List<DictDeviceDataVo>> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(chartShowList)) {
            map.put("??????", chartShowList);
        }
        List<DictDeviceDataVo> dictDeviceDataVos = deviceDictPropertiesSvc.findGroupNameAndName(deviceInfo.getDictDeviceId());
        dictDeviceDataVos.stream().forEach(m1 -> {
            if (StringUtils.isBlank(m1.getTitle())) {
                m1.setTitle(m1.getName());
            }
        });
        List<DictDeviceDataVo> partsList = getParts(tenantId, deviceInfo.getDictDeviceId());
        List<DictDeviceDataVo> devicePropertiesList = filterAlreadyExistsInTheChart(chartDataList, dictDeviceDataVos);//?????????????????????
        Map<String, List<DictDeviceDataVo>> map1 = devicePropertiesList.stream().collect(Collectors.groupingBy(DictDeviceDataVo::getGroupName));
        map.putAll(map1);
        map.put("??????", filterAlreadyExistsInTheChart(chartDataList, partsList));
        return attributeCullingSvc.toMakeToMap(map,tenantId,deviceId,isFactoryUser);
    }





    /**
     * @param deviceId ??????id
     * @param tenantId ??????id
     * @return
     * @throws ThingsboardException
     */
    @Override
    public List<RunningStateVo> queryDictDevice(UUID deviceId, TenantId tenantId,boolean isFactoryUser) throws ThingsboardException {
        List<RunningStateVo> deviceDictionaryPropertiesVos = new ArrayList<>();
        DeviceEntity deviceInfo = deviceRepository.findByTenantIdAndId(tenantId.getId(), deviceId);
        if (deviceInfo == null) {
            throw new ThingsboardException("?????????????????????!", ThingsboardErrorCode.FAIL_VIOLATION);
        }
        List<DictDeviceGraphVO> graphVOS = this.dictDeviceService.listDictDeviceGraphs(tenantId, deviceInfo.getDictDeviceId());
//        log.debug("?????????????????????graphVOS???{}",graphVOS);
        List<DictDeviceDataVo> dictDeviceDataVos = deviceDictPropertiesSvc.findGroupNameAndName(deviceInfo.getDictDeviceId());
//        log.debug("??????????????????dictDeviceDataVos???{}",dictDeviceDataVos);
        if (CollectionUtils.isEmpty(dictDeviceDataVos)) {
            return deviceDictionaryPropertiesVos;
        }
        List<DictDeviceDataVo> partsList = getParts(tenantId, deviceInfo.getDictDeviceId());
        dictDeviceDataVos.addAll(partsList);
        List<RunningStateVo> resultList = filterOutSaved(dictDeviceDataVos, graphVOS);
        return attributeCullingSvc.queryKeyToSwitch(resultList, tenantId, deviceId,isFactoryUser);
    }


    /**
     * ?????? ?????? ?????????????????????
     *
     * @param vo
     * @return
     */
    @Override
    public ResultHomeCapAppVo queryThreePeriodsCapacity(TsSqlDayVo vo) {
        ResultHomeCapAppVo resultVO = new ResultHomeCapAppVo();
        if (vo.getStartTime() == null)  //????????????????????????????????????
        {
            vo.setStartTime(CommonUtils.getYesterdayZero());
        }
        List<CensusSqlByDayEntity> entities = effciencyAnalysisRepository.queryCensusSqlByDay(vo, true);
        Map<LocalDate, CensusSqlByDayEntity> appleMap = entities.stream().collect(Collectors.toMap(CensusSqlByDayEntity::getDate, a -> a, (k1, k2) -> k1));
        LocalDate localDate = LocalDate.now();
        LocalDate yesterday = localDate.plusDays(-1);
        CensusSqlByDayEntity data01 = appleMap.get(yesterday);
        if (data01 != null) {
            resultVO.setYesterdayValue(StringUtilToll.roundUp(data01.getIncrementCapacity()));
        }
        CensusSqlByDayEntity nowDate = appleMap.get(localDate);
        if (nowDate != null) {
            resultVO.setTodayValue(StringUtilToll.roundUp(nowDate.getIncrementCapacity()));
        }
        resultVO.setHistory(effciencyAnalysisRepository.queryHistoricalTelemetryData(vo, true, KeyTitleEnums.key_capacity.getCode()));
        return resultVO;
    }


    @Override
    public TodaySectionHistoryVo todaySectionHistory(TsSqlDayVo vo) {
//        TodaySectionHistoryVo  resultVO = new TodaySectionHistoryVo();
//        resultVO.setTodayValue(todayValueOfOutput(vo));
//        resultVO.setSectionValue(sectionValueOfOutput(vo));
//        resultVO.setHistoryValue(effciencyAnalysisRepository.queryHistoricalTelemetryData(vo,true,KeyTitleEnums.key_capacity.getCode()));
//        return resultVO;
        TodaySectionHistoryVo resultVo = new TodaySectionHistoryVo();
        resultVo.setSectionValue(kanbanInervalCapacityRepository.capacitySumValue(vo, vo.getStartTime(), vo.getEndTime()));
        resultVo.setTodayValue(kanbanInervalCapacityRepository.capacitySumValue(vo, CommonUtils.getZero(), CommonUtils.getNowTime()));
        resultVo.setHistoryValue(kanbanInervalCapacityRepository.capacitySumValue(vo, null, null));
        return resultVo;
    }

    /**
     * ?????? ?????? ????????? ??????  app
     *
     * @param vo
     * @return
     */
    @Override
    public ResultHomeEnergyAppVo queryAppThreePeriodsEnergy(TsSqlDayVo vo) {
        ResultHomeEnergyAppVo resultHomeEnergyAppVo = new ResultHomeEnergyAppVo();
        Map<String, String> yesterdayMap = new HashMap<>();
        Map<String, String> todayMap = new HashMap<>();

        if (vo.getStartTime() == null)  //????????????????????????????????????
        {
            vo.setStartTime(CommonUtils.getYesterdayZero());
        }
        List<CensusSqlByDayEntity> entities = effciencyAnalysisRepository.queryCensusSqlByDay(vo, false);
        Map<LocalDate, CensusSqlByDayEntity> appleMap = entities.stream().collect(Collectors.toMap(CensusSqlByDayEntity::getDate, a -> a, (k1, k2) -> k1));
        log.debug("queryAppThreePeriodsEnergy.appleMap:{}", appleMap);

        LocalDate localDate = LocalDate.now();
        LocalDate yesterday = localDate.plusDays(-1);
        CensusSqlByDayEntity data01 = appleMap.get(yesterday);
        yesterdayMap.put(KeyTitleEnums.key_water.getgName(), (data01 != null ? StringUtilToll.roundUp(data01.getIncrementWater()) : "0"));
        yesterdayMap.put(KeyTitleEnums.key_cable.getgName(), (data01 != null ? StringUtilToll.roundUp(data01.getIncrementElectric()) : "0"));
        yesterdayMap.put(KeyTitleEnums.key_gas.getgName(), (data01 != null ? StringUtilToll.roundUp(data01.getIncrementGas()) : "0"));


        CensusSqlByDayEntity nowDate = appleMap.get(localDate);

        todayMap.put(KeyTitleEnums.key_water.getgName(), (nowDate != null ? StringUtilToll.roundUp(nowDate.getIncrementWater()) : "0"));
        todayMap.put(KeyTitleEnums.key_cable.getgName(), (nowDate != null ? StringUtilToll.roundUp(nowDate.getIncrementElectric()) : "0"));
        todayMap.put(KeyTitleEnums.key_gas.getgName(), (nowDate != null ? StringUtilToll.roundUp(nowDate.getIncrementGas()) : "0"));

        resultHomeEnergyAppVo.setHistory(getEnergyHistroyMap(vo));
        resultHomeEnergyAppVo.setTodayValue(todayMap);
        resultHomeEnergyAppVo.setYesterdayValue(yesterdayMap);

        return resultHomeEnergyAppVo;
    }


    @Override
    public List<ResultEnergyTopTenVo> queryPcResultEnergyTopTenVo(PcTodayEnergyRaningVo vo) {
        List<CensusSqlByDayEntity> entities = effciencyAnalysisRepository.queryTodayEffceency(vo);
        List<ResultEnergyTopTenVo> resultEnergyTopTenVoList = dataVoToResultEnergyTopTenVo(entities, vo);
        return ResultEnergyTopTenVo.compareToMaxToMin(resultEnergyTopTenVoList);
    }


    /**
     * ????????????????????????????????????id
     *
     * @param tenantId ????????????????????????
     * @return
     */
    public UUID getFirstFactory(TenantId tenantId) {
        FactoryEntity factory = factoryDao.findFactoryByTenantIdFirst(tenantId.getId());
        log.debug("??????????????????{}??????????????????{}", tenantId.getId(), factory);
        if (factory == null) {
            log.error("??????????????????{}????????????,???????????????????????????????????????");
            throw new CustomException(ActivityException.FAILURE_ERROR.getCode(), "????????????????????????");

        }
        return factory.getId();
    }


    /**
     * ????????????????????????
     * eg:  ??? (w)
     *
     * @param dataVo
     * @return
     */
    private String getHomeKeyNameOnlyUtilNeW(DictDeviceGroupPropertyVO dataVo) {
        String title = StringUtils.isBlank(dataVo.getTitle()) ? dataVo.getName() : dataVo.getTitle();
        return "" + title + " (" + dataVo.getUnit() + ")";
    }


    /**
     * ????????????????????????
     * eg:  ??????????????? (w)
     *
     * @param dataVo
     * @return
     */
    private String getHomeKeyNameByUtilNeW(DictDeviceGroupPropertyVO dataVo) {
        String title = StringUtils.isBlank(dataVo.getTitle()) ? dataVo.getName() : dataVo.getTitle();
        return "????????????" + title + " (" + dataVo.getUnit() + ")";
    }


    /**
     * ??????????????????key?????????
     *
     * @param keys name ?????????
     * @param map
     * @return
     */
    private Map keyNameNotFound(List<String> keys, Map<String, List<ResultRunStatusByDeviceVo>> map) {
        keys.stream().forEach(str -> {
            if (CollectionUtils.isEmpty(map.get(str))) {
                map.put(str, new ArrayList<>());
            }
        });
        return map;
    }


    /**
     * ?????????????????????
     *
     * @param tenantId     ??????
     * @param dictDeviceId ????????????
     * @return
     */
    private List<DictDeviceDataVo> getParts(TenantId tenantId, UUID dictDeviceId) {
        List<DictDeviceComponentPropertyEntity> componentPropertyEntities = componentPropertyRepository.findAllByDictDeviceId(dictDeviceId);
        log.debug("?????????????????????????????????:{}", componentPropertyEntities);
        List<DictDeviceDataVo> partsList =
                componentPropertyEntities.stream().map(component -> {
                    DictDeviceDataVo vo = new DictDeviceDataVo();
                    vo.setGroupName("??????");
                    vo.setName(component.getName());
                    String title = StringUtils.isBlank(component.getTitle()) ? component.getName() : component.getTitle();
                    vo.setTitle(title);
                    Optional<DictDataEntity> dictDataEntity = dictDataRepository.findByTenantIdAndId(tenantId.getId(), component.getDictDataId());
                    vo.setUnit(dictDataEntity.isPresent() ? dictDataEntity.get().getUnit() : "");
                    return vo;
                }).collect(Collectors.toList());
        return partsList;

    }


    private List<Map> translateTitle(List<Map> list, String deviceName, Map<String, DictDeviceGroupPropertyVO> mapNameToVo) {
        List<Map> mapList = new ArrayList<>();

        for (Map m : list) {
            Map map1 = new HashMap();
            m.forEach((k, v) -> {
                map1.put("????????????", deviceName);
                if (k.equals("ts")) {
                    map1.put("createdTime", v);
                }
                DictDeviceGroupPropertyVO dictVO = mapNameToVo.get(k);
                if (dictVO != null) {
                    map1.put(getHomeKeyNameOnlyUtilNeW(dictVO), v);
                }
            });
            mapList.add(map1);
        }

        return mapList;

    }


    private List<EfficiencyHistoryDataVo> translateTitleNew(List<Map> list, String deviceName) {
        List<EfficiencyHistoryDataVo> mapList = new ArrayList<EfficiencyHistoryDataVo>();

        for (Map m : list) {
            EfficiencyHistoryDataVo efficiencyHistoryDataVo = new EfficiencyHistoryDataVo();

            Object objTs = m.get("ts");
            efficiencyHistoryDataVo.setCreatedTime(objTs != null ? Long.valueOf(objTs.toString()) : 0);

            efficiencyHistoryDataVo.setDeviceName(deviceName);
            efficiencyHistoryDataVo.setRename(deviceName);
            efficiencyHistoryDataVo.setElectric(m.get("electric") != null ? m.get("electric").toString() : "0");
            efficiencyHistoryDataVo.setWater(m.get("water") != null ? m.get("water").toString() : "0");
            efficiencyHistoryDataVo.setGas(m.get("gas") != null ? m.get("gas").toString() : "0");
            mapList.add(efficiencyHistoryDataVo);
        }

        return mapList;

    }


    private List<CapacityHistoryVo> translateTitleCap02(List<Map> list, String deviceName, Map<String, DictDeviceGroupPropertyVO> mapNameToVo) {
        List<CapacityHistoryVo> mapList = new ArrayList<CapacityHistoryVo>();

        for (Map m : list) {
            CapacityHistoryVo vo1 = new CapacityHistoryVo();
            m.forEach((k, v) -> {
                vo1.setDeviceName(deviceName);
                if (k.equals("ts")) {
                    vo1.setCreatedTime(v.toString());
                }
                DictDeviceGroupPropertyVO dictVO = mapNameToVo.get(k);
                if (dictVO != null) {
                    vo1.setValue(v != null ? v.toString() : "");
                }
            });
            mapList.add(vo1);
        }

        return mapList;

    }


    private String translateAppTitle(Map<String, DictDeviceGroupPropertyVO> mapNameToVo, String key) {
        DictDeviceGroupPropertyVO dictVO = mapNameToVo.get(key);
        if (dictVO != null) {
            String title = StringUtils.isBlank(dictVO.getTitle()) ? dictVO.getName() : dictVO.getTitle();
            return title;
        }
        return key;

    }

    private String translateAppUnit(Map<String, DictDeviceGroupPropertyVO> mapNameToVo, String key) {
//        DictDeviceGroupPropertyVO dictVO=  mapNameToVo.get(key);
//        if(dictVO != null) {
//            return  " ("+dictVO.getUnit()+")";
//        }
        return "";

    }


    private List<AppDeviceEnergyVo> translateListAppTitle(List<AppDeviceEnergyVo> vos, Map<String, DictDeviceGroupPropertyVO> mapNameToVo) {
        List<AppDeviceEnergyVo> voList = new ArrayList<>();
        vos.stream().forEach(vo1 -> {
            Map<String, String> mapOld = vo1.getMapValue();
            Map<String, String> mapnew = new HashMap<>();
            mapOld.forEach((key1, value1) -> {
                mapnew.put(translateAppTitle(mapNameToVo, key1), value1 + translateAppUnit(mapNameToVo, key1));
            });
            vo1.setMapValue(mapnew);
            voList.add(vo1);

        });

        return voList;


    }


    /**
     * ???????????????
     * ?????????: 0 (T)
     * ?????????: 0 (KWH)
     * ?????????: 0 (T)
     *
     * @return
     */
    private Map getDefaultMap(List<String> keys, Map<String, String> mapData01) {
//        Map<String,String> mapData  = new HashMap<>();
        keys.stream().forEach(str -> {
            if (StringUtils.isBlank(mapData01.get(str))) {
                mapData01.put(str, "0");
            }
        });
        return mapData01;

    }


    /*****
     * PC????????????????????????
     * @param resultList     ???????????????
     * @param mapNameToVo  ?????????title  ???????????????????????????
     * @return
     */
    private List<Map> resultProcessingByEnergyPc(List<EnergyEffciencyNewEntity> resultList, Map<String, DictDeviceGroupPropertyVO> mapNameToVo) {
        List<Map> mapList = new ArrayList<>();
        resultList.stream().forEach(vo -> {
            Map map = new HashMap();
            map.put(HEADER_0, vo.getDeviceName());
            map.put(HEADER_DEVICE_ID, vo.getEntityId());
            map.put(setKeyTitle(mapNameToVo, KeyTitleEnums.key_water, true), StringUtilToll.roundUp(vo.getWaterAddedValue()));//????????? (T)
            map.put(setKeyTitle(mapNameToVo, KeyTitleEnums.key_cable, true), StringUtilToll.roundUp(vo.getElectricAddedValue()));//????????? (KWH)
            map.put(setKeyTitle(mapNameToVo, KeyTitleEnums.key_gas, true), StringUtilToll.roundUp(vo.getGasAddedValue()));//????????? (T)


            map.put(setKeyTitle(mapNameToVo, KeyTitleEnums.key_capacity, true), StringUtilToll.roundUp(vo.getCapacityAddedValue()));//????????? (T)

            String capacityValue = vo.getCapacityAddedValue();
            //
            map.put(setKeyTitle(mapNameToVo, KeyTitleEnums.key_water, false),
                    computeUnitEnergyConsumption(capacityValue, vo.getWaterAddedValue(), vo.getWaterLastTime(), vo.getWaterFirstTime()));
            map.put(setKeyTitle(mapNameToVo, KeyTitleEnums.key_cable, false),
                    computeUnitEnergyConsumption(capacityValue, vo.getElectricAddedValue(), vo.getElectricLastTime(), vo.getElectricFirstTime()));
            map.put(setKeyTitle(mapNameToVo, KeyTitleEnums.key_gas, false),
                    computeUnitEnergyConsumption(capacityValue, vo.getGasAddedValue(), vo.getGasLastTime(), vo.getGasFirstTime()));
            mapList.add(map);
        });
        return mapList;
    }


    /*****
     * PC????????????????????????
     * @param resultList     ???????????????
     * @return
     */
    private List<EfficiencyEntityInfo> resultProcessingByEnergyPcNew(List<EnergyEffciencyNewEntity> resultList) {
        List<EfficiencyEntityInfo> efficiencyEntityInfoList = new ArrayList<EfficiencyEntityInfo>();
        resultList.stream().forEach(vo -> {
            EfficiencyEntityInfo efficiencyEntityInfo = new EfficiencyEntityInfo();
            efficiencyEntityInfo.setDeviceName(vo.getDeviceName());
            efficiencyEntityInfo.setRename(vo.getDeviceName());
            efficiencyEntityInfo.setDeviceId(vo.getEntityId());
            efficiencyEntityInfo.setWaterConsumption(StringUtilToll.roundUp(vo.getWaterAddedValue()));
            efficiencyEntityInfo.setElectricConsumption(StringUtilToll.roundUp(vo.getElectricAddedValue()));
            efficiencyEntityInfo.setGasConsumption(StringUtilToll.roundUp(vo.getGasAddedValue()));
            efficiencyEntityInfo.setCapacityConsumption(StringUtilToll.roundUp(vo.getCapacityAddedValue()));


            String capacityValue = vo.getCapacityAddedValue();
            efficiencyEntityInfo.setUnitWaterConsumption(computeUnitEnergyConsumption(capacityValue, vo.getWaterAddedValue(), vo.getWaterLastTime(), vo.getWaterFirstTime()));
            efficiencyEntityInfo.setUnitElectricConsumption(computeUnitEnergyConsumption(capacityValue, vo.getElectricAddedValue(), vo.getElectricLastTime(), vo.getElectricFirstTime()));
            efficiencyEntityInfo.setUnitGasConsumption(computeUnitEnergyConsumption(capacityValue, vo.getGasAddedValue(), vo.getGasLastTime(), vo.getGasFirstTime()));
            efficiencyEntityInfoList.add(efficiencyEntityInfo);
        });
        return efficiencyEntityInfoList;
    }


    /**
     * @param mapNameToVo
     * @param enums
     * @param type        true??????:  ????????? (T)
     *                    false ??????: ????????????????????? (T)
     * @return
     */
    private String setKeyTitle(Map<String, DictDeviceGroupPropertyVO> mapNameToVo, KeyTitleEnums enums, Boolean type) {
        DictDeviceGroupPropertyVO groupPropertyVO = mapNameToVo.get(enums.getgName());
        if (enums == KeyTitleEnums.key_capacity) {
            groupPropertyVO.setTitle(enums.getAbbreviationName());
        }
        if (type) {
            return getHomeKeyNameOnlyUtilNeW(groupPropertyVO);
        }
        return getHomeKeyNameByUtilNeW(groupPropertyVO);
    }


    /**
     * ??????????????????
     *
     * @return
     */
    private String computeUnitEnergyConsumption(String capacityValue, String value1, Long lastTime, Long firstTime) {
        if (lastTime == null) {
            return "0";
        }
        if (firstTime == null)  //???????????????
        {
            firstTime = 0L;
        }
        // Long t3 = (lastTime - firstTime) / 60000;
        Long t3 = 1L;
//        String aDouble = StringUtilToll.div(capacityValue, value1, t3.toString());
        String aDouble = StringUtilToll.div(value1, capacityValue, t3.toString());

        return aDouble;
    }


    /**
     * Pc???????????????
     *
     * @param pageList
     * @return
     */
    private List<String> getTotalValueNewMethod(List<EnergyEffciencyNewEntity> pageList, Map<String, DictDeviceGroupPropertyVO> mapNameToVo) {
        List<String> totalValueList = new ArrayList<>();
        BigDecimal invoiceAmount = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getWaterAddedValue()))
                .map(EnergyEffciencyNewEntity::getWaterAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        String waterTotalValue = StringUtilToll.roundUp(invoiceAmount.stripTrailingZeros().toPlainString());
        totalValueList.add(addTotalValueList(mapNameToVo, KeyTitleEnums.key_water, waterTotalValue));

        BigDecimal invoiceAmount02 = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getElectricAddedValue()))
                .map(EnergyEffciencyNewEntity::getElectricAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        String electricTotalValue = StringUtilToll.roundUp(invoiceAmount02.stripTrailingZeros().toPlainString());
        totalValueList.add(addTotalValueList(mapNameToVo, KeyTitleEnums.key_cable, electricTotalValue));


        BigDecimal invoiceAmount03 = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getGasAddedValue()))
                .map(EnergyEffciencyNewEntity::getGasAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        String value03 = StringUtilToll.roundUp(invoiceAmount03.stripTrailingZeros().toPlainString());
        totalValueList.add(addTotalValueList(mapNameToVo, KeyTitleEnums.key_gas, value03));
        return totalValueList;

    }


    /**
     * Pc???????????????
     *
     * @param pageList
     * @return
     */
    private EfficiencyTotalValue getTotalValueNewMethodnew(List<EnergyEffciencyNewEntity> pageList) {
        EfficiencyTotalValue totalValue = new EfficiencyTotalValue();
        BigDecimal invoiceAmount = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getWaterAddedValue()))
                .map(EnergyEffciencyNewEntity::getWaterAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        String waterTotalValue = StringUtilToll.roundUp(invoiceAmount.stripTrailingZeros().toPlainString());
        totalValue.setTotalWaterConsumption(waterTotalValue);

        BigDecimal invoiceAmount02 = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getElectricAddedValue()))
                .map(EnergyEffciencyNewEntity::getElectricAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        String electricTotalValue = StringUtilToll.roundUp(invoiceAmount02.stripTrailingZeros().toPlainString());
        totalValue.setTotalElectricConsumption(electricTotalValue);


        BigDecimal invoiceAmount03 = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getGasAddedValue()))
                .map(EnergyEffciencyNewEntity::getGasAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        String value03 = StringUtilToll.roundUp(invoiceAmount03.stripTrailingZeros().toPlainString());
        totalValue.setTotalGasConsumption(value03);
        return totalValue;

    }


    /**
     * APP???????????????
     *
     * @param pageList
     * @return
     */
    private Map<String, String> getTotalValueApp(List<EnergyEffciencyNewEntity> pageList, Map<String, DictDeviceGroupPropertyVO> mapNameToVo) {
        Map<String, String> resultMap = new HashMap<>();
        BigDecimal invoiceAmount = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getWaterAddedValue()))
                .map(EnergyEffciencyNewEntity::getWaterAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        String waterTotalValue = StringUtilToll.roundUp(invoiceAmount.stripTrailingZeros().toPlainString());
        resultMap.put(KeyTitleEnums.key_water.getgName(), waterTotalValue);

        BigDecimal invoiceAmount02 = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getElectricAddedValue()))
                .map(EnergyEffciencyNewEntity::getElectricAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        String electricTotalValue = StringUtilToll.roundUp(invoiceAmount02.stripTrailingZeros().toPlainString());
        resultMap.put(KeyTitleEnums.key_cable.getgName(), electricTotalValue);


        BigDecimal invoiceAmount03 = pageList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getGasAddedValue()))
                .map(EnergyEffciencyNewEntity::getGasAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        String value03 = StringUtilToll.roundUp(invoiceAmount03.stripTrailingZeros().toPlainString());
        resultMap.put(KeyTitleEnums.key_gas.getgName(), value03);
        return resultMap;

    }

    private String addTotalValueList(Map<String, DictDeviceGroupPropertyVO> mapNameToVo, KeyTitleEnums enums, String value) {
        DictDeviceGroupPropertyVO dvo = mapNameToVo.get(enums.getgName());
        String title = StringUtils.isBlank(dvo.getTitle()) ? dvo.getName() : dvo.getTitle();
        return (title + ": " + value + " (" + dvo.getUnit() + ")");
    }


    /**
     * App?????????????????????????????????
     * ???????????????
     *
     * @return
     */
    private List<ResultRunStatusByDeviceVo> getDefaultValue(Map<String, RunningStateVo> translateMap, String str) {
        List<ResultRunStatusByDeviceVo> resultList = new ArrayList<>();
        RunningStateVo properties = translateMap.get(str);
        ResultRunStatusByDeviceVo vo = new ResultRunStatusByDeviceVo();
        vo.setTitle(properties.getTitle());
        vo.setKeyName(properties.getName());
        vo.setValue("0");
        vo.setUnit(properties.getUnit());
        resultList.add(vo);
        return resultList;

    }


    private List<ResultEnergyTopTenVo> dataVoToResultEnergyTopTenVo(List<CensusSqlByDayEntity> entities, PcTodayEnergyRaningVo vo) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        KeyTitleEnums enums = KeyTitleEnums.getEnumsByPCCode(vo.getKeyNum());
        return entities.stream().map(m1 -> {
            ResultEnergyTopTenVo vo1 = new ResultEnergyTopTenVo();
            vo1.setDeviceId(m1.getEntityId());
            vo1.setDeviceName(m1.getRename());
            vo1.setRename(m1.getRename());
            if (vo.getType().equals("0")) {
                vo1.setValue(StringUtils.isNotEmpty(m1.getCapacityAddedValue()) ? m1.getCapacityAddedValue() : "0");
            } else {

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
            return vo1;
        }).collect(Collectors.toList());


    }


    /**
     * ????????????
     * ????????????????????? ?????????????????????
     */
    private List<RunningStateVo> filterOutSaved(List<DictDeviceDataVo> dictDeviceDataVos, List<DictDeviceGraphVO> graphVOS) {
        List<RunningStateVo> resultList = new ArrayList<>();
        List<RunningStateVo> runningStateVoList = dictDeviceDataVos.stream().map(m0 -> {
            return RunningStateVo.toDataByDictDeviceDataVo(m0);
        }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(graphVOS)) {
            return runningStateVoList;

        }
        Map<String, String> attributesInChartMap = new HashMap<>();
        graphVOS.stream().forEach(m1 -> {

            RunningStateVo vo = toRunningStateVoByDictDeviceVo(m1);
            String unit = "";

            List<DictDeviceGraphPropertyVO> dictDeviceGraphPropertyVOList = m1.getProperties();
            if (!CollectionUtils.isEmpty(dictDeviceGraphPropertyVOList)) {
                List<String> stringList = dictDeviceGraphPropertyVOList.stream().map(DictDeviceGraphPropertyVO::getName).collect(Collectors.toList());
                vo.setAttributeNames(stringList);
                for (DictDeviceGraphPropertyVO v1 : dictDeviceGraphPropertyVOList) {
                    if (StringUtils.isEmpty(unit)) {
                        unit = v1.getUnit();
                    }
                    attributesInChartMap.put(v1.getName(), m1.getName());
                }
            }
            vo.setUnit(unit);
            if (m1.getEnable()) {
                resultList.add(vo);
            }
        });
        runningStateVoList.stream().forEach(m1 -> {
            if (StringUtils.isEmpty(attributesInChartMap.get(m1.getName()))) {
                resultList.add(m1);
            }
        });
        return resultList;
    }


    private RunningStateVo toRunningStateVoByDictDeviceVo(DictDeviceGraphVO vo) {
        RunningStateVo runningStateVo = new RunningStateVo();
        runningStateVo.setTitle(vo.getName());//???????????????
//        runningStateVo.setName(vo.getName());
        runningStateVo.setChartId(vo.getId() != null ? vo.getId().toString() : "");
        return runningStateVo;

    }


    /**
     * ??????????????????keyName
     * 1. ?????? chartId ?????????;????????????????????????;  #??????????????????
     * 2. ??????  attributeNames  ??????????????????????????????;  ##????????????????????????  ????????????keyName??????????????????
     *
     * @param voList
     * @return
     */
    private List<String> getKeyNameByVoList(List<RunningStateVo> voList, TenantId tenantId, Map<String, DictDeviceGraphVO> chartIdToKeyNameMap) {
        List<String> keyNames = new ArrayList<>();
        voList.stream().forEach(m1 -> {
            if (StringUtils.isNotBlank(m1.getChartId())) {
                //????????????????????????
                UUID uuid = UUID.fromString(m1.getChartId());
                try {
                    DictDeviceGraphVO dictDeviceGraphVO = this.dictDeviceService.getDictDeviceGraphDetail(tenantId, uuid);
                    chartIdToKeyNameMap.put(m1.getChartId(), dictDeviceGraphVO);
                    List<DictDeviceGraphPropertyVO> dictDeviceGraphPropertyVOS = dictDeviceGraphVO.getProperties();
                    if (!CollectionUtils.isEmpty(dictDeviceGraphPropertyVOS)) {
                        List<String> strings = dictDeviceGraphPropertyVOS.stream().map(DictDeviceGraphPropertyVO::getName).collect(Collectors.toList());
                        keyNames.addAll(strings);
                    }

                } catch (ThingsboardException e) {
                    e.printStackTrace();
                    log.error("??????id?????????????????????:{}", e);
                }
            } else {
                keyNames.add(m1.getName());
            }

        });
        return keyNames;

    }


    /**
     * @param tsKvEntries
     * @param parameterVo         ???????????????
     * @param keyNames
     * @param chartIdToKeyNameMap ??????id ????????? ??????keyName
     * @return
     */
    private List<OutRunningStateVo> getRunningStatusResults(List<TsKvEntry> tsKvEntries,
                                                            InputRunningSateVo parameterVo,
                                                            List<String> keyNames,
                                                            Map<String, DictDeviceGraphVO> chartIdToKeyNameMap) {
        List<OutRunningStateVo> outRunningStateVos = new ArrayList<>();
        log.debug("??????????????????");
        List<ResultRunStatusByDeviceVo> voList = new ArrayList<>();
        voList = tsKvEntries.stream().map(TsKvEntry -> {
            ResultRunStatusByDeviceVo byDeviceVo = new ResultRunStatusByDeviceVo();
            byDeviceVo.setKeyName(TsKvEntry.getKey());
            byDeviceVo.setValue(StringUtilToll.roundUp(TsKvEntry.getValue().toString()));
            byDeviceVo.setTime(TsKvEntry.getTs());
            return byDeviceVo;
        }).collect(Collectors.toList());
        Map<String, List<ResultRunStatusByDeviceVo>> map = voList.stream().collect(Collectors.groupingBy(ResultRunStatusByDeviceVo::getKeyName));
        log.debug("???????????????????????????:{}", map);
        Map<String, List<ResultRunStatusByDeviceVo>> map1 = keyNameNotFound(keyNames, map);

        List<RunningStateVo> runningStateVoList = parameterVo.getAttributeParameterList();//??????
        logInfoJson("?????????runningStateVoList???", runningStateVoList);
        runningStateVoList.stream().forEach(m1 -> {
            OutRunningStateVo outRunningStateVo = new OutRunningStateVo();
            outRunningStateVo.setTableName(m1.getTitle());//????????????????????????????????????
            outRunningStateVo.setKeyName(m1.getName());
            List<OutOperationStatusChartDataVo> properties = new ArrayList<>();
            //????????????
            if (StringUtils.isBlank(m1.getChartId())) {
                OutOperationStatusChartDataVo vo = new OutOperationStatusChartDataVo();
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
            } else {
                DictDeviceGraphVO graphVO = chartIdToKeyNameMap.get(m1.getChartId());
                if (graphVO != null) {
                    outRunningStateVo.setTableName(graphVO.getName());
                    outRunningStateVo.setChartId(m1.getChartId());
                    List<OutOperationStatusChartDataVo> rrlist2 = getTheDataOfTheChart(graphVO, map1);
                    logInfoJson("====??????????????????????????????????????????resultList", rrlist2);
                    outRunningStateVo.setProperties(rrlist2);
                }
            }
            outRunningStateVos.add(outRunningStateVo);

        });

        return outRunningStateVos;

    }

    /**
     * ????????????
     *
     * @param graphVO
     * @param map1
     * @return
     */
    private List<OutOperationStatusChartDataVo> getTheDataOfTheChart(DictDeviceGraphVO graphVO, Map<String, List<ResultRunStatusByDeviceVo>> map1) {
        List<OutOperationStatusChartDataVo> resultList = new ArrayList<>();

        List<DictDeviceGraphPropertyVO> dictDeviceGraphPropertyVOS = graphVO.getProperties();
        logInfoJson("???????????????????????????", dictDeviceGraphPropertyVOS);
        logInfoJson("???????????????????????????Map<String,List<ResultRunStatusByDeviceVo>>", map1);

        List<Long> timeAll = new ArrayList<>();
        if (!CollectionUtils.isEmpty(map1)) {
            map1.forEach((k1, v1) -> {
                timeAll.addAll(v1.stream().map(ResultRunStatusByDeviceVo::getTime).distinct().collect(Collectors.toList()));
            });
        }
        List<Long> timeAllsort = timeAll.stream().filter(s1 -> s1 != null).distinct().sorted().collect(Collectors.toList());

        logInfoJson("timeAllsort", timeAllsort);

        if (!CollectionUtils.isEmpty(dictDeviceGraphPropertyVOS)) {
            dictDeviceGraphPropertyVOS.stream().forEach(m1 -> {
                OutOperationStatusChartDataVo v2 = new OutOperationStatusChartDataVo();

                List<ResultRunStatusByDeviceVo> resultRunStatusByDeviceVos = map1.get(m1.getName());
                logInfoJson("???????????????????????????MapresultRunStatusByDeviceVos", resultRunStatusByDeviceVos);
                Map<Long, String> mapTimeValue =
                        resultRunStatusByDeviceVos.stream().collect(Collectors.toMap(ResultRunStatusByDeviceVo::getTime, ResultRunStatusByDeviceVo::getValue));

                List<OutOperationStatusChartTsKvDataVo> list3 = timeAllsort.stream().map(m2 -> {
                    OutOperationStatusChartTsKvDataVo tsKvDataVo = new OutOperationStatusChartTsKvDataVo();
                    tsKvDataVo.setTs(m2);
                    String localValue = mapTimeValue.get(m2);
                    if (StringUtils.isEmpty(localValue)) {
                        List<Long> tsTime = resultRunStatusByDeviceVos
                                .stream()
                                .filter(entity01 -> entity01.getTime() < m2)
                                .map(ResultRunStatusByDeviceVo::getTime).collect(Collectors.toList());
                        Long maxTime = StringUtilToll.getMaxByLong(tsTime);
                        localValue = mapTimeValue.get(maxTime);
                    }
                    tsKvDataVo.setValue(localValue);
                    return tsKvDataVo;
                }).collect(Collectors.toList());


                v2.setTitle(m1.getTitle());
                v2.setName(m1.getName());
                v2.setUnit(m1.getUnit());
                v2.setTsKvs(list3);
                logInfoJson("v2???????????????????????????resultList", v2);

                resultList.add(v2);
                logInfoJson("111???????????????????????????resultList", resultList);


            });

        }
        logInfoJson("??????????????????????????????????????????resultList", resultList);
        return resultList;

    }


    /**
     * ????????????????????????????????????????????? [app]
     */
    private List<DictDeviceDataVo> conversionOfChartObjects(List<DictDeviceGraphVO> graphVOS) {
        List<DictDeviceDataVo> targetObjectList = graphVOS.stream().map(source1 -> {
            DictDeviceDataVo targetObject = new DictDeviceDataVo();
            targetObject.setTitle(source1.getName());//???????????????
            targetObject.setChartId(source1.getId() != null ? source1.getId().toString() : "");//??????id
            targetObject.setEnable(source1.getEnable());
            List<DictDeviceGraphPropertyVO> dictDeviceGraphPropertyVOList = source1.getProperties();
            if (!CollectionUtils.isEmpty(dictDeviceGraphPropertyVOList)) {
                List<String> attributeNames = dictDeviceGraphPropertyVOList.stream().map(DictDeviceGraphPropertyVO::getName).collect(Collectors.toList());
                targetObject.setAttributeNames(attributeNames);
                String unit = dictDeviceGraphPropertyVOList.stream().filter(s1 -> StringUtils.isNotEmpty(s1.getUnit())).findFirst().orElse(new DictDeviceGraphPropertyVO()).getUnit();
                targetObject.setUnit(unit);
            }
            return targetObject;

        }).collect(Collectors.toList());
        return targetObjectList;
    }


    /**
     * ??????????????????????????????????????????
     *
     * @param chartDataList     ??????????????? attributeNames
     * @param dictDeviceDataVos ?????? ?????? ?????????; ???name
     */
    private List<DictDeviceDataVo> filterAlreadyExistsInTheChart(List<DictDeviceDataVo> chartDataList, List<DictDeviceDataVo> dictDeviceDataVos) {
        if (CollectionUtils.isEmpty(chartDataList)) {
            return dictDeviceDataVos;
        }
        Map<String, String> chartMap = new HashMap<>();
        chartDataList.stream().forEach(s1 -> {
            List<String> list = s1.getAttributeNames();
            if (!CollectionUtils.isEmpty(list)) {
                list.stream().forEach(str -> {
                    chartMap.put(str, str);
                });

            }
        });
        logInfoJson("chartMap???????????????", chartMap);
        if (CollectionUtils.isEmpty(chartMap)) {
            return dictDeviceDataVos;
        }
        List<DictDeviceDataVo> targetList = new ArrayList<>();
        dictDeviceDataVos.stream().forEach(s2 ->
        {
            if (StringUtils.isEmpty(chartMap.get(s2.getName()))) {
                targetList.add(s2);
            }
        });
        return targetList;
    }


    /**
     * ???????????????
     *
     * @param str
     * @param obj
     */
    private void logInfoJson(String str, Object obj) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(obj);
//            log.info("?????????"+str+"???????????????:"+json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }


    private List<OutAppRunnigStateVo> pcResultVoToApp(List<OutRunningStateVo> pcResultVo) {
        List<OutAppRunnigStateVo> outAppRunnigStateVos =
                pcResultVo.stream().map(sourceVo -> {
                    OutAppRunnigStateVo aapVo = new OutAppRunnigStateVo();
                    aapVo.setChartId(sourceVo.getChartId());
                    aapVo.setTableName(sourceVo.getTableName());
                    List<OutOperationStatusChartDataVo> attributeSourceList = sourceVo.getProperties();
                    String chartUnit = attributeSourceList.stream().filter(s1 -> StringUtils.isNotEmpty(s1.getUnit())).findFirst().orElse(new OutOperationStatusChartDataVo()).getUnit();
                    aapVo.setChartUnit(chartUnit);//???????????????

                    List<OutAppOperationStatusChartDataVo> propertiesAppList = attributeSourceList.stream().map(s1 -> {
                        OutAppOperationStatusChartDataVo t1 = new OutAppOperationStatusChartDataVo();
                        t1.setName(s1.getName());
                        t1.setTitle(s1.getTitle());
                        t1.setUnit(s1.getUnit());
                        List<OutOperationStatusChartTsKvDataVo> tskvList = s1.getTsKvs();
                        List<List<Object>> chartTsKv = new ArrayList<>();
                        tskvList.stream().forEach(m1 -> {
                            List<Object> strings = new ArrayList<>();
                            strings.add(m1.getTs());
                            strings.add(m1.getValue());
                            chartTsKv.add(strings);
                        });
                        t1.setTsKvs(chartTsKv);
                        return t1;
                    }).collect(Collectors.toList());

                    aapVo.setProperties(propertiesAppList);


                    return aapVo;
                }).collect(Collectors.toList());
        return outAppRunnigStateVos;
    }


    private Map<String, String> getEnergyHistroyMap(TsSqlDayVo vo) {
        Map<String, String> historyMap = new HashMap<>();
        setHistoryMapValue(vo, historyMap, KeyTitleEnums.key_water);
        setHistoryMapValue(vo, historyMap, KeyTitleEnums.key_cable);
        setHistoryMapValue(vo, historyMap, KeyTitleEnums.key_gas);
        return historyMap;
    }

    private void setHistoryMapValue(TsSqlDayVo vo, Map<String, String> historyMap, KeyTitleEnums enums) {
        historyMap.put(enums.getgName(), effciencyAnalysisRepository.queryHistoricalTelemetryData(vo, false, enums.getCode()));
    }


    /**
     * ??????????????????
     *
     * @param vo
     * @return
     */
    private String todayValueOfOutput(TsSqlDayVo vo) {
        String value = "0";

        TsSqlDayVo vo1 = new TsSqlDayVo();
        vo1.setFactoryId(vo.getFactoryId());
        vo1.setTenantId(vo.getTenantId());
        vo1.setWorkshopId(vo.getWorkshopId());
        vo1.setProductionLineId(vo.getProductionLineId());
        vo1.setStartTime(CommonUtils.getZero());
        List<CensusSqlByDayEntity> entities = effciencyAnalysisRepository.queryCensusSqlByDay(vo1, true);
        if (CollectionUtils.isEmpty(entities)) {
            return value;
        }

        for (CensusSqlByDayEntity m1 : entities) {
            return StringUtilToll.roundUp(m1.getIncrementCapacity());

        }
        return value;
    }


    /**
     * @param vo
     * @return
     */
    private String sectionValueOfOutput(TsSqlDayVo vo) {
        List<CensusSqlByDayEntity> entities = effciencyAnalysisRepository.queryCensusSqlByDay(vo, true);
        if (CollectionUtils.isEmpty(entities)) {
            return "0";
        }
        List<String> nameList = entities.stream().map(CensusSqlByDayEntity::getIncrementCapacity).collect(Collectors.toList());
        return StringUtilToll.accumulator(nameList);
    }


    private List<TsKvEntity> getExcludeZero(List<TsKvEntity> entities) {
        return entities.stream().filter(s1 -> {
            if (StringUtils.isNotEmpty(s1.getStrValue())) {
                return StringUtilToll.isNotZero(s1.getStrValue());
            }
            if ((s1.getDoubleValue()) != null) {
                return StringUtilToll.isNotZero(s1.getDoubleValue().toString());
            }
            if ((s1.getLongValue()) != null) {
                return StringUtilToll.isNotZero(s1.getLongValue().toString());
            }
            return false;
        }).collect(Collectors.toList());
    }


}
