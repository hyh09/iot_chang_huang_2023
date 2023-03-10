package org.thingsboard.server.dao.board.factoryBoard.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.effciency.EfficiencyEntityInfo;
import org.thingsboard.server.common.data.effciency.total.EfficiencyTotalValue;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageDataAndTotalValue;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.enums.key.KeyNameEnums;
import org.thingsboard.server.common.data.vo.enums.key.KeyPcNameEnums;
import org.thingsboard.server.dao.board.factoryBoard.dto.ChartByChartEnumsDto;
import org.thingsboard.server.dao.board.factoryBoard.impl.base.ChartByChartDateEnumServer;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryEnergySvc;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.*;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request.ChartDateEnums;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.current.CurrentUtilitiesVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.current.EnergyUnitVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.top.FactoryEnergyTop;
import org.thingsboard.server.dao.board.workshopBoard.CapacitiesTop5Vo;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.hs.service.DeviceDictPropertiesSvc;
import org.thingsboard.server.dao.sql.role.dao.EffciencyAnalysisRepository;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;
import org.thingsboard.server.dao.sql.role.service.EfficiencyStatisticsSvc;
import org.thingsboard.server.dao.sqlserver.jdbc.server.HwEnergyService;
import org.thingsboard.server.dao.sqlserver.server.vo.order.HwEnergyEnums;
import org.thingsboard.server.dao.util.decimal.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Project Name: thingsboard
 * @File Name: FactoryEnergyImpl
 * @Date: 2023/1/4 9:32
 * @author: wb04
 * ??????????????????: ????????????????????????????????????
 * Copyright (c) 2023,All Rights Reserved.
 */
@Service
public class FactoryEnergyImpl extends ChartByChartDateEnumServer implements FactoryEnergySvc {

    private EfficiencyStatisticsSvc efficiencyStatisticsSvc;
    private DeviceDictPropertiesSvc deviceDictPropertiesSvc;
    private EffciencyAnalysisRepository effciencyAnalysisRepository;
    private HwEnergyService hwEnergyService;

    public FactoryEnergyImpl(JdbcTemplate jdbcTemplate, EfficiencyStatisticsSvc efficiencyStatisticsSvc,
                             DeviceDictPropertiesSvc deviceDictPropertiesSvc, EffciencyAnalysisRepository effciencyAnalysisRepository,
                             HwEnergyService hwEnergyService) {
        super(jdbcTemplate);
        this.efficiencyStatisticsSvc = efficiencyStatisticsSvc;
        this.deviceDictPropertiesSvc = deviceDictPropertiesSvc;
        this.effciencyAnalysisRepository = effciencyAnalysisRepository;
        this.hwEnergyService = hwEnergyService;
    }

    /**
     * ??????
     *
     * @param queryTsKvVo
     * @return
     */
    @Override
    public CurrentUtilitiesVo queryCurrentEnergy(QueryTsKvVo queryTsKvVo, TenantId tenantId) throws JsonProcessingException {
        PageLink pageLink = new PageLink(2, 0);
        PageDataAndTotalValue<EfficiencyEntityInfo> pageDataAndTotalValue = efficiencyStatisticsSvc.queryEntityByKeysNew(queryTsKvVo, tenantId, pageLink);
        EfficiencyTotalValue efficiencyTotalValue = JacksonUtil.convertValue(pageDataAndTotalValue.getTotalValue(), EfficiencyTotalValue.class);
        Map<String, DictDeviceGroupPropertyVO> map = deviceDictPropertiesSvc.getMapPropertyVoByTitle();
        CurrentUtilitiesVo currentUtilitiesVo = new CurrentUtilitiesVo();
        currentUtilitiesVo.setWater(getEnergyUnitVo(efficiencyTotalValue.getTotalWaterConsumption(), map.get(KeyNameEnums.water.getName())));
        currentUtilitiesVo.setElectricity(getEnergyUnitVo(efficiencyTotalValue.getTotalElectricConsumption(), map.get(KeyNameEnums.electric.getName())));
        currentUtilitiesVo.setGas(getEnergyUnitVo(efficiencyTotalValue.getTotalGasConsumption(), map.get(KeyNameEnums.gas.getName())));
        return currentUtilitiesVo;
    }

    /**
     * 1 ???????????? --- ??? --- ??? ??????
     * 2. ????????????????????? 15???????????????
     * 3. ?????????????????? ?????????????????????????????????????????????????????? ?????????????????????
     *
     * @param queryTsKvVo
     * @param tenantId
     * @return
     */
    @Override
    public List<FactoryEnergyTop> queryCurrentTop(QueryTsKvVo queryTsKvVo, TenantId tenantId) {
        List<FactoryEnergyTop> resultList = new ArrayList<>();
        List<EnergyEffciencyNewEntity> entityList = effciencyAnalysisRepository.queryEnergy(queryTsKvVo);
        if (CollectionUtils.isEmpty(entityList)) {
            return resultList;
        }

        List<EnergyEffciencyNewEntity> waterList = entityList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getWaterAddedValue()))
                .sorted((s1, s2) -> new BigDecimal(s2.getWaterAddedValue()).compareTo(new BigDecimal(s1.getWaterAddedValue())))
                .limit(5)
                .collect(Collectors.toList());
        resultList.addAll(setTopDataConversion(waterList, KeyPcNameEnums.WATER));

        List<EnergyEffciencyNewEntity> energyEffciencyNewEntities = entityList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getElectricAddedValue()))
                .sorted((s1, s2) -> new BigDecimal(s2.getElectricAddedValue()).compareTo(new BigDecimal(s1.getElectricAddedValue())))
                .limit(5)
                .collect(Collectors.toList());
        resultList.addAll(setTopDataConversion(energyEffciencyNewEntities, KeyPcNameEnums.ELECTRIC));

        List<EnergyEffciencyNewEntity> gasList = entityList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getGasAddedValue()))
                .sorted((s1, s2) -> new BigDecimal(s2.getGasAddedValue()).compareTo(new BigDecimal(s1.getGasAddedValue())))
                .limit(5)
                .collect(Collectors.toList());
        resultList.addAll(setTopDataConversion(gasList, KeyPcNameEnums.VAPOR));
        /**??????????????? */
//        List<FactoryEnergyTop> deduplicationResut = resultList.stream().collect(
//                Collectors.collectingAndThen(
//                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FactoryEnergyTop::getDeviceId))), ArrayList::new)
//        );
        return resultList;
    }

    /**
     * ??????????????? or ??????)???????????????  ??? ??????
     *
     * @param queryTsKvVo
     * @param dateEnums   ??????????????? ??? / ???
     * @return
     */
    @Override
    public ChartResultVo queryTrendChart(QueryTsKvVo queryTsKvVo, ChartDateEnums dateEnums) {
        List<ChartByChartEnumsDto> chartByChartEnumsDtos = super.queryChartEnums(queryTsKvVo, dateEnums);
        ChartResultVo chartResultVo = convertData(chartByChartEnumsDtos, dateEnums);
        chartResultVo.setDateEnums(dateEnums);
        return calculateTheCostRatio(chartResultVo);
    }


    @Override
    public List<UserEveryYearCostVo> queryUserEveryYearCost(QueryTsKvVo queryTsKvVo, TenantId tenantId) {
        List<ChartByChartEnumsDto> chartByChartEnumsDtos = super.queryChartEnums(queryTsKvVo, ChartDateEnums.YEARS);
        Map<String, BigDecimal> map = hwEnergyService.queryUnitPrice();
        return calculateMonthlyCostOnAnnulTrend(chartByChartEnumsDtos, map);
    }

    @Override
    public ExpenseDashboardVo queryExpenseDashboard(QueryTsKvVo queryTsKvVo) {
        List<CompletableFuture<ChartResultVo>> futureList =
                EnumSet.allOf(ChartDateEnums.class).stream().map(day -> CompletableFuture.supplyAsync(() -> this.queryTrendChart(queryTsKvVo, day)))
                        .collect(Collectors.toList());
        List<ChartResultVo> resultList = futureList.stream().map(future -> future.join()).collect(Collectors.toList());
        ExpenseDashboardVo resultVo = new ExpenseDashboardVo();
        resultList.stream().forEach(m1 ->
        {
            if (m1.getDateEnums() == ChartDateEnums.YEARS) {
                resultVo.setYear(m1.getCostRatioVo());
            } else {
                resultVo.setMonth(m1.getCostRatioVo());
            }
        });
        return resultVo;
    }

    /**
     * ??????????????????????????? ????????????top5???????????????
     *
     * @param queryTsKvVo ???????????????
     * @return
     */
    @Override
    public List<CapacitiesTop5Vo> queryCapacitiesTop5(QueryTsKvVo queryTsKvVo) {
        List<EnergyEffciencyNewEntity> entityList = effciencyAnalysisRepository.queryEnergy(queryTsKvVo);
        if (CollectionUtils.isEmpty(entityList)) {
            return new ArrayList<>();
        }
        List<EnergyEffciencyNewEntity> energyEffciencyNewEntities = entityList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getCapacityAddedValue()))
                .sorted((s1, s2) -> new BigDecimal(s2.getCapacityAddedValue()).compareTo(new BigDecimal(s1.getCapacityAddedValue())))
                .limit(5)
                .collect(Collectors.toList());
        return energyEffciencyNewEntities.stream().map(m1 -> {
            return new CapacitiesTop5Vo(m1.getDeviceName(), m1.getCapacityAddedValue());
        }).collect(Collectors.toList());
    }

    private EnergyUnitVo getEnergyUnitVo(String value, DictDeviceGroupPropertyVO deviceGroupPropertyVO) {
        EnergyUnitVo energyUnitVo = new EnergyUnitVo();
        energyUnitVo.setActualValue(value);
        if (deviceGroupPropertyVO != null) {
            energyUnitVo.setKey(deviceGroupPropertyVO.getName());
            energyUnitVo.setName(deviceGroupPropertyVO.getTitle());
            energyUnitVo.setUnit(deviceGroupPropertyVO.getUnit());
        }
        return energyUnitVo;
    }


    private List<FactoryEnergyTop> setTopDataConversion(List<EnergyEffciencyNewEntity> energyEffciencyNewEntities, KeyPcNameEnums enums) {
        if (CollectionUtils.isNotEmpty(energyEffciencyNewEntities)) {
            return energyEffciencyNewEntities.stream().map(m1 -> {
                FactoryEnergyTop top = new FactoryEnergyTop();
                top.setDeviceId(m1.getEntityId());
                top.setDeviceName(m1.getDeviceName());
                top.setElectricity(m1.getElectricAddedValue());
                top.setWater(m1.getWaterAddedValue());
                top.setGas(m1.getGasAddedValue());
                top.setType(enums.getCode());
                return top;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


    private ChartResultVo convertData(List<ChartByChartEnumsDto> chartByChartEnumsDtos, ChartDateEnums chartDateEnums) {
        ChartResultVo vo = new ChartResultVo();
        if (CollectionUtils.isEmpty(chartByChartEnumsDtos)) {
            return vo;
        }
        vo.setWater(chartByChartEnumsDtos.stream().map(m1 -> {
                    ChartDataVo v1 = new ChartDataVo();
                    v1.setTime(chartDateEnums.forMartTime(m1.getLocalDateTime()));
                    v1.setValue(m1.getWaterValue());
                    return v1;
                }).collect(Collectors.toList())
        );
        vo.setElectricity(chartByChartEnumsDtos.stream().map(m1 -> {
                    ChartDataVo v1 = new ChartDataVo();
                    v1.setTime(chartDateEnums.forMartTime(m1.getLocalDateTime()));
                    v1.setValue(m1.getElectricValue());
                    return v1;
                }).collect(Collectors.toList())
        );
        vo.setGas(chartByChartEnumsDtos.stream().map(m1 -> {
                    ChartDataVo v1 = new ChartDataVo();
                    v1.setTime(chartDateEnums.forMartTime(m1.getLocalDateTime()));
                    v1.setValue(m1.getGasValue());
                    return v1;
                }).collect(Collectors.toList())
        );
        return vo;
    }

    private ChartResultVo calculateTheCostRatio(ChartResultVo chartResultVo) {
        BigDecimal water = getTotalValue(chartResultVo.getWater(), HwEnergyEnums.WATER);
        BigDecimal electricity = getTotalValue(chartResultVo.getElectricity(), HwEnergyEnums.ELECTRICITY);
        BigDecimal gas = getTotalValue(chartResultVo.getGas(), HwEnergyEnums.GAS);
        BigDecimal denominator = BigDecimalUtil.INSTANCE.add(water, electricity, gas);
        CostRatioVo vo = new CostRatioVo(water.toPlainString(), electricity.toPlainString(), gas.toEngineeringString(), denominator);
        //?????????????????????
        Map map = new HashMap();
        map.put("water", water.toPlainString());
        map.put("electricity", electricity.toPlainString());
        map.put("gas", gas.toPlainString());
        vo.setMap(map);
//        CostRatioVo vo = new CostRatioVo(getPercentage(water, denominator), getPercentage(electricity, denominator), getPercentage(gas, denominator), denominator);
        chartResultVo.setCostRatioVo(vo);
        return chartResultVo;

    }

    /**
     * ??????????????? ???????????? ????????????
     *
     * @param voList
     * @param hwEnergyEnums
     * @return
     */
    private BigDecimal getTotalValue(List<ChartDataVo> voList, HwEnergyEnums hwEnergyEnums) {
        Map<String, BigDecimal> map = hwEnergyService.queryUnitPrice();
        List<String> finalValueList = voList.stream().map(ChartDataVo::getValue).collect(Collectors.toList());
        BigDecimal total = BigDecimalUtil.INSTANCE.accumulator(finalValueList);
        //????????????????????????
        BigDecimal price = map.get(hwEnergyEnums.getChineseField());
        return BigDecimalUtil.INSTANCE.multiply(total, price != null ? price : "1");

    }

    /**
     * ?????????????????????
     *
     * @param currentValue
     * @param totalValue
     * @return
     */
    private String getPercentage(BigDecimal currentValue, BigDecimal totalValue) {
        String valueRatio = BigDecimalUtil.INSTANCE.divide(currentValue, totalValue).toPlainString();
        BigDecimalUtil bigDecimalUtil = BigDecimalUtil.INSTANCE;
        String v01 = bigDecimalUtil.multiply(valueRatio, 100).toPlainString();
        return v01 + "%";
    }

    /**
     * ???????????????????????????
     *
     * @param chartByChartEnumsDtos ???????????????????????? ??? ??? ??????
     * @param map                   ??????
     * @return
     */
    private List<UserEveryYearCostVo> calculateMonthlyCostOnAnnulTrend(List<ChartByChartEnumsDto> chartByChartEnumsDtos, Map<String, BigDecimal> map) {
        return chartByChartEnumsDtos.stream().map(m1 -> {
            UserEveryYearCostVo v1 = new UserEveryYearCostVo();
            v1.setTime(ChartDateEnums.YEARS.forMartTime(m1.getLocalDateTime()));
            v1.setValue(calculateAggregatedValues(m1, map));
            return v1;
        }).collect(Collectors.toList());

    }

    /**
     * ?????????????????????
     *
     * @param m1  ??? ??? ????????????
     * @param map ??? ??? ????????????
     * @return ????????????????????????????????????
     */
    private String calculateAggregatedValues(ChartByChartEnumsDto m1, Map<String, BigDecimal> map) {
        BigDecimal water = BigDecimalUtil.INSTANCE.multiply(m1.getWaterValue(), map.get(HwEnergyEnums.WATER.getChineseField()));
        BigDecimal e02 = BigDecimalUtil.INSTANCE.multiply(m1.getElectricValue(), map.get(HwEnergyEnums.ELECTRICITY.getChineseField()));
        BigDecimal gas = BigDecimalUtil.INSTANCE.multiply(m1.getGasValue(), map.get(HwEnergyEnums.GAS.getChineseField()));
        return BigDecimalUtil.INSTANCE.add(water, e02, gas).toPlainString();

    }

}
