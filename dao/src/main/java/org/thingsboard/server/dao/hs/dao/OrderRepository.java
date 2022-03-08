package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 订单Repository
 *
 * @author wwj
 * @since 2021.11.30
 */
@Repository
public interface OrderRepository extends PagingAndSortingRepository<OrderEntity, UUID>, JpaSpecificationExecutor<OrderEntity> {

    Optional<OrderEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    Optional<OrderEntity> findByTenantIdAndOrderNo(UUID tenantId, String orderNo);

    @Async
    CompletableFuture<List<OrderEntity>> findAllByTenantIdAndIdInOrderByCreatedTimeDesc(UUID tenantId, Set<UUID> ids);

    @Async
    CompletableFuture<List<OrderEntity>> findAllByTenantIdAndFactoryIdAndIdInOrderByCreatedTimeDesc(UUID tenantId, UUID factoryId, Set<UUID> ids);

    @Async
    CompletableFuture<List<OrderEntity>> findAllByTenantIdAndWorkshopIdAndIdInOrderByCreatedTimeDesc(UUID tenantId, UUID workshopId, Set<UUID> ids);

    @Async
    @Query("SELECT new OrderEntity(e.orderNo) FROM OrderEntity e WHERE " +
            "e.tenantId = :tenantId " +
            "AND e.orderNo LIKE CONCAT(:orderNo, '%') ")
    CompletableFuture<List<OrderEntity>> findAllOrderNoByTenantIdAndOrderNoLike(@Param("tenantId") UUID tenantId, @Param("orderNo") String orderNo);

}
