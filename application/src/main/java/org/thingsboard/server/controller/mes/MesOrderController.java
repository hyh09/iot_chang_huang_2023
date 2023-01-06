package org.thingsboard.server.controller.mes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesOrderListDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesOrderProgressListDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesProductionCardListDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesProductionProgressListDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesOrderListVo;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesOrderProgressListVo;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesProductionCardListVo;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesProductionProgressListVo;
import org.thingsboard.server.dao.sqlserver.mes.service.MesOrderService;

@Slf4j
@Api(value="mes订单管理Controller",tags={"mes订单管理接口"})
@RestController
@RequestMapping("/api/mes/order")
public class MesOrderController extends BaseController {

    @Autowired
    private MesOrderService mesOrderService;

    @ApiOperation("查询订单列表")
    @RequestMapping(value = "/findOrderList",params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "其他条件",paramType = "query")
    })
    @ResponseBody
    public PageData<MesOrderListVo> findOrderList(@RequestParam int pageSize, @RequestParam int page, MesOrderListDto dto) {
        try {
            PageLink pageLink = createPageLink(pageSize, page,null,null,null);
            return mesOrderService.findOrderList(dto,pageLink);
        } catch (ThingsboardException e) {
            log.error("查询生产班组列表异常{}",e);
            throw new RuntimeException(e);
        }
    }

    @ApiOperation("查询订单进度列表")
    @RequestMapping(value = "/findOrderProgressList",params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "其他条件",paramType = "query")
    })
    @ResponseBody
    public PageData<MesOrderProgressListVo> findOrderProgressList(@RequestParam int pageSize, @RequestParam int page, MesOrderProgressListDto dto) {
        try {
            PageLink pageLink = createPageLink(pageSize, page,null,null,null);
            return mesOrderService.findOrderProgressList(dto,pageLink);
        } catch (ThingsboardException e) {
            log.error("查询订单进度列表异常{}",e);
            throw new RuntimeException(e);
        }
    }

    @ApiOperation("查询生产卡列表")
    @RequestMapping(value = "/findProductionCardList", params = {"pageSize", "page"},method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "其他条件",paramType = "query")
    })
    @ResponseBody
    public PageData<MesProductionCardListVo> findProductionCardList(@RequestParam int pageSize, @RequestParam int page,MesProductionCardListDto dto) {
        try {
            PageLink pageLink = createPageLink(pageSize, page,null,null,null);
            return mesOrderService.findProductionCardList(dto,pageLink);
        } catch (ThingsboardException e) {
            log.error("查询生产卡列表异常{}",e);
            throw new RuntimeException(e);
        }
    }


    @ApiOperation("查询生产进度列表")
    @RequestMapping(value = "/findProductionProgressList",params = {"pageSize", "page"}, method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "其他条件",paramType = "query")
    })
    @ResponseBody
    public PageData<MesProductionProgressListVo> findProductionProgressList(@RequestParam int pageSize, @RequestParam int page, MesProductionProgressListDto dto) {
        try {
            PageLink pageLink = createPageLink(pageSize, page,null,null,null);
            return mesOrderService.findProductionProgressList(dto,pageLink);
        } catch (ThingsboardException e) {
            log.error("查询生产进度列表异常{}",e);
            throw new RuntimeException(e);
        }
    }
}
