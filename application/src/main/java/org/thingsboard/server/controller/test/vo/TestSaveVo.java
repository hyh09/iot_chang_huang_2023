package org.thingsboard.server.controller.test.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Project Name: thingsboard
 * File Name: TestSaveVo
 * Package Name: org.thingsboard.server.controller.test.vo
 * Date: 2022/7/6 11:10
 * author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
public class TestSaveVo {

    private List<UUID> ids;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime localDateTime;
}
