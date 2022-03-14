package org.thingsboard.server.dao.kafka.service;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.dao.kafka.vo.DataBodayVo;
import org.thingsboard.server.dao.sql.census.service.StatisticalDataService;
import org.thingsboard.server.dao.sql.trendChart.service.EnergyChartService;
import org.thingsboard.server.dao.sql.tskv.svc.EnergyHistoryMinuteSvc;
import org.thingsboard.server.dao.timeseries.TimeseriesLatestDao;
import org.thingsboard.server.dao.util.JsonUtils;

import java.util.UUID;

/**
 * @program: KafkaProducerConsumer
 * @description:
 * @author: HU.YUNHUI
 * @create: 2022-03-10 16:25
 **/
@Slf4j
@Component
public class KafkaConsumerListener {

    @Autowired
    private StatisticalDataService statisticalDataService;
    @Autowired
    private EnergyChartService energyChartService;
    @Autowired
    private EnergyHistoryMinuteSvc energyHistoryMinuteSvc;
    @Autowired
    private TimeseriesLatestDao timeseriesLatestDao;

    @KafkaListener(topics = {"hs_statistical_data_kafka"}, groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
    public void kafkaListener(String message) {
        if (StringUtils.isNotEmpty(message)) {
            Long startTime = System.currentTimeMillis();
//            log.info("打印mess:{}",message);

            DataBodayVo dataBodayVo = JsonUtils.jsonToPojo(message, DataBodayVo.class);
            String title = dataBodayVo.getTitle();
            UUID entityId = dataBodayVo.getEntityId();
            if (StringUtils.isEmpty(dataBodayVo.getValue()) || dataBodayVo.getValue().equals("0")) {
                dataBodayVo.setValue(setPreviousByZero(dataBodayVo));
            }
            statisticalDataService.todayDataProcessing(entityId, dataBodayVo, title);
//            energyHistoryMinuteSvc.saveByMinute( entityId,dataBodayVo,title);
            energyChartService.todayDataProcessing(entityId, dataBodayVo, title);

            Long endTime = System.currentTimeMillis();
            Long tempTime = (endTime - startTime);
//            log.info("消费端的花费时间："+
//                    (((tempTime/86400000)>0)?((tempTime/86400000)+"d"):"")+
//                    ((((tempTime/86400000)>0)||((tempTime%86400000/3600000)>0))?((tempTime%86400000/3600000)+"h"):(""))+
//                    ((((tempTime/3600000)>0)||((tempTime%3600000/60000)>0))?((tempTime%3600000/60000)+"m"):(""))+
//                    ((((tempTime/60000)>0)||((tempTime%60000/1000)>0))?((tempTime%60000/1000)+"s"):(""))+
//                    ((tempTime%1000)+"ms"));

        }

    }

    private String  setPreviousByZero(DataBodayVo dataBodayVo)
    {
        TenantId tenantId = new TenantId(dataBodayVo.getTenantId());
        DeviceId deviceId = new DeviceId(dataBodayVo.getEntityId());
        ListenableFuture<TsKvEntry> tsKvEntryListenableFuture = timeseriesLatestDao.findLatest(tenantId, deviceId, dataBodayVo.getKey());
        try {
            TsKvEntry tsKvEntry1 = tsKvEntryListenableFuture.get();
            return  (tsKvEntry1.getValue() != null ? tsKvEntry1.getValue().toString() : dataBodayVo.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataBodayVo.getValue();
    }

}

