package org.thingsboard.server.dao.sql.role.service.Imp.vo;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 * @Project Name: thingsboard
 * @File Name: TskvDto
 * @Date: 2022/11/28 13:46
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@ToString
public class TskvDto {
    private UUID entityId;

    private Long minTs;
    private Long maxTs;
    private Integer keyId;
}
