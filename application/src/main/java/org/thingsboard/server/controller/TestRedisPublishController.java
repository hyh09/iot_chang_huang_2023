package org.thingsboard.server.controller;

import controller.test2.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test/redis")
public class TestRedisPublishController {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private Pub1 pub;

    @GetMapping("/publish1")
    @ResponseStatus(HttpStatus.OK)
    public void publish() {
        System.out.println("执行发布");
        redisTemplate.convertAndSend("channel1", "Hello, I'm Tom!");
    }

    @GetMapping("/publish2")
    @ResponseStatus(HttpStatus.OK)
    public void publish2() throws InterruptedException {
        System.out.println("执行发布");
        User u  = new User();
        u.setId("1");
        u.setName("wzg");
        pub.sendMessage("dddchannel", "我发消息了");
        //pub.sendMessage("cccchannel", u);
        Thread.sleep(100);//jackson 反向序列化慢
    }

    @GetMapping("/publish3")
    public void publish3(){
        @SuppressWarnings("rawtypes")
        SessionCallback<Object> sessionCallback = new SessionCallback() {

            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                int count = 0;
                while (count < 1000) {
                    operations.opsForValue().set("keyTest", "valueTest" + count);
                    count++;
                    log.error("count:{}", operations.opsForValue().get("keyTest"));
                }

                return null;
            }
        };
        redisTemplate.execute(sessionCallback);
    }

}