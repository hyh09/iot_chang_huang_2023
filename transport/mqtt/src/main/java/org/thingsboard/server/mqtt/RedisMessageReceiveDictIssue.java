package org.thingsboard.server.mqtt;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.util.JSONObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.transport.mqtt.MqttTransportHandler;
import org.thingsboard.server.transport.mqtt.MqttTransportService;

import java.text.ParseException;
import java.util.Map;

@Slf4j
@Component
public class RedisMessageReceiveDictIssue {
    private final String topic = "device/issue/";

    @Autowired
    private MqttTransportService mqttTransportService;

    public void getMessage(String object) throws ThingsboardException, ParseException {
        log.info("设备字典下发，Redis消息订阅成功！");
//序列化对象（特别注意：发布的时候需要设置序列化；订阅方也需要设置序列化）
        Jackson2JsonRedisSerializer seria = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        seria.setObjectMapper(objectMapper);

        String deserialize = seria.deserialize(object.getBytes()).toString();
        log.info("Redis订阅方收到消息：" + object);
        this.dictIssue(deserialize);
    }

    private void dictIssue(String deserialize) throws ThingsboardException, ParseException {
        MqttTransportHandler handler = mqttTransportService.getMqttTransportServerInitializer().getHandler();
        if(handler == null){
            log.error("handler 为空！");
            return;
        }
        if(StringUtils.isNotEmpty(deserialize)){
            Map<String, Object> parse = JSONObjectUtils.parse(deserialize);
            JSONArray topicObject = (JSONArray)parse.get("topic");
            String body = parse.get("body").toString();
            if(topicObject != null && StringUtils.isNotEmpty(topicObject.toString())){
               for (int i=0; i<topicObject.size(); i++){
                   String gatewayId = topicObject.get(i).toString();
                   log.info("发布MQTT消息");
                   log.info("消息主题：" + topic + gatewayId);
                   log.info("消息内容：" + body);
                   handler.dictIssue(topic + gatewayId,body);
               }
            }
        }
    }
}