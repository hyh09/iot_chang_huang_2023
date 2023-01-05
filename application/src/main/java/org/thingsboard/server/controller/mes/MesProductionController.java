package org.thingsboard.server.controller.mes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesProductionMonitorDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesProductionPlanDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesProductionWorkDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesProductionMonitorVo;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesProductionPlanVo;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesProductionWorkVo;
import org.thingsboard.server.dao.sqlserver.mes.service.MesProductionService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/mes/production")
@Api(value="mes生产管理Controller",tags={"mes生产管理接口"})
public class MesProductionController extends BaseController {
    @Autowired
    private MesProductionService mesProductionService;


    @ApiOperation("查询生产班组列表")
    @ApiImplicitParam(name = "MesProductionPlanDto", value = "入参实体", dataType = "MesProductionPlanDto", paramType = "dto")
    @RequestMapping(value = "/findPlanList", method = RequestMethod.GET)
    @ResponseBody
    public PageData<MesProductionPlanVo> findPlanList(@RequestParam int pageSize, @RequestParam int page, MesProductionPlanDto dto) {
        try {
            PageLink pageLink = createPageLink(pageSize, page,null,null,null);
            dto.setTTrackTimeStart(this.getDateStr(dto.getTTrackTimeStart()));
            dto.setTTrackTimeEnd(this.getDateStr(dto.getTTrackTimeEnd()));
            return mesProductionService.findPlanList(dto,pageLink);
        } catch (ThingsboardException e) {
            log.error("查询生产班组列表异常{}",e);
            throw new RuntimeException(e);
        }
    }

    @ApiOperation("查询生产报工列表")
    @ApiImplicitParam(name = "MesProductionWorkDto", value = "入参实体", dataType = "MesProductionWorkDto", paramType = "dto")
    @RequestMapping(value = "/findWorkList", method = RequestMethod.GET)
    @ResponseBody
    public PageData<MesProductionWorkVo> findWorkList(@RequestParam int pageSize, @RequestParam int page, MesProductionWorkDto dto) {
        try {
            PageLink pageLink = createPageLink(pageSize, page,null,null,null);
            dto.setTFactEndTime(this.getDateStr(dto.getTFactEndTime()));
            dto.setTFactStartTime(this.getDateStr(dto.getTFactStartTime()));
            return mesProductionService.findWorkList(dto,pageLink);
        } catch (ThingsboardException e) {
            log.error("查询生产报工列表异常{}",e);
            throw new RuntimeException(e);
        }
    }


    @ApiOperation("查询生产监控列表")
    @ApiImplicitParam(name = "MesProductionMonitorDto", value = "入参实体", dataType = "MesProductionMonitorDto", paramType = "dto")
    @RequestMapping(value = "/findMonitorList", method = RequestMethod.GET)
    @ResponseBody
    public PageData<MesProductionMonitorVo> findMonitorList(@RequestParam int pageSize, @RequestParam int page, MesProductionMonitorDto dto) {
        try {
            PageLink pageLink = createPageLink(pageSize, page,null,null,null);
            return mesProductionService.findMonitorList(dto,pageLink);
        } catch (ThingsboardException e) {
            log.error("查询生产监控列表异常{}",e);
            throw new RuntimeException(e);
        }
    }

    @ApiOperation("查询生产工序名称列表")
    @RequestMapping(value = "/findWorkingProcedureNameList", method = RequestMethod.GET)
    @ResponseBody
    public List<String> findWorkingProcedureNameList(){
        try {
            return mesProductionService.findWorkingProcedureNameList();
        } catch (Exception e) {
            log.error("查询生产工序名称列表异常{}",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 时间戳字符串转日期字符串
     * @param longString
     * @return
     */
    private String getDateStr(String longString){
        if (!longString.isBlank()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(new Date(Long.parseLong(String.valueOf(longString))));
        }
        return "";
    }

}
