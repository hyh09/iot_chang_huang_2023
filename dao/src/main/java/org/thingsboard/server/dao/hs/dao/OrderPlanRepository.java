package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.dao.model.sql.ProductionCalenderEntity;

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
    @Query("select p from OrderPlanEntity p where p.tenantId = :tenantId and p.deviceId = :deviceId and p.maintainStartTime is not null and p.maintainEndTime is not null and (p.maintainEndTime >= :startTime or p.maintainStartTime <= :endTime)")
    CompletableFuture<List<OrderPlanEntity>> findAllMaintainTimeCross(@Param("tenantId") UUID tenantId, @Param("deviceId") UUID deviceId, @Param("startTime") Long startTime,@Param("endTime") Long endTime);


    @Async
    @Query("select p from OrderPlanEntity p where p.tenantId = :tenantId and p.deviceId = :deviceId and p.actualStartTime is not null and p.actualEndTime is not null and (p.actualEndTime >= :startTime or p.actualStartTime <= :endTime)")
    CompletableFuture<List<OrderPlanEntity>> findAllActualTimeCross(@Param("tenantId") UUID tenantId, @Param("deviceId") UUID deviceId, @Param("startTime") Long startTime,@Param("endTime") Long endTime);

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

    @Async
    @Query("SELECT t1 FROM OrderPlanEntity t1 WHERE t1.orderId in :orderIds and ( t1.actualStartTime > :actualEndTime or t1.actualStartTime < :actualStartTime ) " )
    List<OrderPlanEntity> findAllByOrderIdsAndTime(@Param("orderIds") List<UUID> orderIds, @Param("actualStartTime") Long actualStartTime, @Param("actualEndTime") Long actualEndTime);

    /**
     * 查询时间范围内的实际产量
     * @param factoryId
     * @param startTime
     * @param endTime
     * @return
     */
    @Query("select t from OrderPlanEntity t where t.factoryId = :factoryId and ( t.actualStartTime > :endTime or t.actualEndTime < :startTime )")
    List<OrderPlanEntity> findActualByFactoryIds(@Param("factoryId") UUID factoryId,@Param("startTime") Long startTime, @Param("endTime") Long endTime);


    /**
     * 查询时间范围内的计划产量
     * @param factoryId
     * @param startTime
     * @param endTime
     * @return
     */
    @Query("select t from OrderPlanEntity t where t.factoryId = :factoryId and ( t.intendedStartTime > :endTime or t.intendedEndTime < :startTime )")
    List<OrderPlanEntity> findIntendedByFactoryIds(@Param("factoryId") UUID factoryId,@Param("startTime") Long startTime, @Param("endTime") Long endTime);
}
