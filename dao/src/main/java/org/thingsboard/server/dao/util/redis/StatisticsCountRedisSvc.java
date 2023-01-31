package org.thingsboard.server.dao.util.redis;

import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.TsKvEntry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @Project Name: thingsboard
 * @File Name: StatisticsCountRedisSvc
 * @Date: 2023/1/31 15:15
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
public interface StatisticsCountRedisSvc {

    void writeCount(EntityId entityId, TsKvEntry tsKvEntry);

    Long readCount(List<UUID> uuidList, LocalDateTime localDateTime);
}
