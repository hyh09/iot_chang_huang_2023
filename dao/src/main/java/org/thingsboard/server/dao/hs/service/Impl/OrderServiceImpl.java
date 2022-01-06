package org.thingsboard.server.dao.hs.service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.dao.*;
import org.thingsboard.server.dao.hs.entity.po.Order;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.hs.service.CommonService;
import org.thingsboard.server.dao.hs.service.OrderService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 订单接口实现类
 *
 * @author wwj
 * @since 2021.10.18
 */
@Service
@Slf4j
@Transactional(readOnly = true, rollbackFor = Exception.class)
@AllArgsConstructor
public class OrderServiceImpl extends AbstractEntityService implements OrderService, CommonService {

    private final OrderRepository orderRepository;
    private final OrderPlanRepository orderPlanRepository;
    private final ClientService clientService;

    /**
     * 保存订单-从文件导入
     *
     * @param tenantId          租户Id
     * @param checksum          校验和
     * @param checksumAlgorithm 校验和算法
     * @param file              文件
     */
    @Override
    @Transactional
    public void saveOrdersFromFile(TenantId tenantId, String checksum, ChecksumAlgorithm checksumAlgorithm, MultipartFile file) throws IOException {
        var workbook = new XSSFWorkbook(file.getInputStream());
        var sheetIterator = workbook.sheetIterator();
        while (sheetIterator.hasNext()) {
            var sheet = sheetIterator.next();
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                Cell cell = row.getCell(0);
            }
        }
    }

    /**
     * 订单列表- 分页
     *
     * @param tenantId   租户id
     * @param userId     用户Id
     * @param orderQuery 查询参数
     * @param pageLink   分页参数
     * @return 订单列表
     */
    @Override
    public PageData<OrderListResult> listPageOrdersByQuery(TenantId tenantId, UserId userId, OrderListQuery orderQuery, PageLink pageLink) {
        return null;
    }

    /**
     * 订单-详情
     *
     * @param tenantId 租户id
     * @param orderId  订单id
     * @return 订单
     */
    @Override
    public OrderVO getOrderDetail(TenantId tenantId, UUID orderId) throws ThingsboardException {
        return null;
    }

    /**
     * 订单-更新或创建
     *
     * @param tenantId 租户id
     * @param orderVO  订单
     */
    @Override
    @Transactional
    public OrderVO updateOrSaveOrder(TenantId tenantId, OrderVO orderVO) throws ThingsboardException {
        return null;
    }

    /**
     * 订单-删除
     *
     * @param tenantId 租户id
     * @param orderId  订单id
     */
    @Override
    @Transactional
    public void deleteOrder(TenantId tenantId, UUID orderId) throws ThingsboardException {
    }

    /**
     * 获得当前可用订单编码
     *
     * @param tenantId 租户id
     * @return 编码
     */
    @Override
    public String getAvailableCode(TenantId tenantId) {
        return null;
    }

    /**
     * 订单产能监控-列表查询
     *
     * @param tenantId 租户id
     * @param userId   用户Id
     * @param query    查询参数
     * @param pageLink 分页参数
     */
    @Override
    public PageData<OrderListResult> listPageOrderCapacityMonitorByQuery(TenantId tenantId, UserId userId, OrderListQuery query, PageLink pageLink) {
        return null;
    }

    /**
     * 订单产能监控-详情
     *
     * @param tenantId 租户id
     * @param orderId  订单Id
     */
    @Override
    public OrderVO getOrderCapacityMonitorDetail(TenantId tenantId, UUID orderId) throws ThingsboardException {
        return null;
    }

    /**
     * 订单产能监控-看板
     *
     * @param tenantId  租户id
     * @param factoryId 工厂Id
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    @Override
    public List<OrderBoardCapacityResult> listBoardCapacityMonitorOrders(TenantId tenantId, UUID factoryId, Long startTime, Long endTime) {


        return null;
    }

    /**
     * 订单产能监控-App-首页
     *
     * @param tenantId  租户id
     * @param factoryId 工厂Id
     */
    @Override
    public OrderAppIndexCapacityResult getAppIndexOrderCapacityResult(TenantId tenantId, UUID factoryId) {
        return null;
    }

    /**
     * App-订单-订单监控
     *
     * @param tenantId  租户id
     * @param factoryId 工厂Id
     * @param orderNo   订单编号
     */
    @Override
    public PageData<OrderListResult> listPageAppOrderCapacityMonitorByQuery(TenantId tenantId, UUID factoryId, String orderNo) {
        return null;
    }

    /**
     * App-订单-订单监控-生产计划-更新实际时间
     *
     * @param tenantId 租户id
     * @param orderId  订单Id
     * @param deviceId 设备Id
     * @param timeVO   时间参数
     */
    @Override
    public void updateOrderPlanDeviceActualTime(TenantId tenantId, UUID orderId, UUID deviceId, OrderPlanDeviceActualTimeVO timeVO) {
    }

    /**
     * 订单-生产计划删除
     */
    @Transactional
    public void deleteOrderPlan(UUID orderId) {
        this.orderPlanRepository.deleteAllByOrderId(orderId).join();
    }
}
