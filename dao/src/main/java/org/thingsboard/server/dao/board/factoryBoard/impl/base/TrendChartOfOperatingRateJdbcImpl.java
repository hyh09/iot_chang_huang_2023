package org.thingsboard.server.dao.board.factoryBoard.impl.base;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.thingsboard.server.dao.board.factoryBoard.vo.collection.chart.TrendChartRateDto;

import java.util.List;
import java.util.UUID;

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

    private String SELECT_TIME_BY_MONTH = "SELECT   bdate, sum((total_time + start_time)) time01 FROM trep_day_sta_detail where bdate>=date_trunc( 'month', now() ) " +
            "and and entity_id in  (:deviceIdList) group by bdate";

    private String SELECT_TIME_BY_YEAR = "SELECT   to_date(to_char(t1.bdate ,'yyyy-MM'),'yyyy-MM') as bdate, sum((total_time + start_time)) time01 FROM trep_day_sta_detail where bdate>=date_trunc( 'year', now() ) " +
            " and entity_id in  (:deviceIdList) group by to_char(t1.bdate ,'yyyy-MM')  ";


    /**
     * 查询开机率的趋势图接口
     */
    public List<TrendChartRateDto> startTimeOfThisMonth(List<UUID> deviceIdList) {
        return getTrendChartRateDtos(deviceIdList, SELECT_TIME_BY_MONTH);

    }

    /**
     * 查询开机率的趋势图接口
     */
    public List<TrendChartRateDto> startTimeOfThisYear(List<UUID> deviceIdList) {
        return getTrendChartRateDtos(deviceIdList, SELECT_TIME_BY_YEAR);

    }


    @NotNull
    private List<TrendChartRateDto> getTrendChartRateDtos(List<UUID> deviceIdList, String select_time_by_month) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("deviceIdList", deviceIdList);
        NamedParameterJdbcTemplate givenParamJdbcTemp = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<TrendChartRateDto> dtoList = givenParamJdbcTemp.query(select_time_by_month, parameters, new BeanPropertyRowMapper<>(TrendChartRateDto.class));
        return dtoList;
    }


}
