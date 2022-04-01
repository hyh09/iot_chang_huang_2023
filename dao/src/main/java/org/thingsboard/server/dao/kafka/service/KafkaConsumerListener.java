package org.thingsboard.server.dao.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.dao.kafka.vo.DataBodayVo;
import org.thingsboard.server.dao.model.sqlts.ts.TsKvEntity;
import org.thingsboard.server.dao.sql.census.service.StatisticalDataService;
import org.thingsboard.server.dao.sql.trendChart.service.EnergyChartService;
import org.thingsboard.server.dao.sql.tskv.service.EnergyHistoryHourService;
import org.thingsboard.server.dao.sql.tskv.svc.EnergyHistoryMinuteSvc;
import org.thingsboard.server.dao.sqlts.BaseAbstractSqlTimeseriesDao;
import org.thingsboard.server.dao.sqlts.ts.TsKvRepository;
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
public class KafkaConsumerListener extends BaseAbstractSqlTimeseriesDao {

    @Autowired
    private StatisticalDataService statisticalDataService;
    @Autowired
    private EnergyChartService energyChartService;
    @Autowired
    private EnergyHistoryMinuteSvc energyHistoryMinuteSvc;
    @Autowired
    private TimeseriesLatestDao timeseriesLatestDao;
    @Autowired
    private EnergyHistoryHourService energyHistoryHourService;
    @Autowired  private TsKvRepository tsKvRepository;

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
//            energyChartService.todayDataProcessing(entityId, dataBodayVo, title);
//            energyHistoryHourService.saveByHour(entityId,dataBodayVo,title);

//            Long endTime = System.currentTimeMillis();
//            Long tempTime = (endTime - startTime);
//            log.info("消费端的花费时间："+
//                    (((tempTime/86400000)>0)?((tempTime/86400000)+"d"):"")+
//                    ((((tempTime/86400000)>0)||((tempTime%86400000/3600000)>0))?((tempTime%86400000/3600000)+"h"):(""))+
//                    ((((tempTime/3600000)>0)||((tempTime%3600000/60000)>0))?((tempTime%3600000/60000)+"m"):(""))+
//                    ((((tempTime/60000)>0)||((tempTime%60000/1000)>0))?((tempTime%60000/1000)+"s"):(""))+
//                    ((tempTime%1000)+"ms"));

        }

    }


    @KafkaListener(topics = {"hs_energy_chart_kafka"}, groupId = "group2", containerFactory = "kafkaListenerContainerFactory01")
    public void kafkaListenerChart(String message) {
        if (StringUtils.isNotEmpty(message)) {
            DataBodayVo dataBodayVo = JsonUtils.jsonToPojo(message, DataBodayVo.class);
            String title = dataBodayVo.getTitle();
            UUID entityId = dataBodayVo.getEntityId();
            if (StringUtils.isEmpty(dataBodayVo.getValue()) || dataBodayVo.getValue().equals("0")) {
                dataBodayVo.setValue(setPreviousByZero(dataBodayVo));
            }

            energyChartService.todayDataProcessing(entityId, dataBodayVo, title);


        }

    }


    @KafkaListener(topics = {"hs_energy_hour_kafka"}, groupId = "group3", containerFactory = "kafkaListenerContainerFactory02")
    public void kafkaListenerhour(String message) {
        if (StringUtils.isNotEmpty(message)) {
            Long startTime = System.currentTimeMillis();

            DataBodayVo dataBodayVo = JsonUtils.jsonToPojo(message, DataBodayVo.class);
            String title = dataBodayVo.getTitle();
            UUID entityId = dataBodayVo.getEntityId();
            if (StringUtils.isEmpty(dataBodayVo.getValue()) || dataBodayVo.getValue().equals("0")) {
                dataBodayVo.setValue(setPreviousByZero(dataBodayVo));
            }

            energyHistoryHourService.saveByHour(entityId,dataBodayVo,title);



        }

    }




    private String  setPreviousByZero(DataBodayVo dataBodayVo)
    {
       UUID  entityId=dataBodayVo.getEntityId();
       long time=dataBodayVo.getTs();
        Integer  key=   getOrSaveKeyId(dataBodayVo.getKey());
        Long longTime =    tsKvRepository.findAllMaxTime(entityId,key,time);
        if(longTime  == null )
        {
            return "0";
        }
        TsKvEntity tsKvEntity =    tsKvRepository.findAllByTsAndEntityIdAndKey(entityId,key,longTime);
        TsKvEntry  entry=   tsKvEntity.toData();
        Object o =entry.getValue();
        if(o == null)
        {
            return  "0";
        }
        return  o.toString();


//        TenantId tenantId = new TenantId(dataBodayVo.getTenantId());
//        DeviceId deviceId = new DeviceId(dataBodayVo.getEntityId());
//        ListenableFuture<TsKvEntry> tsKvEntryListenableFuture = timeseriesLatestDao.findLatest(tenantId, deviceId, dataBodayVo.getKey());
//        try {
//            TsKvEntry tsKvEntry1 = tsKvEntryListenableFuture.get();
//            return  (tsKvEntry1.getValue() != null ? tsKvEntry1.getValue().toString() : dataBodayVo.getValue());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return dataBodayVo.getValue();
    }

}

