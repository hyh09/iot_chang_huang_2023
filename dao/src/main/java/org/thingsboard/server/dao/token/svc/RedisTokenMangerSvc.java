package org.thingsboard.server.dao.token.svc;

import java.util.Map;
import java.util.UUID;

public interface RedisTokenMangerSvc {
    /**
     *
     * @param userId
     * @param token
     * @param message
     * @param time 如果为0,就是长久在线
     */
    public void save(UUID userId, String token, String message, long time);

    public  void delToken(UUID  userId);

    public   String  get(UUID userId,String token);






    public Map<Object, Object> hmget(UUID key);


    public boolean hmset(String key, Map<String, Object> map,long time);

}
