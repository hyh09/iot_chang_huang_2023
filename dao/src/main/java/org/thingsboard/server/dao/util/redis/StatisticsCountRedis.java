package org.thingsboard.server.dao.util.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.dao.util.CommonUtils;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @Project Name: thingsboard
 * @File Name: StatisticsCountRedis
 * @Date: 2023/1/31 15:12
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Slf4j
@Service
public class StatisticsCountRedis implements StatisticsCountRedisSvc {

    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

    private final String REDIS_HUANSI_COUNT = "REDIS_HUANSI_COUNT:";


    @Override
    public void writeCount(EntityId entityId, TsKvEntry tsKvEntry) {
        String keyPre = getKey(entityId.getId(), tsKvEntry.getTs());
        String value = tsKvEntry.getKey() + tsKvEntry.getTs();
        redisTemplateUtil.pfadd(keyPre, value);
    }

    @Override
    public Long readCount(UUID entityId, LocalDateTime localDateTime) {
        String keyPre = getKey(entityId, CommonUtils.getTimestampOfDateTime(localDateTime));
        Long count = redisTemplateUtil.pfcount(keyPre);
        return count;
    }


    private String getKey(UUID id, long ts) {
        String keyPre = REDIS_HUANSI_COUNT + id.toString() + CommonUtils.getHour(ts);
        return keyPre;
    }
}
