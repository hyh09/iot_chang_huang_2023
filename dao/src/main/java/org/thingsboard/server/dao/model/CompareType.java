package org.thingsboard.server.dao.model;

import lombok.Data;
import lombok.ToString;

/**
 * Project Name: thingsboard
 * File Name: CompareType
 * Package Name: org.thingsboard.server.dao.model
 * Date: 2022/6/6 14:51
 * author: wb04
 * 业务中文描述: sql的比较
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@ToString
public class CompareType {

    private  String ne =" != ";

    private  String lt =" < ";

    private  String gt=" > ";

    public CompareType() {
    }
}


