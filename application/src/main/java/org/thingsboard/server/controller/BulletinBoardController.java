package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.enums.EfficiencyEnums;
import org.thingsboard.server.common.data.vo.home.ResultHomeCapAppVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.common.data.vo.tskv.MaxTsVo;
import org.thingsboard.server.dao.sql.role.service.BulletinBoardSvc;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 看板的控制层
 * @author: HU.YUNHUI
 * @create: 2021-12-07 17:19
 **/
@Api(value = "看板的关于能耗和产能的相关接口", tags = {"看板的关于能耗和产能的相关接口"})
@Slf4j
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/board/")
public class BulletinBoardController extends BaseController{

    @Autowired private BulletinBoardSvc bulletinBoardSvc;


    @ApiOperation(value = "【三个时期的总产量】")
    @RequestMapping(value = "/threePeriodsValue", method = RequestMethod.GET)
    @ResponseBody
    public  ResultHomeCapAppVo  threePeriodsValue(@RequestParam(required = false ,value = "factoryId")  String factoryId)
    {
        ResultHomeCapAppVo result = new ResultHomeCapAppVo();

        try {
            result.setTodayValue(getValueByTime(factoryId, CommonUtils.getZero(), CommonUtils.getNowTime()));
            result.setYesterdayValue(getValueByTime(factoryId, CommonUtils.getYesterdayZero(), CommonUtils.getYesterdayLastTime()));
            result.setHistory(bulletinBoardSvc.getHistoryCapValue(factoryId,getTenantId().getId()));
            return result;
        }catch (Exception e)
        {
            e.printStackTrace();

        }
        return  result;
    }


    private  String getValueByTime(String factoryId, long startTime, long EndTime) throws ThingsboardException {
        QueryTsKvVo queryTsKvVo = new QueryTsKvVo();
        queryTsKvVo.setTenantId(getTenantId().getId());
        if(StringUtils.isNotEmpty(factoryId)) {
            queryTsKvVo.setFactoryId(UUID.fromString(factoryId));
        }
        queryTsKvVo.setStartTime(startTime);
        queryTsKvVo.setEndTime(EndTime);
        ResultCapAppVo resultCapAppVo =   efficiencyStatisticsSvc.queryCapApp(queryTsKvVo,getTenantId());
        if(resultCapAppVo != null)
        {
            return  resultCapAppVo.getTotalValue();
        }
        return  "0";
    }



}
