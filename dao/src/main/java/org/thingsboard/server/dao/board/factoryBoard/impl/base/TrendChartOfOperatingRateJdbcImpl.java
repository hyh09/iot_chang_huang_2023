package org.thingsboard.server.dao.board.factoryBoard.impl.base;

import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.thingsboard.server.dao.board.factoryBoard.vo.collection.chart.TrendChartRateDto;

import java.time.LocalDate;
import java.util.*;

/**
 * @Project Name: thingsboard
 * @File Name: TrendChartOfOperatingRateJdbcImpl
 * @Date: 2023/2/14 14:33
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
public abstract class TrendChartOfOperatingRateJdbcImpl {

    protected JdbcTemplate jdbcTemplate;

    public TrendChartOfOperatingRateJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String SELECT_TIME_BY_MONTH = "SELECT   t1.bdate, sum((t1.total_time + t1.start_time)) bootTime FROM trep_day_sta_detail t1 where t1.bdate>=date_trunc( 'month', now() ) " +
            " and  entity_id in  (:deviceIdList) group by bdate";

    private String SELECT_TIME_BY_YEAR = "SELECT   to_date(to_char(t1.bdate ,'yyyy-MM'),'yyyy-MM') as bdate, sum((t1.total_time + t1.start_time)) bootTime FROM trep_day_sta_detail t1 where t1.bdate>=date_trunc( 'year', now() ) " +
            " and t1.entity_id in  (:deviceIdList) group by to_char(t1.bdate ,'yyyy-MM')  ";

    private String SELECT_TIME_BY_DAY = "SELECT   t1.bdate, sum((t1.total_time + t1.start_time)) bootTime FROM trep_day_sta_detail t1 where t1.bdate= :bdate  " +
            " and  entity_id in  (:deviceIdList) group by bdate";

    /**
     * 查询开机率的趋势图接口
     */
    public List<TrendChartRateDto> startTimeOfThisDay(List<UUID> deviceIdList, LocalDate localDate) {
        if (CollectionUtils.isEmpty(deviceIdList)) {
            return new ArrayList<>();
        }
        Map<String, Object> map = getMapByParameter(deviceIdList);
        map.put("bdate", localDate);
        return getTrendChartRateDtos(map, SELECT_TIME_BY_DAY);

    }

    /**
     * 查询开机率的趋势图接口
     */
    public List<TrendChartRateDto> startTimeOfThisMonth(List<UUID> deviceIdList) {
        if (CollectionUtils.isEmpty(deviceIdList)) {
            return new ArrayList<>();
        }
        return getTrendChartRateDtos(getMapByParameter(deviceIdList), SELECT_TIME_BY_MONTH);

    }

    /**
     * 查询开机率的趋势图接口
     */
    public List<TrendChartRateDto> startTimeOfThisYear(List<UUID> deviceIdList) {
        if (CollectionUtils.isEmpty(deviceIdList)) {
            return new ArrayList<>();
        }
        return getTrendChartRateDtos(getMapByParameter(deviceIdList), SELECT_TIME_BY_YEAR);

    }


    @NotNull
    private List<TrendChartRateDto> getTrendChartRateDtos(Map<String, Object> map, String select_time_by_month) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        if (map != null && !map.isEmpty()) {
            map.forEach((k1, v1) -> {
                parameters.addValue(k1, v1);
            });
        }
        NamedParameterJdbcTemplate givenParamJdbcTemp = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<TrendChartRateDto> dtoList = givenParamJdbcTemp.query(select_time_by_month, parameters, new BeanPropertyRowMapper<>(TrendChartRateDto.class));
        if (CollectionUtils.isEmpty(dtoList)) {
            return new ArrayList<TrendChartRateDto>();
        }
        return dtoList;
    }

    private Map<String, Object> getMapByParameter(List<UUID> deviceIdList) {
        Map<String, Object> map = new HashMap<>();
        map.put("deviceIdList", deviceIdList);
        return map;
    }


}
