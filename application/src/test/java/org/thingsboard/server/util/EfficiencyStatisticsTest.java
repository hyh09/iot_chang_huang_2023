package org.thingsboard.server.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thingsboard.server.ThingsboardServerApplication;
import org.thingsboard.server.common.data.vo.DeviceCapacityVo;
import org.thingsboard.server.dao.sql.role.service.BulletinBoardSvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private BulletinBoardSvc bulletinBoardSvc;


    ///    {
    //        "id":"c5ec6fd0-5d7b-11ec-98bf-8d3d37c1f97a",
    //        "entityId":"c5ec6fd0-5d7b-11ec-98bf-8d3d37c1f97a",
    //        "startTime":1647410400000,
    //        "endTime":1647417600000
    //    }
    @Test
    public void   getTest()
    {
        DeviceCapacityVo vo = new DeviceCapacityVo();
        vo.setEntityId(UUID.fromString("c5ec6fd0-5d7b-11ec-98bf-8d3d37c1f97a"));
        vo.setId(UUID.fromString("c5ec6fd0-5d7b-11ec-98bf-8d3d37c1f97a"));
        vo.setStartTime(1647410400000L);
        vo.setEndTime(1647417600000L);
        List<DeviceCapacityVo> deviceCapacityVoList = new ArrayList<>();
        deviceCapacityVoList.add(vo);
        Map<UUID,String>   map =  bulletinBoardSvc.queryCapacityValueByDeviceIdAndTime(deviceCapacityVoList);
        System.out.println("打印结果:"+map);
    }






}
