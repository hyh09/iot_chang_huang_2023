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
 * @author: wb04 设备采集量的接口
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
public interface StatisticsCountRedisSvc {

    /**
     * 计算采集量的数据
     *
     * @param entityId
     * @param tsKvEntry
     */
    void writeCount(EntityId entityId, TsKvEntry tsKvEntry);

    /**
     * 读取当日的采集量的数据
     *
     * @param uuidList
     * @param localDateTime
     * @return
     */
    Long readCount(List<UUID> uuidList, LocalDateTime localDateTime);
}
