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
    @Query("select p2 " +
            "from OrderPlanEntity p2 " +
            "where p2.tenantId = :tenantId and " +
            "p2.deviceId = :deviceId and " +
            "p2.maintainStartTime is not null and " +
            "p2.maintainEndTime is not null and " +
            "p2.id not in ( select p1.id from OrderPlanEntity p1 where " +
            "p1.tenantId = :tenantId and " +
            "p1.deviceId = :deviceId and " +
            "p1.maintainStartTime is not null and " +
            "p1.maintainEndTime is not null and (p1.maintainEndTime < :startTime or p1.maintainStartTime > :endTime))")
    CompletableFuture<List<OrderPlanEntity>> findAllMaintainTimeCross(@Param("tenantId") UUID tenantId, @Param("deviceId") UUID deviceId, @Param("startTime") Long startTime,@Param("endTime") Long endTime);


    @Async
    @Query("select p2 " +
            "from OrderPlanEntity p2 " +
            "where p2.tenantId = :tenantId and " +
            "p2.deviceId = :deviceId and " +
            "p2.actualStartTime is not null and " +
            "p2.actualEndTime is not null and " +
            "p2.id not in ( select p1.id from OrderPlanEntity p1 where " +
            "p1.tenantId = :tenantId and " +
            "p1.deviceId = :deviceId and " +
            "p1.actualStartTime is not null and " +
            "p1.actualEndTime is not null and (p1.actualEndTime < :startTime or p1.actualStartTime > :endTime))")
    CompletableFuture<List<OrderPlanEntity>> findAllActualTimeCross(@Param("tenantId") UUID tenantId, @Param("deviceId") UUID deviceId, @Param("startTime") Long startTime,@Param("endTime") Long endTime);

    @Async
    @Query("select p2 " +
            "from OrderPlanEntity p2 " +
            "where p2.tenantId = :tenantId and " +
            "p2.actualStartTime is not null and " +
            "p2.actualEndTime is not null and " +
            "p2.id not in ( select p1.id from OrderPlanEntity p1 where " +
            "p1.tenantId = :tenantId and " +
            "p1.actualStartTime is not null and " +
            "p1.actualEndTime is not null and (p1.actualEndTime < :startTime or p1.actualStartTime > :endTime))")
    CompletableFuture<List<OrderPlanEntity>> findAllActualTimeCross(@Param("tenantId") UUID tenantId, @Param("startTime") Long startTime,@Param("endTime") Long endTime);

    @Async
    CompletableFuture<List<OrderPlanEntity>> findAllByTenantIdAndOrderIdOrderBySortAsc(UUID tenantId, UUID orderId);

    void deleteAllByOrderId(UUID orderId);

    @Async
    CompletableFuture<List<OrderPlanEntity>> findAllByOrderIdIn(List<UUID> orderIds);

    @Async
    CompletableFuture<List<OrderPlanEntity>> findAllByOrderId(UUID orderId);

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
    @Query("select t1 from OrderPlanEntity t1 where t1.factoryId = :factoryId and ( " +
            "( t1.actualStartTime >= :startTime and t1.actualEndTime <= :endTime ) or" +
            "( t1.actualStartTime >= :startTime and t1.actualStartTime <= :endTime and t1.actualEndTime >= :endTime) or" +
            "( t1.actualEndTime >= :startTime and t1.actualEndTime <= :endTime and t1.actualStartTime <= :startTime) or" +
            "( t1.actualStartTime <= :startTime and t1.actualEndTime >= :endTime ) " +
            ")")
    List<OrderPlanEntity> findActualByFactoryIds(@Param("factoryId") UUID factoryId,@Param("startTime") Long startTime, @Param("endTime") Long endTime);


    /**
     * 查询时间范围内的计划产量
     * @param factoryId
     * @param startTime
     * @param endTime
     * @return
     */
    @Query("select t1 from OrderPlanEntity t1 where t1.factoryId = :factoryId and ( " +
            "( t1.intendedStartTime >= :startTime and t1.intendedEndTime <= :endTime ) or" +
            "( t1.intendedStartTime >= :startTime and t1.actualStartTime <= :endTime and t1.intendedEndTime >= :endTime) or" +
            "( t1.intendedEndTime >= :startTime and t1.intendedEndTime <= :endTime and t1.intendedStartTime <= :startTime) or" +
            "( t1.intendedStartTime <= :startTime and t1.intendedEndTime >= :endTime ) " +
            ")")
    List<OrderPlanEntity> findIntendedByFactoryIds(@Param("factoryId") UUID factoryId,@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * 查询设备时间范围内的计划产量
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    @Query("select t from OrderPlanEntity t where t.deviceId = :deviceId and ( (t.intendedStartTime >= :startTime  and t.intendedStartTime <= :endTime) or (t.intendedStartTime >= :startTime and t.intendedEndTime <= :endTime ) or (t.intendedEndTime >= :startTime and t.intendedEndTime <= :endTime )or (t.actualStartTime <= :startTime and t.actualEndTime >= :endTime ))")
    List<OrderPlanEntity> findIntendedByDeviceId(@Param("deviceId")UUID deviceId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * 查询设备时间范围内的实际产量
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    @Query("select t from OrderPlanEntity t where t.deviceId = :deviceId and ((t.actualStartTime >= :startTime  and t.actualStartTime <= :endTime) or (t.actualStartTime >= :startTime and t.actualEndTime <= :endTime ) or (t.actualEndTime >= :startTime and t.actualEndTime <= :endTime ) or (t.actualStartTime <= :startTime and t.actualEndTime >= :endTime ))")
    List<OrderPlanEntity> findActualByDeviceId(@Param("deviceId")UUID deviceId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
}
