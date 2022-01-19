package org.thingsboard.server.mqtt.controller;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("subRedis")
public class SubRedis implements MessageListener {

    //@Autowired
    private StringRedisSerializer stringRedisSerializer;

    //@Autowired
    private JdkSerializationRedisSerializer jdkSerializationRedisSerializer;

    @PostConstruct
    public void init(){
        stringRedisSerializer = new StringRedisSerializer();
        jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
    }



    /* (非 Javadoc)
     * Description:
     * @see org.springframework.data.redis.connection.MessageListener#onMessage(org.springframework.data.redis.connection.Message, byte[])
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println(message.toString());

        byte[] body = message.getBody();//请使用valueSerializer
        byte[] channel = message.getChannel();
        String msg = (String)stringRedisSerializer.deserialize(body);
        String topic = (String)stringRedisSerializer.deserialize(channel);
        System.out.println("我是sub2,监听"+topic+",我收到消息："+msg);
    }
}
