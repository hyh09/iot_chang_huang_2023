package org.thingsboard.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class RedisMessagePublish {
    @Autowired
    //@Resource(name="redisTemplate1")
    private RedisTemplate<String, String> rt;

    private GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer;

    private JdkSerializationRedisSerializer jdkSerializationRedisSerializer;

    @PostConstruct
    public void init(){
        jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
    }

    public void sendMessage(String channel, String message) {
        rt.convertAndSend(channel, message);
    }

    public void sendMessage(String channel,Object message) {
        byte[] msg =jackson2JsonRedisSerializer.serialize(message);
        rt.convertAndSend(channel,new String(msg));
    }



}
