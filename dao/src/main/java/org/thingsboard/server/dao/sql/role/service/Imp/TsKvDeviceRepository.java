package org.thingsboard.server.dao.sql.role.service.Imp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.dao.sql.role.service.Imp.vo.TskvDto;
import org.thingsboard.server.dao.sql.role.service.TsKvDeviceRepositorySvc;
import org.thingsboard.server.dao.util.StringUtilToll;
import org.thingsboard.server.dao.util.sql.JdbcTemplateUtil;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Project Name: thingsboard
 * @File Name: TsKvDeviceRepository
 * @Date: 2022/11/28 13:45
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
@Repository
public class TsKvDeviceRepository implements TsKvDeviceRepositorySvc {

    @Autowired
    private JdbcTemplateUtil jdbcTemplateUtil;


    public String queryData(UUID entityId, Long startTime, Long endTime, Integer keyId) {
        String sql = "select entity_id as entityId,min(ts) as minTs,max(ts) maxTs,max(\"key\") as keyId from  ts_kv where entity_id=? and ts >=? and ts<=? and key =? " +
                "and concat(long_v, dbl_v, str_v, json_v)<> '0' group  by entity_id";
        Object[] param = {entityId, startTime, endTime, keyId};
        int[] index = {Types.OTHER, Types.BIGINT, Types.BIGINT, Types.BIGINT};
        TskvDto query = jdbcTemplateUtil.queryForObject(sql, param, index, new BeanPropertyRowMapper<TskvDto>(TskvDto.class));
        if (query != null) {
            return queryValue(query);
        }
        return "0";
    }




    private String queryValue(TskvDto tskvDto) {
        String sqlMax = "select concat(long_v, dbl_v, str_v, json_v) from ts_kv tk01 where tk01.entity_id = ? and tk01.\"key\" = ? and tk01.ts = ? ";
        Object[] param = {tskvDto.getEntityId(), tskvDto.getKeyId(), tskvDto.getMaxTs()};
        int[] index = {Types.OTHER, Types.BIGINT, Types.BIGINT};
        String maxValue = jdbcTemplateUtil.queryForObject(sqlMax, param, index, String.class);

        Object[] paramMin = {tskvDto.getEntityId(), tskvDto.getKeyId(), tskvDto.getMinTs()};
        String minValue = jdbcTemplateUtil.queryForObject(sqlMax, paramMin, index, String.class);
        return StringUtilToll.sub(maxValue, minValue);

    }
}
