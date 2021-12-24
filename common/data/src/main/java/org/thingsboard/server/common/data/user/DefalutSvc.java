package org.thingsboard.server.common.data.user;

/**
 * @program: thingsboard
 * @description: 默认常量配置接口
 * @author: HU.YUNHUI
 * @create: 2021-11-24 13:32
 **/
public interface DefalutSvc {

    //默认密码
     String DEFAULT_PASSWORD="123456";//rawPassword

    //遥测数据的时间差
    // 1800000=30分钟
    Long  ENERGY_TIME_GAP=1800000L;

}
