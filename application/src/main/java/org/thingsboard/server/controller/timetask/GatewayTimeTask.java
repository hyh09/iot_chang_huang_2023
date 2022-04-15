package org.thingsboard.server.controller.timetask;

import com.amazonaws.util.CollectionUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.kv.BaseAttributeKvEntry;
import org.thingsboard.server.common.data.kv.BooleanDataEntry;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.config.RedisMessagePublish;
import org.thingsboard.server.dao.device.DeviceCredentialsDao;
import org.thingsboard.server.dao.sql.attributes.JpaAttributeDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class GatewayTimeTask {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Value("${state.defaultGatewayTimeout}")
    @Getter
    private long defaultGatewayTimeout;

    @Autowired
    private DeviceCredentialsDao deviceCredentialsDao;

    @Autowired
    private RedisMessagePublish pub;

    @Autowired
    private JpaAttributeDao jpaAttributeDao;


    @Scheduled(cron = "0/10 * * * * ?")
    public void updateGatewayActive() {
        //pub.setWithExpire("fAtHNfi4kCRPkEr7V6aw",String.valueOf(System.currentTimeMillis()),1l, TimeUnit.HOURS);
        log.info("定时任务-定时更新网关状态执行了！");
        //获取所有网关令牌
        List<DeviceCredentials> deviceCredentialsList = deviceCredentialsDao.find(null);
        if (!CollectionUtils.isNullOrEmpty(deviceCredentialsList)) {
            for (DeviceCredentials deviceCredentials : deviceCredentialsList) {
                if (pub.hasKey(deviceCredentials.getCredentialsId())) {
                    //在线
                    //String result = (String)pub.get(deviceCredentials.getCredentialsId());
                    //long ts = Long.parseLong(result);
                    long ts = (long)pub.get(deviceCredentials.getCredentialsId());
                    log.info("定时更新网关在线，网关id=" + deviceCredentials.getDeviceId().getId() + ";网关令牌=" + deviceCredentials.getCredentialsId() + "；更新时间=" + sdf.format(new Date(ts)));
                    if (ts + (this.defaultGatewayTimeout * 1000) > System.currentTimeMillis()) {
                        jpaAttributeDao.save(null, deviceCredentials.getDeviceId(), DataConstants.SERVER_SCOPE, new BaseAttributeKvEntry(new BooleanDataEntry("active", true), ts));
                    }else {
                        log.info("定时更新网关离线（超时离线），网关id=" + deviceCredentials.getDeviceId().getId() + ";网关令牌=" + deviceCredentials.getCredentialsId() + "；更新时间=" + sdf.format(new Date(ts)));
                        jpaAttributeDao.save(null, deviceCredentials.getDeviceId(), DataConstants.SERVER_SCOPE, new BaseAttributeKvEntry(new BooleanDataEntry("active", false), System.currentTimeMillis()));
                    }
                } else {
                    //离线
                    //log.info("定时更新网关离线，网关id=" + deviceCredentials.getDeviceId().getId() + ";网关令牌=" + deviceCredentials.getCredentialsId());
                    jpaAttributeDao.save(null, deviceCredentials.getDeviceId(), DataConstants.SERVER_SCOPE, new BaseAttributeKvEntry(new BooleanDataEntry("active", false), System.currentTimeMillis()));
                }
            }
        }

    }


}
