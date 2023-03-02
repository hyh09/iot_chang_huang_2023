package org.thingsboard.server.dao.board.factoryBoard.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.board.factoryBoard.impl.base.TrendChartOfOperatingRateJdbcImpl;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryCollectionInformationSvc;
import org.thingsboard.server.dao.board.factoryBoard.vo.collection.chart.TrendChartRateDto;
import org.thingsboard.server.dao.board.factoryBoard.vo.collection.collectionVolume.HourlyTrendGraphOfCollectionVolumeVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.collection.onlie.DeviceStatusNumVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.collection.piechart.RatePieChartVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.ChartDataVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request.ChartDateEnums;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request.ChartDateEnumsToLocalDateVo;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.entity.vo.DeviceOnlineStatusResult;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;
import org.thingsboard.server.dao.hs.service.DeviceMonitorService;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.dao.util.decimal.BigDecimalUtil;
import org.thingsboard.server.dao.util.decimal.DateLocaDateAndTimeUtil;
import org.thingsboard.server.dao.util.redis.StatisticsCountRedisSvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Project Name: thingsboard
 * @File Name: FactoryCollectionInformationImpl
 * @Date: 2023/1/6 9:54
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Slf4j
@Service
public class FactoryCollectionInformationImpl extends TrendChartOfOperatingRateJdbcImpl implements FactoryCollectionInformationSvc {

    @Autowired
    private DeviceMonitorService deviceMonitorService;
    @Autowired
    private StatisticsCountRedisSvc statisticsCountRedisSvc;
    @Autowired
    private DeviceDao deviceDao;

    public FactoryCollectionInformationImpl(@Autowired JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }


    /**
     * ###2023-02-23 去掉 % 百分比；
     * 涉及的接口：工厂看板的 1.查询车间的信息 的接口
     * 查询 设备数（设备总数，在线，离线）在线率%
     */
    @Override
    public DeviceStatusNumVo queryDeviceStatusNum(TenantId tenantId, FactoryDeviceQuery factoryDeviceQuery) {
        DeviceOnlineStatusResult deviceOnlineStatusResult = deviceMonitorService.getDeviceOnlineStatusData(tenantId, factoryDeviceQuery);
        DeviceStatusNumVo deviceStatusNumVo = JacksonUtil.convertValue(deviceOnlineStatusResult, DeviceStatusNumVo.class);
        BigDecimalUtil bigDecimalUtil = new BigDecimalUtil(4, RoundingMode.HALF_UP);
        BigDecimal deviceAfterResult = bigDecimalUtil.divide(deviceOnlineStatusResult.getOnLineDeviceCount(), deviceOnlineStatusResult.getAllDeviceCount());
        String rate = BigDecimalUtil.INSTANCE.multiply(deviceAfterResult, "100").toPlainString();
        deviceStatusNumVo.setOnlineRate(rate);
        return deviceStatusNumVo;
    }

    /**
     * 采集量趋势图的接口-每小时的
     *
     * @param tenantId
     * @param factoryDeviceQuery
     * @return
     */
    @Override
    public HourlyTrendGraphOfCollectionVolumeVo queryCollectionVolumeByHourly(TenantId tenantId, FactoryDeviceQuery factoryDeviceQuery) {
        DateLocaDateAndTimeUtil dateAndTimeUtil = DateLocaDateAndTimeUtil.INSTANCE;
        LocalDateTime zeroTime = dateAndTimeUtil.getTodayZeroTime();
        LocalDateTime twentyThreeTime = dateAndTimeUtil.getTodayTwentyThreeTime();

        List<LocalDateTime> todayTimeLine = getTodayTimeLine(zeroTime, twentyThreeTime);
        List<LocalDateTime> yesterdayTimeLine = getYesterdayTimeLine(zeroTime, twentyThreeTime);
        /** 查询设备的接口*/
        List<DeviceEntity> deviceEntityList = deviceDao.findAllByEntity(factoryDeviceQueryConvertDeviceEntity(factoryDeviceQuery));

        return new HourlyTrendGraphOfCollectionVolumeVo(getHourData(todayTimeLine, deviceEntityList),
                getHourData(yesterdayTimeLine, deviceEntityList));
    }

    /**
     * 开机率趋势图接口
     *
     * @param tenantId
     * @param factoryDeviceQuery
     * @param dateEnums          月：       开机时长 / 可用时间 月 (开始-结束）  /设备数、
     *                           开机时长： 工厂下的设备的开机时长的累加
     * @return
     */
    @Override
    public List<ChartDataVo> queryTrendChartOfOperatingRate(TenantId tenantId, FactoryDeviceQuery factoryDeviceQuery, ChartDateEnums dateEnums) {
        List<DeviceEntity> deviceEntityList = deviceDao.findAllByEntity(factoryDeviceQueryConvertDeviceEntity(factoryDeviceQuery));
        List<UUID> uuids = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(deviceEntityList)) {
            uuids.addAll(deviceEntityList.stream().map(DeviceEntity::getUuid).collect(Collectors.toList()));
        }
        List<TrendChartRateDto> trendChartRateDtoList = (dateEnums == ChartDateEnums.YEARS) ? startTimeOfThisYear(uuids) : startTimeOfThisMonth(uuids);
        return getRunRate(trendChartRateDtoList, dateEnums);
    }

    /**
     * 开机率的饼状图接口
     *
     * @param tenantId           租户id
     * @param factoryDeviceQuery 目前只传了工厂id
     * @return
     */
    @Override
    public RatePieChartVo queryPieChart(TenantId tenantId, FactoryDeviceQuery factoryDeviceQuery) {
        List<DeviceEntity> deviceEntityList = deviceDao.findAllByEntity(factoryDeviceQueryConvertDeviceEntity(factoryDeviceQuery));
        List<UUID> uuids = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(deviceEntityList)) {
            uuids.addAll(deviceEntityList.stream().map(DeviceEntity::getUuid).collect(Collectors.toList()));
        }
        LocalDate localDate = LocalDate.now();
        LocalDate yesterday = localDate.plusDays(-1);
        List<TrendChartRateDto> todayDto = startTimeOfThisDay(uuids, LocalDate.now());
        List<TrendChartRateDto> yesterdayDto = startTimeOfThisDay(uuids, yesterday);
        RatePieChartVo vo = new RatePieChartVo();
        vo.setCurrentValue(runRateOne(todayDto));
        vo.setYesterdayValue(runRateOne(yesterdayDto));
        return vo;
    }


    private DeviceEntity factoryDeviceQueryConvertDeviceEntity(FactoryDeviceQuery factoryDeviceQuery) {
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setFactoryId(UUID.fromString(factoryDeviceQuery.getFactoryId()));
        return deviceEntity;
    }

    /**
     * 获取今天的时间抽 (整点时间)
     *
     * @param zeroTime        今天0点
     * @param twentyThreeTime 今天23点
     * @return
     */
    private List<LocalDateTime> getTodayTimeLine(LocalDateTime zeroTime, LocalDateTime twentyThreeTime) {
        return DateLocaDateAndTimeUtil.INSTANCE.getBetweenHour(zeroTime, twentyThreeTime);
    }

    /**
     * 获取昨天的时间抽 (整点时间)
     *
     * @param zeroTime        今天0点
     * @param twentyThreeTime 今天23点
     * @return
     */
    private List<LocalDateTime> getYesterdayTimeLine(LocalDateTime zeroTime, LocalDateTime twentyThreeTime) {
        LocalDateTime zeroYesterday = zeroTime.minusDays(1);
        LocalDateTime twentyThreeYesterday = twentyThreeTime.minusDays(1);
        return DateLocaDateAndTimeUtil.INSTANCE.getBetweenHour(zeroYesterday, twentyThreeYesterday);
    }

    /**
     * 获取返回当前时间轴对应的采集量
     *
     * @param currentTimeList  当前时间轴
     * @param deviceEntityList 设备列表
     * @return
     */
    private List<ChartDataVo> getHourData(List<LocalDateTime> currentTimeList, List<DeviceEntity> deviceEntityList) {
        List<UUID> idList = CollectionUtils.isEmpty(deviceEntityList) ? new ArrayList<>() : deviceEntityList.stream().map(DeviceEntity::getId).collect(Collectors.toList());
        return currentTimeList.stream().map(t1 -> {
            ChartDataVo vo = new ChartDataVo();
            vo.setTime(DateTimeFormatter.ofPattern("HH:mm").format(t1));
            Long value = statisticsCountRedisSvc.readCount(idList, t1);
            vo.setValue(String.valueOf(value));
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 报的异常： java.lang.IllegalStateException: Duplicate key 2023-02 (attempted merging values 13914352 and 13106212)
     *
     * @param trendChartRateDtoList
     * @param dateEnums
     * @return
     */
    private List<ChartDataVo> getRunRate(List<TrendChartRateDto> trendChartRateDtoList, ChartDateEnums dateEnums) {
        ChartDateEnumsToLocalDateVo dateVoDate = dateEnums.currentConvert();
        List<LocalDate> localDates = DateLocaDateAndTimeUtil.INSTANCE.getMiddleDate(dateEnums, dateVoDate.getBeginDate(), dateVoDate.getEndDate());
//        Map<String, String> map = trendChartRateDtoList.stream().collect(Collectors.toMap(TrendChartRateDto::getdateStr, TrendChartRateDto::getBootTime));
        Map<String, String> map = new HashMap<>();
        trendChartRateDtoList.stream().forEach(trendChartRateDto -> {
            map.put(DateLocaDateAndTimeUtil.formatDate(trendChartRateDto.getBdate(),dateEnums),trendChartRateDto.getBootTime());

        });

        List<ChartDataVo> list =
                localDates.stream().map(t1 -> {
                    ChartDataVo v1 = new ChartDataVo();
                    String timeStr = dateEnums.forMartTime(t1);
                    v1.setTime(timeStr);
                    String value = map.get(DateLocaDateAndTimeUtil.formatDate(t1,dateEnums));
                    v1.setValue(runRateCalculation(value, dateVoDate, trendChartRateDtoList));
                    return v1;
                }).collect(Collectors.toList());
        return list;

    }

    /**
     * 计算规则：   开机时长(毫秒数） / 可用时间 月 (开始-结束）  /设备数
     *
     * @param value
     * @param dateVoDate
     * @param trendChartRateDtoList
     * @return
     */
    private String runRateCalculation(String value, ChartDateEnumsToLocalDateVo dateVoDate, List<TrendChartRateDto> trendChartRateDtoList) {
        if (StringUtils.isEmpty(value)) {
            return "0";
        }
        Integer deviceSize = trendChartRateDtoList.size();
        LocalDate startTime = dateVoDate.getBeginDate();
        LocalDate endTime = dateVoDate.getEndDate();
        Long startTimeOfLong = CommonUtils.getTimestampOfDateTime(LocalDateTime.of(startTime, LocalTime.parse("00:00:00")));
        Long endTimeOfLong = CommonUtils.getTimestampOfDateTime(LocalDateTime.of(endTime, LocalTime.parse("00:00:00")));
        BigDecimal timeDifference = BigDecimalUtil.INSTANCE.subtract(endTimeOfLong, startTimeOfLong);
        return BigDecimalUtil.INSTANCE.divide(value, timeDifference, deviceSize).toPlainString();


    }

    /**
     * ###2023-02-23 修改去掉 % ； 前端要求的规范
     * 饼状图的接口---计算当天 或者 昨日的 天维度的单条的开机率的 百分之比
     *
     * @param trendChartRateDtoList
     * @return
     */
    private String runRateOne(List<TrendChartRateDto> trendChartRateDtoList) {
        if (CollectionUtils.isEmpty(trendChartRateDtoList)) {
            return "0";
        }
        TrendChartRateDto trendChartRateDto = trendChartRateDtoList.stream().findFirst().get();
        String value = trendChartRateDto.getBootTime();
        if (StringUtils.isEmpty(value)) {
            return "0";
        }
        BigDecimalUtil decimalUtil = new BigDecimalUtil(4, RoundingMode.HALF_UP);
        String valueStr = decimalUtil.divide(value, HSConstants.DAY_TIME).toPlainString();
        String resultStr = BigDecimalUtil.INSTANCE.multiply(valueStr, 100).toPlainString();
        return resultStr ;

    }

}
