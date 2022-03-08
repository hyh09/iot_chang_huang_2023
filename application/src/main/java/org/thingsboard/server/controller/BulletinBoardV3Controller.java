package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.vo.TsSqlDayVo;
import org.thingsboard.server.dao.board.BulletinV3BoardVsSvc;
import org.thingsboard.server.dao.sql.role.entity.BoardV3DeviceDitEntity;
import org.thingsboard.server.queue.util.TbCoreComponent;

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

    @RequestMapping("/queryDeviceDictionary")
    public List<BoardV3DeviceDitEntity>  queryDeviceDictionary(@RequestParam(required = false ,value = "factoryId")  String factoryId,
                                                               @RequestParam(required = false ,value = "workshopId")  String workshopId,
                                                               @RequestParam(required = false ,value = "productionLineId")  String productionLineId,
                                                               @RequestParam(required = false ,value = "deviceId")  String deviceId)
    {
        TsSqlDayVo tsSqlDayVo =    TsSqlDayVo.constructionTsSqlDayVo(factoryId,workshopId,productionLineId,deviceId);
        return bulletinV3BoardVsSvc.queryDeviceDictionaryByEntityVo(tsSqlDayVo);
    }
}
