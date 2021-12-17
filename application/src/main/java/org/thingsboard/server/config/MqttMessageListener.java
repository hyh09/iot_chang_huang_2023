package org.thingsboard.server.config;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.mqtt.MqttHandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MqttMessageListener implements MqttHandler {
    private final BlockingQueue<MqttEvent> events;

    public MqttMessageListener() {
        events = new ArrayBlockingQueue<>(100);
    }

    @Override
    public void onMessage(String topic, ByteBuf message) {
        log.info("MQTT message [{}], topic [{}]", message.toString(StandardCharsets.UTF_8), topic);
        events.add(new MqttEvent(topic, message.toString(StandardCharsets.UTF_8)));
    }

    @Data
    private class MqttEvent {
        private final String topic;
        private final String message;
    }
}
