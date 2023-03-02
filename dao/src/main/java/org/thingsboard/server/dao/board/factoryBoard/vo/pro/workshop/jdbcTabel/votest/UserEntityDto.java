package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.votest;

import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.query.SqlColumnAnnotation;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.singleTable.SingleTableName;

/**
 * @Project Name: long-win-iot
 * @File Name: UserEntityDto
 * @Date: 2023/2/17 17:11
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
@SingleTableName(name = "TEST_USER")
public class UserEntityDto {

    @SqlColumnAnnotation(name = "A1.tTime as userName")
    private String userName;

    @SqlColumnAnnotation(name = " COUNT(1) as roleName ")
    private String roleName;
}
