package org.thingsboard.server.dao.util.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thingsboard.server.ThingsboardServerApplication;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.kv.BasicTsKvEntry;
import org.thingsboard.server.common.data.kv.StringDataEntry;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.dao.util.decimal.DateLocaDateAndTimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Project Name: long-win-iot
 * @File Name: StatisticsCountRedisSvcTest
 * @Date: 2023/1/31 16:06
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ThingsboardServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatisticsCountRedisSvcTest {

    @Autowired
    private StatisticsCountRedisSvc statisticsCountRedisSvc;

    private List<UUID> uuidList = new ArrayList<>() {{
        add(UUID.fromString("24d0aa00-589c-11ec-afcd-2bd77acada2c"));
        add(UUID.fromString("24d0aa00-589c-11ec-afcd-2bd77acada1c"));

    }};

    // 17
    @Test
    public void write() {
        //24d0aa00-589c-11ec-afcd-2bd77acada1c"  24d0aa00-589c-11ec-afcd-2bd77acada2c"
        DeviceId deviceId = new DeviceId(UUID.fromString("24d0aa00-589c-11ec-afcd-2bd77acada2c"));
        TsKvEntry dataPoint1 = new BasicTsKvEntry(System.currentTimeMillis(), new StringDataEntry("temperature", "009"));
        statisticsCountRedisSvc.writeCount(deviceId, dataPoint1);

        DeviceId deviceId1 = new DeviceId(UUID.fromString("24d0aa00-589c-11ec-afcd-2bd77acada1c"));
        statisticsCountRedisSvc.writeCount(deviceId1, dataPoint1);
    }


    @Test
    public void read() {

        LocalDateTime startDate = LocalDateTime.now().withHour(1).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDate = startDate.plusHours(23);
        List<LocalDateTime> localDateTimes = DateLocaDateAndTimeUtil.INSTANCE.getBetweenHour(startDate, endDate);
        localDateTimes.stream().forEach(t1 -> {
            Long count = statisticsCountRedisSvc.readCount(uuidList, t1);
            System.out.println(t1 + "===>" + count);
        });

    }
}
