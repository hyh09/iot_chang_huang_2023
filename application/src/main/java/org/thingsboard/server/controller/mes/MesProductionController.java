package org.thingsboard.server.controller.mes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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
import org.thingsboard.server.excel.dto.ExportDto;
import org.thingsboard.server.utils.ExcelUtil;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/mes/production")
@Api(value="mes生产管理Controller",tags={"mes生产管理接口"})
public class MesProductionController extends BaseController {
    @Autowired
    private MesProductionService mesProductionService;
    @Autowired
    private ExcelUtil fileService;


    @ApiOperation("查询生产班组列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "其他条件",paramType = "query")
    })
    @RequestMapping(value = "/findPlanList", params = {"pageSize", "page"}, method = RequestMethod.GET)
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

    @ApiOperation("导出生产班组excel")
    @RequestMapping(value = "/exportPlan", method = RequestMethod.POST)
    @ApiImplicitParam(name = "path", value = "导出路径",paramType = "query")
    @ResponseBody
    public void downloadExcel(@RequestParam String path, HttpServletResponse response) {
        try {
            PageLink pageLink = createPageLink(Integer.MAX_VALUE, 0,null,null,null);
            PageData<MesProductionPlanVo> planList = mesProductionService.findPlanList(null, pageLink);
            ExportDto dto = new ExportDto();
            fileService.downloadExcel(this.getExportDtoFromPlan(planList.getData(),path), response);
        } catch (Exception e) {
            log.error("导出excel异常", e);
        }
    }

    @ApiOperation("查询生产报工列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "其他条件",paramType = "query")
    })    @RequestMapping(value = "/findWorkList", params = {"pageSize", "page"}, method = RequestMethod.GET)
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

    @ApiOperation("导出生产报工excel")
    @RequestMapping(value = "/exportWork", method = RequestMethod.POST)
    @ApiImplicitParam(name = "path", value = "导出路径",paramType = "query")
    @ResponseBody
    public void exportWork(@RequestParam String path, HttpServletResponse response) {
        try {
            PageLink pageLink = createPageLink(Integer.MAX_VALUE, 0,null,null,null);
            PageData<MesProductionWorkVo> workList = mesProductionService.findWorkList(null, pageLink);
            ExportDto dto = new ExportDto();
            fileService.downloadExcel(this.getExportDtoFromWork(workList.getData(),path), response);
        } catch (Exception e) {
            log.error("导出excel异常", e);
        }
    }


    @ApiOperation("查询生产监控列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "其他条件",paramType = "query")
    })    @RequestMapping(value = "/findMonitorList", params = {"pageSize", "page"}, method = RequestMethod.GET)
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

    @ApiOperation("导出生产监控excel")
    @RequestMapping(value = "/exportMonitor", method = RequestMethod.POST)
    @ApiImplicitParam(name = "path", value = "导出路径",paramType = "query")
    @ResponseBody
    public void exportMonitor(@RequestParam String path, HttpServletResponse response) {
        try {
            PageLink pageLink = createPageLink(Integer.MAX_VALUE, 0,null,null,null);
            PageData<MesProductionMonitorVo> monitorList = mesProductionService.findMonitorList(null, pageLink);
            fileService.downloadExcel(this.getExportDtoFromMonitor(monitorList.getData(),path), response);
        } catch (Exception e) {
            log.error("导出excel异常", e);
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


    /**
     * 处理生产排班要导出的数据
     * @param list
     * @param path
     * @return
     */
    private ExportDto getExportDtoFromPlan(List<MesProductionPlanVo> list,String path){
        List<List<String>> dataList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            dataList.add(
                    new ArrayList<>(Arrays.asList(new String[] {
                            "日期","班组","班组人员","工序","订单号","卡号","计划完成日期","计划数量","实际数量","超时（分）"
                    })));
            list.forEach(i->{
                dataList.add(
                        new ArrayList<>(Arrays.asList(new String[] {
                                i.getTTrackTime(),
                                i.getSWorkerGroupName(),
                                i.getSWorkerNameList(),
                                i.getSWorkingProcedureName(),
                                i.getSOrderNo(),
                                i.getSCardNo(),
                                i.getTPlanEndTime(),
                                i.getNPlanOutputQty(),
                                i.getNTrackQty(),
                                i.getTimeout()})));
            });
        }
        return new ExportDto(dataList,"生产班组",path);

    }
    /**
     * 处理生产报工要导出的数据
     * @param list
     * @param path
     * @return
     */
    private ExportDto getExportDtoFromWork(List<MesProductionWorkVo> list,String path){
        List<List<String>> dataList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            dataList.add(
                    new ArrayList<>(Arrays.asList(new String[] {
                            "订单号","卡号","色号","开始时间","结束时间","时长","生产产量","班组","生产机台"
                    })));
            list.forEach(i->{
                dataList.add(
                        new ArrayList<>(Arrays.asList(new String[] {
                                i.getSOrderNo(),
                                i.getSColorNo(),
                                i.getSColorNo(),
                                i.getTFactStartTime(),
                                i.getTFactEndTime(),
                                i.getFnMESGetDiffTimeStr(),
                                i.getNTrackQty(),
                                i.getSWorkerGroupName(),
                                i.getSEquipmentName()})));
            });
        }
        return new ExportDto(dataList,"生产报工",path);
    }
    /**
     * 处理生产监控要导出的数据
     * @param list
     * @param path
     * @return
     */
    private ExportDto getExportDtoFromMonitor(List<MesProductionMonitorVo> list,String path){
        List<List<String>> dataList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            dataList.add(
                    new ArrayList<>(Arrays.asList(new String[] {
                            "卡号","订单号","客户","交期","品名","颜色","卡数量","完工工序","待生产工序","呆滞时长(h)"
                    })));
            list.forEach(i->{
                dataList.add(
                        new ArrayList<>(Arrays.asList(new String[] {
                                i.getSCardNo(),
                                i.getSOrderNo(),
                                i.getSCustomerName(),
                                i.getDDeliveryDate(),
                                i.getSMaterialName(),
                                i.getSColorName(),
                                i.getNPlanOutputQty(),
                                i.getSWorkingProcedureNameFinish(),
                                i.getSWorkingProcedureName(),
                                i.getFnMESGetDiffTimeStr()
                        })));
            });
        }
        return new ExportDto(dataList,"生产监控",path);
    }

}
