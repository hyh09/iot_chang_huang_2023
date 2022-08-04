package org.thingsboard.server.dao.sql.census.vo;

import lombok.Data;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;

/**
 * Project Name: thingsboard
 * File Name: StaticalDataVo
 * Package Name: org.thingsboard.server.dao.sql.census.vo
 * Date: 2022/7/5 16:42
 * author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
public class StaticalDataVo {

    private String firstValue;

    private long firstTime;

    private String lastValue;

    private  long lastTime;


    private  String addValue;

}
