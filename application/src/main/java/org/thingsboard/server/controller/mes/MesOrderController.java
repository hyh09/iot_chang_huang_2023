package org.thingsboard.server.controller.mes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.hs.entity.vo.HistoryGraphPropertyTsKvVO;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.*;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.*;
import org.thingsboard.server.dao.sqlserver.mes.service.MesOrderService;
import org.thingsboard.server.excel.dto.ExportDto;
import org.thingsboard.server.utils.ExcelUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Api(value = "mes订单管理Controller", tags = {"mes订单管理接口"})
@RestController
@RequestMapping("/api/mes/order")
public class MesOrderController extends BaseController {

    @Autowired
    private MesOrderService mesOrderService;

    @Resource
    private ExcelUtil fileService;

    @ApiOperation("查询订单列表")
    @RequestMapping(value = "/findOrderList", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "其他条件", paramType = "query")
    })
    @ResponseBody
    public PageData<MesOrderListVo> findOrderList(@RequestParam int pageSize, @RequestParam int page, MesOrderListDto dto) {
        try {
            PageLink pageLink = createPageLink(pageSize, page, null, null, null);
            return mesOrderService.findOrderList(dto, pageLink);
        } catch (ThingsboardException e) {
            log.error("查询生产班组列表异常{}", e);
            throw new RuntimeException(e);
        }
    }

    @ApiOperation("查询订单进度列表")
    @RequestMapping(value = "/findOrderProgressList", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "其他条件", paramType = "query")
    })
    @ResponseBody
    public PageData<MesOrderProgressListVo> findOrderProgressList(@RequestParam int pageSize, @RequestParam int page, MesOrderProgressListDto dto) {
        try {
            PageLink pageLink = createPageLink(pageSize, page, null, null, null);
            dto.setDDeliveryDateBegin(this.getDateStr(dto.getDDeliveryDateBegin()));
            dto.setDDeliveryDateEnd(this.getDateStr(dto.getDDeliveryDateEnd()));
            return mesOrderService.findOrderProgressList(dto, pageLink);
        } catch (ThingsboardException e) {
            log.error("查询订单进度列表异常{}", e);
            throw new RuntimeException(e);
        }
    }

    @ApiOperation("查询生产卡列表")
    @RequestMapping(value = "/findProductionCardList", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "其他条件", paramType = "query")
    })
    @ResponseBody
    public PageData<MesProductionCardListVo> findProductionCardList(@RequestParam int pageSize, @RequestParam int page, MesProductionCardListDto dto) {
        try {
            PageLink pageLink = createPageLink(pageSize, page, null, null, null);
            dto.setDDeliveryDateBegin(this.getDateStr(dto.getDDeliveryDateBegin()));
            dto.setDDeliveryDateEnd(this.getDateStr(dto.getDDeliveryDateEnd()));
            return mesOrderService.findProductionCardList(dto, pageLink);
        } catch (ThingsboardException e) {
            log.error("查询生产卡列表异常{}", e);
            throw new RuntimeException(e);
        }
    }


    @ApiOperation("查询生产进度列表")
    @RequestMapping(value = "/findProductionProgressList", params = {"pageSize", "page"}, method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "其他条件", paramType = "query")
    })
    @ResponseBody
    public PageData<MesProductionProgressListVo> findProductionProgressList(@RequestParam int pageSize, @RequestParam int page, MesProductionProgressListDto dto) {
        try {
            PageLink pageLink = createPageLink(pageSize, page, null, null, null);
            return mesOrderService.findProductionProgressList(dto, pageLink);
        } catch (ThingsboardException e) {
            log.error("查询生产进度列表异常{}", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 时间戳字符串转日期字符串
     *
     * @param longString
     * @return
     */
    private String getDateStr(String longString) {
        if (StringUtils.isNotEmpty(longString)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(new Date(Long.parseLong(String.valueOf(longString))));
        }
        return "";
    }


    @ApiOperation("生产卡选择列表")
    @RequestMapping(value = "/findOrderCardList", params = {"pageSize", "page"}, method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "其他条件", paramType = "query")
    })
    @ResponseBody
    public PageData<MesOrderCardListVo> findOrderCardList(@RequestParam int pageSize, @RequestParam int page, MesOrderCardListDto dto) {
        try {
            PageLink pageLink = createPageLink(pageSize, page, null, null, null);
            dto.setDateBegin(this.getDateStr(dto.getDateBegin()));
            dto.setDateEnd(this.getDateStr(dto.getDateEnd()));
            return mesOrderService.findOrderCardList(dto, pageLink);
        } catch (ThingsboardException e) {
            log.error("生产卡选择列表异常{}", e);
            throw new RuntimeException(e);
        }
    }

    @ApiOperation("导出生产卡excel")
    @RequestMapping(value = "/exportCard", method = RequestMethod.POST)
    @ApiImplicitParam(name = "path", value = "导出路径", paramType = "query")
    @ResponseBody
    public void downloadExcel(@RequestParam String path, MesOrderCardListDto dto, HttpServletResponse response) {
        try {
            PageLink pageLink = createPageLink(Integer.MAX_VALUE, 0, null, null, null);
            PageData<MesOrderCardListVo> planList = mesOrderService.findOrderCardList(dto, pageLink);
            fileService.downloadExcel(this.getExportDtoFromWork(planList.getData(), path), response);
        } catch (Exception e) {
            log.error("导出excel异常", e);
        }
    }

    @ApiOperation("工序选择")
    @PostMapping(value = "/findProductedList")
    @ResponseBody
    public List<MesProductedVo> findProductedList(String cardNo) {
        return mesOrderService.findProductedList(cardNo);
    }

    @ApiOperation("参数趋势图")
    @PostMapping(value = "/getChart")
    @ResponseBody
    public List<MesChartVo> getChart(@Validated MesChartDto dto) throws ThingsboardException {
        if (StringUtils.isEmpty(dto.getUemEquipmentGUID())) {
            throw new RuntimeException("设备id不能为空");
        }
        dto.setTenantId(getTenantId());
        return mesOrderService.getChart(dto);
    }

    @ApiOperation("参数折线图")
    @PostMapping(value = "/getParamChart")
    @ResponseBody
    public List<HistoryGraphPropertyTsKvVO> getParamChart(@Validated MesChartDto dto) throws ThingsboardException {
        if (dto.getDeviceId() == null) {
            throw new RuntimeException("设备id不能为空");
        }
        if (StringUtils.isEmpty(dto.getKey())) {
            throw new RuntimeException("参数不能为空");
        }
        dto.setTenantId(getTenantId());
        return mesOrderService.getParamChart(dto);
    }


    /**
     * 处理生产卡要导出的数据内容
     *
     * @param list
     * @param path
     * @return
     */
    private ExportDto getExportDtoFromWork(List<MesOrderCardListVo> list, String path) {
        List<List<String>> dataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            dataList.add(
                    new ArrayList<>(Arrays.asList(new String[]{
                            "生产卡号", "布种", "色名", "所属订单"
                    })));
            list.forEach(i -> {
                dataList.add(
                        new ArrayList<>(Arrays.asList(new String[]{
                                i.getSCardNo(),
                                i.getSMaterialName(),
                                i.getSColorName(),
                                i.getSOrderNo()})));
            });
        }
        return new ExportDto(dataList, "生产卡", path);
    }
}
