package org.thingsboard.server.dao.mylock;

/**
 * @program: demo-all
 * @description:
 * @author: HU.YUNHUI
 * @create: 2022-04-18 11:40
 **/
public interface Lock {

    //获取锁
    public void getLock(String keyName);
    //釋放鎖
    public void unLock();
}
