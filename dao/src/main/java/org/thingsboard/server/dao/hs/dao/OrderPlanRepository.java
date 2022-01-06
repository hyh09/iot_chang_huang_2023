package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
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

    @Async
    CompletableFuture<Void> deleteAllByOrderId(UUID orderId);

    @Async
    CompletableFuture<List<OrderPlanEntity>> findAllByOrderIdIn(List<UUID> orderIds);
}
