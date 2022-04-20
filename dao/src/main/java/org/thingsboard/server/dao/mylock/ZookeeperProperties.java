package org.thingsboard.server.dao.mylock;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2022-04-19 13:02
 **/
@Component
@Data
@ToString
public class ZookeeperProperties {

    @Value("${zk.url}")
    private String zkUrl;


    public String getZkUrl() {
//        System.out.println("zkUrl:"+zkUrl);
        return zkUrl;
    }

    public void setZkUrl(String zkUrl) {
        this.zkUrl = zkUrl;
    }



}
