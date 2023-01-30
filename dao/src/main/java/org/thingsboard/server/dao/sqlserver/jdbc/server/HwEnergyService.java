package org.thingsboard.server.dao.sqlserver.jdbc.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.thingsboard.server.dao.sqlserver.jdbc.dto.HwEnergyDto;
import org.thingsboard.server.dao.sqlserver.server.vo.order.HwEnergyEnums;
import org.thingsboard.server.dao.sqlserver.server.vo.order.HwEnergyVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Project Name: thingsboard
 * @File Name: HwEnergyService
 * @Date: 2023/1/30 10:23
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Slf4j
@Service
public class HwEnergyService {

    @Autowired
    @Qualifier("sqlServerTemplate")
    protected JdbcTemplate jdbcTemplate;

    /**
     * 获取水 电 气的单价
     * @return
     */
    public Map<String,String> queryUnitPrice(){
        List<HwEnergyDto> hwEnergyDtoList =  this.queryHwEnergyDto();
        if(CollectionUtils.isNotEmpty(hwEnergyDtoList)){
          return   hwEnergyDtoList.stream().collect(Collectors.toMap(HwEnergyDto::getSName,HwEnergyDto::getNCurrPrice));
        }
        return  new HashMap<>();
    }




    public List<HwEnergyDto> queryHwEnergyDto(){
        String sql = "select A1.uGUID,A1.sCode,A1.sName,A1.sClass,A1.nCurrPrice,A1.sUnitName,A1.nCalcCoefficient,A1.isort from dbo.hwEnergy A1 WHERE A1.sName in (:sNameList)  ";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("sNameList", Stream.of(HwEnergyEnums.values()).map(HwEnergyEnums::getChineseField).collect(Collectors.toList()));
        NamedParameterJdbcTemplate givenParamJdbcTemp = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<HwEnergyDto> data = givenParamJdbcTemp.query(sql, parameters, new BeanPropertyRowMapper<>(HwEnergyDto.class));
        return data;
    }
}
