package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.hs.service.OrderRtService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.queue.util.TbCoreComponent;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.thingsboard.server.dao.service.Validator.validatePageLink;


/**
 * 订单接口
 *
 * @author wwj
 * @since 2021.10.18
 */
@Api(value = "订单接口", tags = {"订单接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class OrderController extends BaseController {

    @Autowired
    OrderRtService orderService;

    @Autowired
    ClientService clientService;

    /**
     * Pc-订单-导入
     */
    @ApiOperation(value = "Pc-订单-导入")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "checksum", value = "校验和", paramType = "query"),
            @ApiImplicitParam(name = "checksumAlgorithmStr", value = "校验和算法", paramType = "query"),
            @ApiImplicitParam(name = "file", value = "文件", paramType = "form", dataType = "file", required = true),
    })
    @PostMapping(value = "/order/import")
    public void orderImport(@RequestParam(required = false) String checksum,
                            @RequestParam(required = false, defaultValue = "MD5") String checksumAlgorithmStr,
                            @RequestBody MultipartFile file) throws ThingsboardException, IOException {
        if (file == null || file.isEmpty())
            throw new ThingsboardException("文件不能为空！", ThingsboardErrorCode.GENERAL);

        ChecksumAlgorithm checksumAlgorithm = ChecksumAlgorithm.valueOf(checksumAlgorithmStr.toUpperCase());
        this.orderService.saveOrdersFromFile(getTenantId(), getCurrentUser().getId(), checksum, checksumAlgorithm, file);
        saveAuditLog(getCurrentUser(), null, EntityType.ORDER, null, ActionType.ORDER_IMPORT, "");
    }

    /**
     * Pc-订单-模板下载
     */
    @ApiOperation(value = "Pc-订单-模板下载")
    @GetMapping("/order/template")
    public void downloadTemplate(HttpServletResponse response) throws ThingsboardException, IOException {
        var fileName = URLEncoder.encode("订单批量导入模板.xlsx", StandardCharsets.UTF_8.name());
        response.addHeader("Content-disposition", "attachment;filename=" + fileName + ";filename*=UTF-8" + fileName);
        response.addHeader("Access-Control-Expose-Headers", "*");
        this.orderService.createTemplate().write(response.getOutputStream());
        response.flushBuffer();
    }

    /**
     * Pc-订单-可用编码
     *
     * @return 订单编码
     */
    @ApiOperation(value = "Pc-订单-可用编码")
    @GetMapping("/order/availableCode")
    public String getAvailableCode() throws ThingsboardException {
        return this.orderService.getAvailableCode(getTenantId());
    }

    /**
     * Pc-订单-列表查询
     */
    @ApiOperation(value = "Pc-订单-列表查询", notes = "工厂Id不传，按登陆用户所属全部工厂查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query", defaultValue = "createdTime"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query", defaultValue = "desc"),
            @ApiImplicitParam(name = "orderNo", value = "订单号", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "订单类型", paramType = "query"),
            @ApiImplicitParam(name = "factoryName", value = "工厂名称", paramType = "query"),
    })
    @GetMapping("/orders")
    public PageData<OrderListResult> listOrders(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false, defaultValue = "createdTime") String sortProperty,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String factoryName
    )
            throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
        validatePageLink(pageLink);
        OrderListQuery query = OrderListQuery.builder().orderNo(orderNo).factoryName(factoryName).type(type).build();
        return this.orderService.listPageOrdersByQuery(getTenantId(), getCurrentUser().getId(), query, pageLink);
    }

    /**
     * Pc-订单-详情
     */
    @ApiOperation(value = "Pc-订单-详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单Id", paramType = "path", required = true)
    })
    @GetMapping("/order/{id}")
    public OrderVO getOrderDetail(@PathVariable("id") String orderId) throws ThingsboardException {
        checkParameter("orderId", orderId);
        return this.orderService.getOrderDetail(getTenantId(), toUUID(orderId));
    }

    /**
     * Pc-订单-更新或新增
     */
    @ApiOperation(value = "Pc-订单-更新或新增")
    @PostMapping("/order")
    public OrderVO updateOrSaveOrder(@RequestBody @Valid OrderVO orderVO) throws ThingsboardException {
        var tempId = orderVO.getId();
        var oldOrderVO = this.orderService.updateOrSaveOrder(getTenantId(), orderVO);
        var newOrderVO =  this.orderService.getOrderDetail(getTenantId(), toUUID(oldOrderVO.getId()));
        if (StringUtils.isBlank(tempId))
            saveAuditLog(getCurrentUser(), null, EntityType.ORDER, orderVO.getOrderNo(), ActionType.ADDED, orderVO);
        else
            saveAuditLog(getCurrentUser(), toUUID(tempId), EntityType.ORDER, orderVO.getOrderNo(), ActionType.UPDATED, orderVO);
        return newOrderVO;
    }

    /**
     * Pc-订单-删除
     */
    @ApiOperation(value = "Pc-订单-删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单Id", paramType = "path", required = true)
    })
    @DeleteMapping("/order/{id}")
    public void deleteOrder(@PathVariable("id") String orderId) throws ThingsboardException {
        checkParameter("orderId", orderId);
        var data = this.orderService.getOrderDetail(getTenantId(), toUUID(orderId));
        this.orderService.deleteOrder(getTenantId(), toUUID(orderId));
        saveAuditLog(getCurrentUser(), toUUID(orderId), EntityType.ORDER, null, ActionType.DELETED, data);
    }

    /**
     * Pc-订单-报工完成
     */
    @ApiOperation(value = "Pc-订单-报工完成")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单Id", paramType = "path", required = true)
    })
    @PostMapping("/order/{id}/done")
    public void updateOrderDone(@PathVariable("id") String orderId) throws ThingsboardException {
        checkParameter("orderId", orderId);
        this.orderService.updateOrderDone(getTenantId(), toUUID(orderId));
        saveAuditLog(getCurrentUser(), toUUID(orderId), EntityType.ORDER, null, ActionType.ORDER_DONE, orderId);
    }

    /**
     * Pc-订单产能监控-列表查询
     */
    @ApiOperation(value = "Pc-订单产能监控-列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query", defaultValue = "createdTime"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query", defaultValue = "desc"),
            @ApiImplicitParam(name = "orderNo", value = "订单号", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "订单类型", paramType = "query"),
            @ApiImplicitParam(name = "factoryName", value = "工厂名称", paramType = "query"),
    })
    @GetMapping("/order/capacityMonitor")
    public PageData<OrderListResult> listOrderCapacityMonitor(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false, defaultValue = "createdTime") String sortProperty,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String factoryName
    )
            throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
        validatePageLink(pageLink);
        OrderListQuery query = OrderListQuery.builder().orderNo(orderNo).factoryName(factoryName).type(type).build();
        return this.orderService.listPageOrderCapacityMonitorByQuery(getTenantId(), getCurrentUser().getId(), query, pageLink);
    }

    /**
     * Pc-订单产能监控-详情
     */
    @ApiOperation(value = "Pc-订单产能监控-详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单Id", paramType = "path", required = true)
    })
    @GetMapping("/order/{id}/capacityMonitor")
    public OrderVO getOrderCapacityMonitorDetail(@PathVariable("id") String orderId) throws ThingsboardException {
        checkParameter("orderId", orderId);
        return this.orderService.getOrderCapacityMonitorDetail(getTenantId(), toUUID(orderId));
    }

    /**
     * 看板-订单产能监控(含集团、工厂、车间)
     */
    @ApiOperation(value = "看板-订单产能监控(含集团、工厂、车间)", notes = "默认当天；工厂Id和车间Id均不传，则显示全部工厂")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query"),
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query")
    })
    @GetMapping("/order/board/capacityMonitor")
    public List<OrderCustomCapacityResult> listBoardCapacityMonitorOrders(
            @RequestParam("startTime") Long startTime,
            @RequestParam("endTime") Long endTime,
            @RequestParam(value = "factoryId", required = false) UUID factoryId,
            @RequestParam(value = "workshopId", required = false) UUID workshopId
    ) throws ThingsboardException {
        if (startTime == null || startTime == 0)
            startTime = CommonUtil.getTodayStartTime();
        if (endTime == null || endTime == 0)
            endTime = CommonUtil.getTodayCurrentTime();
        return this.orderService.listBoardCapacityMonitorOrders(getTenantId(), factoryId, workshopId, TimeQuery.builder().startTime(startTime).endTime(endTime).build());
    }

    /**
     * App-首页-订单统计
     */
    @ApiOperation(value = "App-首页-订单管理", notes = "不传工厂Id，则当前登录用户所属工厂下全部订单；默认当月")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", defaultValue = "当月第一天零点"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", defaultValue = "当前时间")
    })
    @GetMapping("/order/app/index/capacityMonitor")
    public OrderAppIndexCapacityResult getAppIndexOrderCapacityResult(
            @RequestParam(value = "factoryId", required = false) String factoryId,
            @RequestParam(value = "startTime", required = false) Long startTime,
            @RequestParam(value = "endTime", required = false) Long endTime
    ) throws ThingsboardException {
        if (startTime == null || startTime <= 0)
            startTime = CommonUtil.getThisMonthStartTime();
        if (endTime == null || endTime <= 0)
            endTime = CommonUtil.getTodayCurrentTime();
        var tenantId = getTenantId();
        var userId = getCurrentUser().getId();
        var factoryIds = Optional.ofNullable(factoryId).filter(StringUtils::isNotBlank).map(this::toUUID).map(List::of)
                .orElseGet(() -> this.clientService.listFactoriesByUserId(tenantId, userId).stream().map(Factory::getId).collect(Collectors.toList()));
        return this.orderService.getAppIndexOrderCapacityResult(tenantId, factoryIds, TimeQuery.builder().startTime(startTime).endTime(endTime).build());
    }

    /**
     * App-订单-订单监控
     */
    @ApiOperation(value = "App-订单-订单监控", notes = "不传工厂Id，则当前登录用户所属工厂下全部订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query", defaultValue = "createdTime"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query", defaultValue = "desc"),
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query"),
            @ApiImplicitParam(name = "orderNo", value = "订单编号", paramType = "query")
    })
    @GetMapping("/order/app/capacityMonitor")
    public PageData<OrderListResult> getAppIndexOrderCapacityResult(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false, defaultValue = "createdTime") String sortProperty,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(value = "factoryId", required = false) String factoryId,
            @RequestParam(value = "orderNo", required = false) String orderNo
    ) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
        validatePageLink(pageLink);
        return this.orderService.listPageAppOrderCapacityMonitorByQuery(getTenantId(), getCurrentUser().getId(), OrderListQuery.builder().orderNo(orderNo).factoryId(CommonUtil.toUUIDNullable(factoryId)).build(), pageLink);
    }

    /**
     * App-订单-订单监控-生产计划
     */
    @ApiOperation(value = "App-订单-订单监控-生产计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单Id", paramType = "path", required = true),
    })
    @GetMapping("/order/app/capacityMonitor/{orderId}/plan")
    public OrderVO getAppOrderPlan(
            @PathVariable("orderId") String orderId
    ) throws ThingsboardException {
        checkParameter("orderId", orderId);
        return this.orderService.getOrderCapacityMonitorDetail(getTenantId(), toUUID(orderId));
    }

    /**
     * App-订单-订单监控-生产计划-更新实际时间
     */
    @Deprecated
    @ApiOperation(value = "App-订单-订单监控-生产计划-更新实际时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "planId", value = "设备计划Id", paramType = "path", required = true),
    })
    @PutMapping("/order/app/capacityMonitor/plan/device/{planId}/actualTime")
    public void updateOrderPlanDeviceActualTime(@PathVariable("planId") String planId, @RequestBody @Valid OrderPlanDeviceActualTimeVO timeVO) throws ThingsboardException {
        checkParameter("planId", planId);
        this.orderService.updateOrderPlanDeviceActualTime(getTenantId(), toUUID(planId), timeVO);
    }
}
