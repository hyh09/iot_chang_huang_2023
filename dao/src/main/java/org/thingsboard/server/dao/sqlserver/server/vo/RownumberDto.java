package org.thingsboard.server.dao.sqlserver.server.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Project Name: thingsboard
 * @File Name: RownumberDto
 * @Date: 2022/12/27 9:55
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
public class RownumberDto {

    protected Integer rownumber;

    /**
     * 开始时间
     */
    protected LocalDateTime factStartTime;

    /**
     * 结束时间
     */
    protected LocalDateTime factEndTime;

    protected Long createdTime;

    protected Long  updatedTime;


}
