package org.thingsboard.server.mqtt.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("subRedis")
public class SubRedis implements MessageListener {

    //设备字典下发主题
    private String topic = "device/issue/";

    private StringRedisSerializer stringRedisSerializer;

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
        System.out.println("redis订阅收到设备字典下发消息："+ message.toString());
        byte[] body = message.getBody();//请使用valueSerializer
        byte[] channel = message.getChannel();
        String msg = (String)stringRedisSerializer.deserialize(body);
        String topic = (String)stringRedisSerializer.deserialize(channel);
        String msgPattern = new String(pattern);
        System.out.println("我是sub2,监听"+topic+",我收到消息："+msg);
    }
}
