package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.ThingsboardServerApplication;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.util.JdbcByAssembleSqlUtil;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.votest.UserEntityDto;

import java.util.List;

/**
 * @Project Name: long-win-iot
 * @File Name: JdbcByAssembleSqlUtilTest
 * @Date: 2023/2/17 17:09
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ThingsboardServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JdbcByAssembleSqlUtilTest {
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Test
    public void finListBy() {
        JdbcByAssembleSqlUtil jdbcByAssembleSqlUtil = new JdbcByAssembleSqlUtil();
        jdbcByAssembleSqlUtil.setJdbcTemplate(jdbcTemplate);
        UserEntityDto dto = new UserEntityDto();
        dto.setUserName("林丽香");
        List<UserEntityDto> userEntityDtoList = jdbcByAssembleSqlUtil.finaListByObj(dto);
        log.info("user:" + JacksonUtil.toString(userEntityDtoList));
    }


    @Test
    public void saveOne() {
        JdbcByAssembleSqlUtil jdbcByAssembleSqlUtil = new JdbcByAssembleSqlUtil();
        jdbcByAssembleSqlUtil.setJdbcTemplate(jdbcTemplate);
        UserEntityDto dto = new UserEntityDto();
        dto.setUserName("林丽香");
        jdbcByAssembleSqlUtil.saveOne(dto);
    }
}
