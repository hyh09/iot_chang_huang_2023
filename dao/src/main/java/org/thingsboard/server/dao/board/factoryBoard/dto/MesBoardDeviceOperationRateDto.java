package org.thingsboard.server.dao.board.factoryBoard.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.enums.DataBaseTypeEnums;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.query.SqlColumnAnnotation;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.query.SqlOnFromTableAnnotation;

import java.util.UUID;

/**
 * @Project Name: long-win-iot
 * @File Name: MesBoardDeviceOperationRateDto
 * @Date: 2023/3/7 14:55
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
@ApiModel(value = "车间看板-设备开机率top10")
@SqlOnFromTableAnnotation(from = "device d1 LEFT JOIN trep_day_sta_detail t1 on d1.id =t1.entity_id ",
        whereValue = " ( ( t1.total_time + t1.start_time ) ) is  not null  and  t1.bdate=  current_date ",
        groupByLast = "t1.entity_id",
        orderBy = " sum((t1.total_time + t1.start_time)) DESC",
        dataBaseType = DataBaseTypeEnums.POSTGRESQL)
public class MesBoardDeviceOperationRateDto {


    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @SqlColumnAnnotation(name = "max(d1.rename)")
    private String name;

    /**
     * 开机时长
     */
    @ApiModelProperty(value = "开机时长", notes = "不显示")
    @SqlColumnAnnotation(name = "sum((t1.total_time + t1.start_time))")
    private Long time;

    /**
     * id
     */
    @ApiModelProperty(value = "id", notes = "不显示")
    @SqlColumnAnnotation(name = "t1.entity_id")
    private UUID id;

    @SqlColumnAnnotation(name = "d1.workshop_id", ignoreSelectField = true, queryWhere = "=")
    private UUID workshopId;

}
