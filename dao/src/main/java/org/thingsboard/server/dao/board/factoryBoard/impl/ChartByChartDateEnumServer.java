package org.thingsboard.server.dao.board.factoryBoard.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.dao.board.factoryBoard.dto.ChartByChartEnumsDto;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request.ChartDateEnums;

import java.util.List;

/**
 * @Project Name: long-win-iot
 * @File Name: ChartByChartDateEnumServer
 * @Date: 2023/1/5 14:00
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */

public abstract class ChartByChartDateEnumServer {

    protected JdbcTemplate jdbcTemplate;

    public ChartByChartDateEnumServer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected List<ChartByChartEnumsDto> queryChartEnums(QueryTsKvVo queryTsKvVo, ChartDateEnums dateEnums) {
        String sql = " ::date as localDateTime,\n" +
                "SUM ( to_number( water_added_value, '999999999999999999999999999999999999.9999' ) ) AS waterValue,\n" +
                "SUM ( to_number( electric_added_value, '999999999999999999999999999999999999.9999' ) ) AS electricValue,\n" +
                "SUM ( to_number( gas_added_value, '999999999999999999999999999999999999.9999' ) ) AS gasValue\n" +
                "  from  hs_statistical_data  h1 ,device  d1 " +
                "  where h1.entity_id  = d1.id ";
        String selectField = groupByField(dateEnums);
        StringBuffer sqlAll = new StringBuffer(" SELECT ").append(selectField).append(sql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        structureSQL(queryTsKvVo, sqlAll, parameters);
        sqlAll.append(" GROUP BY ").append(selectField);
        NamedParameterJdbcTemplate givenParamJdbcTemp = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<ChartByChartEnumsDto> data = givenParamJdbcTemp.query(sqlAll.toString(), parameters, new BeanPropertyRowMapper<>(ChartByChartEnumsDto.class));
        return data;
    }


    private void structureSQL(QueryTsKvVo queryTsKvVo, StringBuffer sonSql01, MapSqlParameterSource param) {
        if (queryTsKvVo.getTenantId() != null) {
            sonSql01.append(" and  d1.tenant_id = :tenantId");
            param.addValue("tenantId", queryTsKvVo.getTenantId());
            sonSql01.append("  and (position('\"gateway\":true' in d1.additional_info)=0 or    d1.additional_info is null )");

        }
        if (queryTsKvVo.getFactoryId() != null) {
            sonSql01.append(" and  d1.factory_id = :factoryId");
            param.addValue("factoryId", queryTsKvVo.getFactoryId());
        }
        if (queryTsKvVo.getWorkshopId() != null) {
            sonSql01.append(" and  d1.workshop_id = :workshopId");
            param.addValue("workshopId", queryTsKvVo.getWorkshopId());
        }
        if (queryTsKvVo.getProductionLineId() != null) {
            sonSql01.append(" and  d1.production_line_id = :productionLineId");
            param.addValue("productionLineId", queryTsKvVo.getProductionLineId());
        }
        if (queryTsKvVo.getDeviceId() != null) {
            sonSql01.append(" and  d1.id = :did");
            param.addValue("did", queryTsKvVo.getDeviceId());
        }
        if (queryTsKvVo.getDictDeviceId() != null) {
            sonSql01.append(" and  d1.dict_device_id = :dictId ");
            param.addValue("dictId", queryTsKvVo.getDictDeviceId());
        }
    }

    private String groupByField(ChartDateEnums dateEnums) {
        return (new StringBuffer("date_trunc(\'")).append(dateEnums.getPrecision()).append("\' ,h1.\"date\")").toString();
    }


}
