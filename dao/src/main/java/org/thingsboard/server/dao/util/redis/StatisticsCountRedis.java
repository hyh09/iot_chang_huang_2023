package org.thingsboard.server.dao.util.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.dao.util.decimal.BigDecimalUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        redisTemplateUtil.expireDays(keyPre,3);//保存3天的量;
    }

    @Override
    public Long readCount(List<UUID> uuidList, LocalDateTime localDateTime) {
        List<String> keyList = uuidList.stream().map(m1 -> getKey(m1, CommonUtils.getTimestampOfDateTime(localDateTime))).collect(Collectors.toList());
        Long count = keyList.stream().map(str1 -> redisTemplateUtil.pfcount(str1)).map(l1 -> BigDecimalUtil.INSTANCE.formatByObject(l1)).reduce(BigDecimal.ZERO, BigDecimal::add).longValue();
        return count;
    }


    private String getKey(UUID id, long ts) {
        String keyPre = REDIS_HUANSI_COUNT + id.toString() + CommonUtils.getHour(ts);
        return keyPre;
    }
}
