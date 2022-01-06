package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.service.OrderService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.queue.util.TbCoreComponent;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

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
    OrderService orderService;

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
        this.orderService.saveOrdersFromFile(getTenantId(), checksum, checksumAlgorithm, file);
    }

    /**
     * Pc-订单-模板下载
     */
    @ApiOperation(value = "Pc-订单-模板下载")
    @GetMapping("/order/template")
    public ResponseEntity<Resource> downloadTemplate() throws ThingsboardException, IOException {
//        var fileInfo = this.fileService.getFileInfo(getTenantId(), id);
//        var filePath = Paths.get(fileInfo.getLocation());
//
//        ByteArrayResource resource = new ByteArrayResource(ByteBuffer.wrap(Files.readAllBytes(filePath)).array());

        return ResponseEntity.ok().build();
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode("xxx", StandardCharsets.UTF_8))
////                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + new String(fileInfo.getFileName().getBytes("utf-8"), "ISO8859-1"))
//                .contentLength(resource.contentLength())
//                .body(resource);
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
        return this.orderService.updateOrSaveOrder(getTenantId(), orderVO);
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
        this.orderService.deleteOrder(getTenantId(), toUUID(orderId));
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
     * 看板-订单产能监控
     */
    @ApiOperation(value = "看板-订单产能监控", notes = "工厂Id不传，则显示全部工厂")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", required = true),
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query")
    })
    @GetMapping("/order/board/capacityMonitor")
    public List<OrderBoardCapacityResult> listBoardCapacityMonitorOrders(
            @RequestParam("startTime") Long startTime,
            @RequestParam("endTime") Long endTime,
            @RequestParam(value = "factoryId", required = false) String factoryId) throws ThingsboardException {
        checkParameter("startTime", startTime);
        checkParameter("endTime", endTime);
        return this.orderService.listBoardCapacityMonitorOrders(getTenantId(), CommonUtil.toUUIDNullable(factoryId), startTime, endTime);
    }

    /**
     * App-首页-订单统计
     */
    @ApiOperation(value = "App-首页-订单管理", notes = "不传工厂Id，则当前登录用户所属工厂下全部订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query")
    })
    @GetMapping("/order/app/index/capacityMonitor")
    public OrderAppIndexCapacityResult getAppIndexOrderCapacityResult(
            @RequestParam(value = "factoryId", required = false) String factoryId) throws ThingsboardException {
        return this.orderService.getAppIndexOrderCapacityResult(getTenantId(), CommonUtil.toUUIDNullable(factoryId));
    }

    /**
     * App-订单-订单监控
     */
    @ApiOperation(value = "App-订单-订单监控", notes = "不传工厂Id，则当前登录用户所属工厂下全部订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query"),
            @ApiImplicitParam(name = "orderNo", value = "订单编号", paramType = "query")
    })
    @GetMapping("/order/app/capacityMonitor")
    public PageData<OrderListResult> getAppIndexOrderCapacityResult(
            @RequestParam(value = "factoryId", required = false) String factoryId,
            @RequestParam(value = "orderNo", required = false) String orderNo
    ) throws ThingsboardException {
        return this.orderService.listPageAppOrderCapacityMonitorByQuery(getTenantId(), CommonUtil.toUUIDNullable(factoryId), orderNo);
    }

    /**
     * App-订单-订单监控-生产计划
     */
    @ApiOperation(value = "App-订单-订单监控-生产计划", notes = "不传工厂Id，则当前登录用户所属工厂下全部订单")
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
    @ApiOperation(value = "App-订单-订单监控-生产计划-更新实际时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单Id", paramType = "path", required = true),
    })
    @PutMapping("/order/app/capacityMonitor/{orderId}/plan/device/{deviceId}/actualTime")
    public void updateOrderPlanDeviceActualTime(@PathVariable("orderId") String orderId, @PathVariable("deviceId") String deviceId, @RequestBody @Valid OrderPlanDeviceActualTimeVO timeVO) throws ThingsboardException {
        checkParameter("orderId", orderId);
        checkParameter("deviceId", deviceId);
        this.orderService.updateOrderPlanDeviceActualTime(getTenantId(), toUUID(orderId), toUUID(deviceId), timeVO);
    }
}
