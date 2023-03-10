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
import org.thingsboard.server.dao.util.decimal.BigDecimalUtil;
import org.thingsboard.server.dao.util.decimal.DateLocaDateAndTimeUtil;
import org.thingsboard.server.dao.util.redis.StatisticsCountRedisSvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Project Name: thingsboard
 * @File Name: FactoryCollectionInformationImpl
 * @Date: 2023/1/6 9:54
 * @author: wb04
 * ??????????????????:
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
     * ###2023-02-23 ?????? % ????????????
     * ????????????????????????????????? 1.????????????????????? ?????????
     * ?????? ??????????????????????????????????????????????????????%
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
     * ???????????????????????????-????????????
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
        /** ?????????????????????*/
        List<DeviceEntity> deviceEntityList = deviceDao.findAllByEntity(factoryDeviceQueryConvertDeviceEntity(factoryDeviceQuery));

        return new HourlyTrendGraphOfCollectionVolumeVo(getHourData(todayTimeLine, deviceEntityList),
                getHourData(yesterdayTimeLine, deviceEntityList));
    }

    /**
     * ????????????????????????
     *
     * @param tenantId
     * @param factoryDeviceQuery
     * @param dateEnums          ??????       ???????????? / ???????????? ??? (??????-?????????  /????????????
     *                           ??????????????? ??????????????????????????????????????????
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
        return getRunRate(trendChartRateDtoList, dateEnums, deviceEntityList.size());
    }

    /**
     * ???????????????????????????
     *
     * @param tenantId           ??????id
     * @param factoryDeviceQuery ?????????????????????id
     * @return
     */
    @Override
    public RatePieChartVo queryPieChart(TenantId tenantId, FactoryDeviceQuery factoryDeviceQuery) {
        /**1 ???????????????????????????*/
        List<DeviceEntity> deviceEntityList = deviceDao.findAllByEntity(factoryDeviceQueryConvertDeviceEntity(factoryDeviceQuery));
        /** 2 ???????????????id ??????*/
        List<UUID> uuids = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(deviceEntityList)) {
            uuids.addAll(deviceEntityList.stream().map(DeviceEntity::getUuid).collect(Collectors.toList()));
        }
        /** 3 ??????  ????????? */
        LocalDate localDate = LocalDate.now();
        LocalDate yesterday = localDate.plusDays(-1);
        /**4. */
        List<TrendChartRateDto> todayDto = startTimeOfThisDay(uuids, LocalDate.now());
        List<TrendChartRateDto> yesterdayDto = startTimeOfThisDay(uuids, yesterday);
        RatePieChartVo vo = new RatePieChartVo();
        vo.setCurrentValue(runRateOne(todayDto, uuids.size()));
        vo.setYesterdayValue(runRateOne(yesterdayDto, uuids.size()));
        return vo;
    }


    private DeviceEntity factoryDeviceQueryConvertDeviceEntity(FactoryDeviceQuery factoryDeviceQuery) {
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setFactoryId(UUID.fromString(factoryDeviceQuery.getFactoryId()));
        return deviceEntity;
    }

    /**
     * ???????????????????????? (????????????)
     *
     * @param zeroTime        ??????0???
     * @param twentyThreeTime ??????23???
     * @return
     */
    private List<LocalDateTime> getTodayTimeLine(LocalDateTime zeroTime, LocalDateTime twentyThreeTime) {
        return DateLocaDateAndTimeUtil.INSTANCE.getBetweenHour(zeroTime, twentyThreeTime);
    }

    /**
     * ???????????????????????? (????????????)
     *
     * @param zeroTime        ??????0???
     * @param twentyThreeTime ??????23???
     * @return
     */
    private List<LocalDateTime> getYesterdayTimeLine(LocalDateTime zeroTime, LocalDateTime twentyThreeTime) {
        LocalDateTime zeroYesterday = zeroTime.minusDays(1);
        LocalDateTime twentyThreeYesterday = twentyThreeTime.minusDays(1);
        return DateLocaDateAndTimeUtil.INSTANCE.getBetweenHour(zeroYesterday, twentyThreeYesterday);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param currentTimeList  ???????????????
     * @param deviceEntityList ????????????
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
     * ??????????????? java.lang.IllegalStateException: Duplicate key 2023-02 (attempted merging values 13914352 and 13106212)
     *
     * @param trendChartRateDtoList
     * @param dateEnums
     * @return
     */
    private List<ChartDataVo> getRunRate(List<TrendChartRateDto> trendChartRateDtoList, ChartDateEnums dateEnums, Integer deviceCount) {
        /**1 ???????????????????????? ??????????????? ????????? ??????????????????*/
        ChartDateEnumsToLocalDateVo dateVoDate = dateEnums.currentConvert();
        /**2 ??????????????? x??? ?????????*/
        List<LocalDate> localDates = DateLocaDateAndTimeUtil.INSTANCE.getMiddleDate(dateEnums, dateVoDate.getBeginDate(), dateVoDate.getEndDate());
//        Map<String, String> map = trendChartRateDtoList.stream().collect(Collectors.toMap(TrendChartRateDto::getdateStr, TrendChartRateDto::getBootTime));
        Map<String, String> map = new HashMap<>();
        trendChartRateDtoList.stream().forEach(trendChartRateDto -> {
            map.put(DateLocaDateAndTimeUtil.formatDate(trendChartRateDto.getBdate(), dateEnums), trendChartRateDto.getBootTime());

        });

        List<ChartDataVo> list =
                localDates.stream().map(t1 -> {
                    ChartDataVo v1 = new ChartDataVo();
                    String timeStr = dateEnums.forMartTime(t1);
                    v1.setTime(timeStr);
                    String value = map.get(DateLocaDateAndTimeUtil.formatDate(t1, dateEnums));
                    v1.setValue(runRateCalculation(value, t1, dateEnums, deviceCount));
                    return v1;
                }).collect(Collectors.toList());
        return list;

    }


    /**
     * ???????????????   ????????????(???????????? / ???????????? ??? (??????-?????????  /?????????
     *
     * @param value
     * @param localDate
     * @param dateEnums
     * @return
     */
    private String runRateCalculation(String value, LocalDate localDate, ChartDateEnums dateEnums, Integer deviceSize) {
        if (StringUtils.isEmpty(value)) {
            return "0";
        }

        String timeDifference = timeDifference(localDate, dateEnums);
        /** ??????????????? ?? ???????????? ?? ????????? */
        BigDecimalUtil decimalUtil = new BigDecimalUtil(4, RoundingMode.HALF_UP);
        String divisorResult = decimalUtil.divide(value, timeDifference, deviceSize).toPlainString();
        /** ??????????????? ?????? 100 */
        String percentageOfNumber = BigDecimalUtil.INSTANCE.multiply(divisorResult, "100").toPlainString();
        return percentageOfNumber;


    }


    /**
     * ###2023-02-23 ???????????? % ??? ?????????????????????
     * ??????????????????---???????????? ?????? ????????? ????????????????????????????????? ????????????
     * ###2023-03-02 bug????????? bootTime {???????????????????????????????????????} ??86400000L{1?????????????????????} ??? ???????????? {[sql2]} ???????????? ?????????????????????
     *
     * @param trendChartRateDtoList
     * @return
     */
    private String runRateOne(List<TrendChartRateDto> trendChartRateDtoList, Integer deviceCount) {
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
        String valueStr02 = decimalUtil.divide(valueStr, deviceCount).toPlainString();
        String resultStr = BigDecimalUtil.INSTANCE.multiply(valueStr02, 100).toPlainString();
        return resultStr;

    }

    /**
     * ???????????? ??? (??????-?????????
     * 1.??????????????? ??????  HSConstants.DAY_TIME  24??????????????????
     * 2.??????????????? ?????????????????????????????? ???????????????????????????????????????
     *
     * @param localDate
     * @param dateEnums ??? or ???
     * @return
     */
    private String timeDifference(LocalDate localDate, ChartDateEnums dateEnums) {
        if (dateEnums == ChartDateEnums.MONTHS) {
            return HSConstants.DAY_TIME.toString();
        }
        LocalDateTime currentTime = LocalDateTime.of(localDate, LocalTime.parse("00:00:00"));
        LocalDateTime firstDayOfMonth = currentTime.with(TemporalAdjusters.firstDayOfMonth());
        LocalDateTime lastDayOfMonth = currentTime.with(TemporalAdjusters.lastDayOfMonth());
        Duration duration = Duration.between(firstDayOfMonth, lastDayOfMonth);
        Long millisTime = duration.toMillis();
        return millisTime.toString();

    }


}
