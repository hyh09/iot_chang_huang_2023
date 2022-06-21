package org.thingsboard.server.dao.board.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.thingsboard.server.common.data.vo.bodrd.energy.Input.EnergyHourVo;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.nosql.CompareType;
import org.thingsboard.server.dao.sql.role.dao.JpaSqlTool;
import org.thingsboard.server.dao.sql.role.entity.device.DeviceSqlEntity;
import org.thingsboard.server.dao.sql.role.entity.hour.EffectHourEntity;
import org.thingsboard.server.dao.sql.role.entity.month.EffectMonthEntity;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Project Name: thingsboard
 * File Name: EnergyLargeScreenReposutory
 * Package Name: org.thingsboard.server.dao.board.repository
 * Date: 2022/6/15 14:04
 * author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
@Repository
public class EnergyLargeScreenReposutory extends JpaSqlTool {


    /**
     * 查询24小时的趋势图
     *
     * @param vo
     * @return
     */
    public List<EffectHourEntity> getHourList(EnergyHourVo vo) {
        vo.setGroupBy(ModelConstants.ABSTRACT_TS_COLUMN);
        return querySql(sqlEnergyDataHourTemplate(vo,ModelConstants.HOUR_TABLE_NAME), null, ModelConstants.HOUR_TABLE_NAME_MAP);
    }


    public List<EffectMonthEntity> getMonthDimensionList(EnergyHourVo vo) {
        vo.setGroupBy(ModelConstants.toChar(ModelConstants.ABSTRACT_DATE_COLUMN, "YYYY-MM"));
        return querySql(sqlEnergyDataHourTemplate(vo,ModelConstants.DAY_TABLE_NAME), null, ModelConstants.MONTH_TABLE_NAME_MAP);
    }


    /**
     * 查询设备的id
     *   根据租户id ,工厂 ，产线 ，车间 ，设备id
     * @param vo
     * @return
     */
    public List<UUID> getDeviceIdByVo(EnergyHourVo vo) {
        List<DeviceSqlEntity>  entities =  querySql(getDeviceIdTemplate(vo), null, ModelConstants.DEVICE_COLUMN_FAMILY_NAME+"map1");
        List<UUID> deviceId = entities.stream().map(DeviceSqlEntity::getId).collect(Collectors.toList());
        return  deviceId;
    }


    private String sqlEnergyDataHourTemplate(EnergyHourVo vo,String tableName) {
        STGroup stg = new STGroupFile(ModelConstants.STG_YIE_ID_DATA_02);
        ST sqlST = stg.getInstanceOf(ModelConstants.SQL_ENERGY_TEMPLATE);
        List<String> columnList = new LinkedList<String>();
        columnList.add(ModelConstants.as(vo.getGroupBy(), ModelConstants.HOUR_TIME));
        columnList.add(ModelConstants.as(ModelConstants.sum(ModelConstants.toNumber(ModelConstants.ABSTRACT_WATER_ADDED_VALUE_COLUMN))
                , ModelConstants.ABSTRACT_WATER_ADDED_VALUE_COLUMN));
        columnList.add(ModelConstants.as(ModelConstants.sum(ModelConstants.toNumber(ModelConstants.ABSTRACT_ELECTRIC_ADDED_VALUE_COLUMN))
                , ModelConstants.ABSTRACT_ELECTRIC_ADDED_VALUE_COLUMN));
        columnList.add(ModelConstants.as(ModelConstants.sum(ModelConstants.toNumber(ModelConstants.ABSTRACT_GAS_ADDED_VALUE_COLUMN))
                , ModelConstants.ABSTRACT_GAS_ADDED_VALUE_COLUMN));
        sqlST.add("columns", columnList);
        sqlST.add("model", vo);
        sqlST.add("tableName", tableName);
        List<String> childColumnList = new LinkedList<String>();
        childColumnList.add(ModelConstants.ID_PROPERTY);
        sqlST.add("childColumns", childColumnList);
        sqlST.add("childtableName", ModelConstants.DEVICE_COLUMN_FAMILY_NAME);
        sqlST.add("CompareType", new CompareType());
        String result = sqlST.render();
//        log.info("[sqlEnergyDataHourTemplate]打印当前的sql:{}", result);
        return result;

    }



    private String getDeviceIdTemplate(EnergyHourVo vo) {
        STGroup stg = new STGroupFile(ModelConstants.STG_YIE_ID_DATA_02);
        ST sqlST = stg.getInstanceOf(ModelConstants.SQL_CHILD_DEVICE_TEMPLATE);
        List<String> childColumnList = new LinkedList<String>();
        childColumnList.add(ModelConstants.ID_PROPERTY);
        sqlST.add("childColumns", childColumnList);
        sqlST.add("childModel",vo);
        sqlST.add("childtableName", ModelConstants.DEVICE_COLUMN_FAMILY_NAME);
        String result = sqlST.render();
        log.info("打印查询设备的id的具体的sql:{}",result);
        return result;

    }


}
