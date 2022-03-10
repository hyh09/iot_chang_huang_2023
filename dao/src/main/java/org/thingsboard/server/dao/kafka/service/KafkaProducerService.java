package org.thingsboard.server.dao.kafka.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2022-03-10 16:37
 **/
@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    private static String default_topic="hs_statistical_data_kafka";

    /**
     * producer 同步方式发送数据
     *
     * @param topic   topic名称
     * @param message producer发送的数据
     */
    public void sendMessageSync(String topic, String message) throws InterruptedException, ExecutionException, TimeoutException {
        if(StringUtils.isEmpty(topic))
        {
            topic =default_topic;
        }
        kafkaTemplate.send(topic, message).get(30, TimeUnit.SECONDS);
    }

    /**
     * producer 异步方式发送数据
     *
     * @param topic   topic名称
     * @param message producer发送的数据
     */
    public void sendMessageAsync(String topic, String message) {
        if(StringUtils.isEmpty(topic))
        {
            topic =default_topic;
        }
        kafkaTemplate.send(topic, message).addCallback(new ListenableFutureCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("success");
            }

            @Override
            public void onSuccess(Object o) {
                System.out.println("failure");

            }
        });
    }

}

