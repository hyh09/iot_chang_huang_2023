package org.thingsboard.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisMessagePublish {
    @Autowired
    @Resource(name="redisTemplateBiz")
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

    /**
     * @description string设置 key和 value的值
     * @author chengjunyu
     * @date 2022/2/11
     * @param key
     * @param value
     * @return void
     */
    public void set(String key, String value) {
        rt.opsForValue().set(key, value);
    }

    /**
     * @description string设置 key和 value的值并设置过期时间和时间单位
     * @author chengjunyu
     * @date 2022/2/12
     * @param key
     * @param value
     * @param seconds
     * @param timeUnit
     * @return void
     */
    public void setWithExpire(String key, String value, Long seconds, TimeUnit timeUnit) {
        rt.opsForValue().set(key, value, seconds, timeUnit);
    }

    /**
     * @description string获取 key对应的 value值
     * @author chengjunyu
     * @date 2022/2/11
     * @param  * @param key
     * @return java.lang.Object
     */
    public Object get(String key) {
        BoundValueOperations<String,String> boundValueOperations = rt.boundValueOps(key);
        return boundValueOperations.get();
        //return rt.opsForValue().get(key);
    }

    /**
     * @description 判断在 redis中是不是存在对应的 key值，有的话就返回 true，没有就返回 false
     * @author chengjunyu
     * @date 2022/2/11
     * @param  * @param key
     * @return boolean
     */
    public Boolean hasKey(String key) {
        return rt.hasKey(key);
    }

    /**
     * @description 删除redis中对应的key值
     * @author chengjunyu
     * @date 2022/2/11
     * @param key
     * @return boolean
     */
    public Boolean del(String key) {
        return rt.delete(key);
    }

    /**
     * @description 批量删除 redis中对应的 key值，其中 keys是数组 keys:Collection<K> keys
     * @author chengjunyu
     * @date 2022/2/11
     * @param keys
     * @return long
     */
    public Long batchDel(Collection<String> keys) {
        return rt.delete(keys);
    }

    /**
     * @description 把 key值序列化成 byte[]类型
     * @author chengjunyu
     * @date 2022/2/11
     * @param key
     * @return byte[]
     */
    public byte[] dump(String key) {
        return rt.dump(key);
    }

    /**
     * @description 对传入的 key值设置过期时间
     * @author chengjunyu
     * @date 2022/2/11
     * @param  * @param key
     * @param seconds
     * @return void
     */
    public Boolean expire(String key, long seconds) {
        return rt.expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * @description 对传入的 key值设置过期日期
     * @author chengjunyu
     * @date 2022/2/11
     * @param  * @param key
     * @param date
     * @return boolean
     */
    public Boolean expireAt(String key, Date date) {
        return rt.expireAt(key, date);
    }

    /**
     * @description 模糊查询，返回一个没有重复的 Set类型
     * @author chengjunyu
     * @date 2022/2/11
     * @param  * @param key
     * @return java.util.Set<java.lang.String>
     */
    public Set<String> getStringKeys(String key) {
        return rt.keys(key);
    }

    /**
     * @description 根据新的 key的名称修改 redis中老的 key的名称
     * @author chengjunyu
     * @date 2022/2/12
     * @param oldKey
     * @param newKey
     * @return void
     */
    public void rename(String oldKey, String newKey) {
        rt.rename(oldKey, newKey);
    }

    /**
     * @description 重命名旧的 key值
     * @author chengjunyu
     * @date 2022/2/12
     * @param oldKey
     * @param newKey
     * @return boolean
     */
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        return rt.renameIfAbsent(oldKey, newKey);
    }
}
