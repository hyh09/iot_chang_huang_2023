package org.thingsboard.server.dao.board.factoryBoard.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.board.factoryBoard.impl.base.SqlServerBascFactoryImpl;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryCollectionInformationSvc;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryProductionInformationSvc;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request.ChartDateEnums;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.*;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.vo.CurrentOrdersInProduction07Vo;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;
import org.thingsboard.server.dao.util.GenericsUtils;
import org.thingsboard.server.dao.util.decimal.BigDecimalUtil;
import org.thingsboard.server.dao.util.decimal.DateLocaDateAndTimeUtil;
import org.thingsboard.server.dao.workshop.WorkshopService;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Project Name: thingsboard
 * @File Name: FactoryProductionInformationImpl
 * @Date: 2023/1/6 13:46
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Slf4j
@Service
public class FactoryProductionInformationImpl extends SqlServerBascFactoryImpl implements FactoryProductionInformationSvc, InitializingBean {

    @Autowired
    private WorkshopService workshopService;
    @Autowired
    private FactoryCollectionInformationSvc factoryCollectionInformationSvc;

    public FactoryProductionInformationImpl(@Autowired @Qualifier("sqlServerTemplate") JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    /**
     * 查询车间的信息
     *
     * @param tenantId  租户id
     * @param factoryId 工厂id
     * @return
     */
    @Override
    public List<WorkshopAndRunRateVo> queryWorkshopAndRunRate(TenantId tenantId, UUID factoryId) {
        List<WorkshopAndRunRateVo> finalResultList = new ArrayList<>();
        List<Workshop> workshopList = workshopService.findWorkshopListByTenant(tenantId.getId(), factoryId);
        if (CollectionUtils.isEmpty(workshopList)) {
            return finalResultList;
        }
        List<CompletableFuture<WorkshopAndRunRateVo>> futures = workshopList.stream()
                .map(t ->
                        CompletableFuture.supplyAsync(() ->
                                (getVo(t, tenantId))
                        )


                ).collect(Collectors.toList());
        List<WorkshopAndRunRateVo> result =
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList());
        return result;
    }

    /**
     * 订单生产情况
     *
     * @return
     */
    @Override
    public OrderProductionVo getOrderProduction() throws Exception {
        OrderProductionVo vo = new OrderProductionVo();
        super.executeSqlByObject(vo);
        return vo;
    }

    /**
     * ③  订单的 完成率  和 成品率
     *
     * @return
     * @throws Exception
     */
    @Override
    public OrderCompletionRateAndYieldRateVo getOrderCompletionRateAndYieldRate() throws Exception {
        OrderCompletionRateAndYieldRateVo vo = new OrderCompletionRateAndYieldRateVo();
        super.executeSqlByObject(vo);
        return vo;
    }

    /**
     * ④ 订单完成情况
     *
     * @return
     */
    @Override
    public List<OrderFulfillmentVo> queryListOrderFulfillmentVo() {
        OrderFulfillmentVo fulfillmentVo = new OrderFulfillmentVo();
        List<OrderFulfillmentVo> fulfillmentVoList = jdbcByAssembleSqlUtil.finaListByObj(fulfillmentVo);
        return timelineSupplement(fulfillmentVoList);

    }

    /**
     * 实例化之后的属性赋值
     *
     * @throws Exception
     */
    @Override
    public List<ProcessRealTimeOutputVo> queryListProcessRealTimeOutputVo() {
        ProcessRealTimeOutputVo fulfillmentVo = new ProcessRealTimeOutputVo();
        List<ProcessRealTimeOutputVo> fulfillmentVoList = jdbcByAssembleSqlUtil.finaListByObj(fulfillmentVo);
        return fulfillmentVoList;
    }

    /**
     * top 10 默认排序 @SqlOnFromTableAnnotation中的 orderBy
     *
     * @return
     */
    @Override
    public List<CurrentOrdersInProductionDto> queryCurrentOrdersInProductionDto() {
//        List<CurrentOrdersInProductionDto> ordersInProductionDtos = jdbcByAssembleSqlUtil.finaListByObj(new CurrentOrdersInProductionDto());
        PageLink pageLink = new PageLink(10);
        PageData<CurrentOrdersInProductionDto> pageData = jdbcByAssembleSqlUtil.pageQuery(new CurrentOrdersInProductionDto(), DaoUtil.toPageable(pageLink));
        List<CurrentOrdersInProductionDto> list = pageData.getData();
        if (CollectionUtils.isNotEmpty(list)) {
            list.stream().forEach(m1 -> {
                String value = m1.getNumberOfOrders();
                m1.setNumberOfOrders(BigDecimalUtil.INSTANCE.formatByObject(value).toPlainString());
            });
        }
        return list;
    }

    /**
     * @throws Exception
     */
    @Override
    public List<CurrentOrdersInProduction07Vo> queryCurrentOrdersInProduction07Dto() {
        CurrentOrdersInProduction07Dto dto = new CurrentOrdersInProduction07Dto();
        List<CurrentOrdersInProduction07Dto> dtoList = jdbcByAssembleSqlUtil.finaListByObj(dto);
        if (CollectionUtils.isEmpty(dtoList)) {
            return new ArrayList<>();
        }
        List<String> yieldValueList = dtoList.stream().map(CurrentOrdersInProduction07Dto::getYieldValue).collect(Collectors.toList());
        String total = BigDecimalUtil.INSTANCE.accumulatorStr(yieldValueList);
        return dtoList.stream().map(m1 -> {
            CurrentOrdersInProduction07Vo vo = new CurrentOrdersInProduction07Vo();
            vo.setProcessName(m1.getProcessName());
            String molecular = BigDecimalUtil.INSTANCE.divide(m1.getYieldValue(), total).toPlainString();
            vo.setPercentage(BigDecimalUtil.INSTANCE.multiply(molecular, "100").toPlainString());
            return vo;
        }).collect(Collectors.toList());

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Hashtable<String, SqlOnFieldAnnotation> hashtable = GenericsUtils.getRowNameHashSql(OrderProductionVo.class);
        sqlMappingMap.put(OrderProductionVo.class, hashtable);
        Hashtable<String, SqlOnFieldAnnotation> orderCompletionMap = GenericsUtils.getRowNameHashSql(OrderCompletionRateAndYieldRateVo.class);
        sqlMappingMap.put(OrderCompletionRateAndYieldRateVo.class, orderCompletionMap);
    }


    /**
     * 查询车间的 查询 设备数（设备总数，在线，离线）在线率%
     *
     * @param t1
     * @param tenantId
     * @return
     */
    private WorkshopAndRunRateVo getVo(Workshop t1, TenantId tenantId) {
        WorkshopAndRunRateVo vo = JacksonUtil.convertValueNoUNKNOWN(t1, WorkshopAndRunRateVo.class);
        FactoryDeviceQuery factoryDeviceQuery = new FactoryDeviceQuery();
        factoryDeviceQuery.setWorkshopId(t1.getId().toString());
        vo.setOnlineRate(factoryCollectionInformationSvc.queryDeviceStatusNum(tenantId, factoryDeviceQuery).getOnlineRate());
        return vo;
    }


    /**
     * 订单完成情况
     * #####################
     * 修改日期:2023-03-03
     * 修改原因： 返回到前端的是百分比;  当日的
     *
     * @param voList
     * @return
     */
    private List<OrderFulfillmentVo> timelineSupplement(List<OrderFulfillmentVo> voList) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        List<LocalDate> localDateList = DateLocaDateAndTimeUtil.INSTANCE.getBetweenDay(startDate, endDate);
        return localDateList.stream().map(t1 -> {
            OrderFulfillmentVo vo = new OrderFulfillmentVo();
            String timeStr = ChartDateEnums.MONTHS.forMartTime(t1);
            vo.setTime(timeStr);
            String time02dd = DateLocaDateAndTimeUtil.INSTANCE.formatDate(t1, "yyyy-MM-dd");
            Optional<OrderFulfillmentVo> optional = voList.stream().filter(vt -> vt.getTime().equals(time02dd)).findFirst();
            vo.setValue(optional.isPresent() ? optional.get().getValue() : "0");
            return vo;
        }).collect(Collectors.toList());
    }
}
