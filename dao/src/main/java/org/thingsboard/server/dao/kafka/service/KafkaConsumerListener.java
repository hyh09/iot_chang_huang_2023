package org.thingsboard.server.dao.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.dao.kafka.vo.DataBodayVo;
import org.thingsboard.server.dao.model.sqlts.ts.TsKvEntity;
import org.thingsboard.server.dao.mylock.Lock;
import org.thingsboard.server.dao.mylock.ZookeeperDistrbuteLock;
import org.thingsboard.server.dao.mylock.ZookeeperProperties;
import org.thingsboard.server.dao.sql.census.service.StatisticalDataService;
import org.thingsboard.server.dao.sql.trendChart.service.EnergyChartService;
import org.thingsboard.server.dao.sql.tskv.service.EnergyHistoryHourService;
import org.thingsboard.server.dao.sql.tskv.svc.EnergyHistoryMinuteSvc;
import org.thingsboard.server.dao.sqlts.BaseAbstractSqlTimeseriesDao;
import org.thingsboard.server.dao.sqlts.ts.TsKvRepository;
import org.thingsboard.server.dao.timeseries.TimeseriesLatestDao;
import org.thingsboard.server.dao.util.JsonUtils;
import org.thingsboard.server.dao.util.StringUtilToll;

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
    @Autowired  private ZookeeperProperties zookeeperProperties;

//    @KafkaListener(topics = {"hs_statistical_data_kafka"}, groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
    public void kafkaListener(String message) {
        if (StringUtils.isNotEmpty(message)) {
            Long startTime = System.currentTimeMillis();
//            log.info("打印mess:{}",message);
            Lock lock = new ZookeeperDistrbuteLock(zookeeperProperties);

            try {
                DataBodayVo dataBodayVo = JsonUtils.jsonToPojo(message, DataBodayVo.class);
                lock.getLock("statistical"+dataBodayVo.getEntityId());
                String title = dataBodayVo.getTitle();
                UUID entityId = dataBodayVo.getEntityId();
                if (StringUtils.isEmpty(dataBodayVo.getValue()) || StringUtilToll.isZero(dataBodayVo.getValue())) {
                    dataBodayVo.setValue(setPreviousByZero(dataBodayVo));
                }
                statisticalDataService.todayDataProcessing(entityId, dataBodayVo, title);
            }catch (Exception e){
                log.error("小时的异常:{}",e);
            }
            finally {
                lock.unLock();
            }


        }

    }





//    @KafkaListener(topics = {"hs_energy_hour_kafka"}, groupId = "group3", containerFactory = "kafkaListenerContainerFactory02")
    public void kafkaListenerhour(String message) {
        if (StringUtils.isNotEmpty(message)) {
            Long startTime = System.currentTimeMillis();
            Lock lock = new ZookeeperDistrbuteLock(zookeeperProperties);

            try {
               DataBodayVo dataBodayVo = JsonUtils.jsonToPojo(message, DataBodayVo.class);
               lock.getLock("hour" + dataBodayVo.getEntityId());
               String title = dataBodayVo.getTitle();
               UUID entityId = dataBodayVo.getEntityId();
               if (StringUtils.isEmpty(dataBodayVo.getValue()) || StringUtilToll.isZero(dataBodayVo.getValue())) {
                   dataBodayVo.setValue(setPreviousByZero(dataBodayVo));
               }
               energyHistoryHourService.saveByHour(entityId, dataBodayVo, title);
           }catch (Exception e)
            {
                log.error("小时的统计表的异常:{}",e);
            }
            finally {
               lock.unLock();
           }
        }

    }




    public String  setPreviousByZero(DataBodayVo dataBodayVo)
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


    }

}

