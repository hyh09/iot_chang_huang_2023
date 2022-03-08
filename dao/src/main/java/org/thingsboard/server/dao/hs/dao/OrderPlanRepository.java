package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 订单计划Repository
 *
 * @author wwj
 * @since 2021.11.30
 */
@Repository
public interface OrderPlanRepository extends PagingAndSortingRepository<OrderPlanEntity, UUID>, JpaSpecificationExecutor<OrderPlanEntity> {
    Optional<OrderPlanEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    @Async
    CompletableFuture<List<OrderPlanEntity>> findAllByTenantIdAndOrderIdOrderBySortAsc(UUID tenantId, UUID orderId);

    void deleteAllByOrderId(UUID orderId);

    @Async
    CompletableFuture<List<OrderPlanEntity>> findAllByOrderIdIn(List<UUID> orderIds);

    @Async
    CompletableFuture<List<OrderPlanEntity>> findAllByOrderId(UUID orderId);

    @Async
    CompletableFuture<List<OrderPlanEntity>> findAllByTenantIdAndActualStartTimeLessThanEqualAndActualEndTimeGreaterThanEqual(UUID tenantId, Long startTime, Long endTime);

    @Query("select t2 from OrderPlanEntity t2 " +
            "where t2.id not in(" +
            "   select t1.id from OrderPlanEntity t1 " +
            "   where  t1.actualStartTime > :actualEndTime or t1.actualStartTime < :actualStartTime " +
            ") " +
            " and t2.deviceId in :deviceId ")
    List<OrderPlanEntity> findDeviceAchieveOrPlanList(@Param("deviceId") List<UUID> deviceId, @Param("actualStartTime") Long actualStartTime, @Param("actualEndTime") Long actualEndTime);

}
