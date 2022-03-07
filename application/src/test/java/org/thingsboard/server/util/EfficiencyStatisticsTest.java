package org.thingsboard.server.util;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thingsboard.server.ThingsboardServerApplication;
import org.thingsboard.server.dao.sql.role.service.EfficiencyStatisticsSvc;

/**
 * @program: thingsboard
 * @description: 测试能耗历史
 * @author: HU.YUNHUI
 * @create: 2022-01-18 14:08
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ThingsboardServerApplication.class)
public class EfficiencyStatisticsTest  {

    @Autowired
    EfficiencyStatisticsSvc  efficiencyStatisticsSvc;







}
