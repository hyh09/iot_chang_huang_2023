package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.vo.TsSqlDayVo;
import org.thingsboard.server.dao.board.BulletinV3BoardVsSvc;
import org.thingsboard.server.dao.sql.role.entity.BoardV3DeviceDitEntity;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: thingsboard
 * @description: 长胜3期需求
 * @author: HU.YUNHUI
 * @create: 2022-03-07 11:16
 **/
@Api(value = "看板3期", tags = {"看板的关于能耗和产能的相关接口"})
@Slf4j
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/board/v3/")
public class BulletinBoardV3Controller   extends BaseController {

    @Autowired
    private BulletinV3BoardVsSvc bulletinV3BoardVsSvc;

    /**
     * 查询设备字典
     * @param factoryId 工厂id
     * @param workshopId 车间id
     * @param productionLineId 产线id
     * @param deviceId 设备id
     * @return
     */
    @RequestMapping("/queryDeviceDictionary")
    public List<BoardV3DeviceDitEntity>  queryDeviceDictionary(@RequestParam(required = false ,value = "factoryId")  String factoryId,
                                                               @RequestParam(required = false ,value = "workshopId")  String workshopId,
                                                               @RequestParam(required = false ,value = "productionLineId")  String productionLineId,
                                                               @RequestParam(required = false ,value = "deviceId")  String deviceId)
    {

        try {
            TsSqlDayVo tsSqlDayVo =    TsSqlDayVo.constructionTsSqlDayVo(factoryId,workshopId,productionLineId,deviceId);
            tsSqlDayVo.setTenantId(getTenantId().getId());
            return bulletinV3BoardVsSvc.queryDeviceDictionaryByEntityVo(tsSqlDayVo);

        } catch (ThingsboardException e) {
            log.error("【看板3期】查询设备字典接口异常：{}",e);
            e.printStackTrace();
            return  new ArrayList<>();
        }
    }
}
