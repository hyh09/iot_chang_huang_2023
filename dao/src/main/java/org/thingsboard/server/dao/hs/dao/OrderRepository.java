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
import java.util.stream.Stream;

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
    @Query("SELECT new OrderEntity(e.orderNo) FROM OrderEntity e WHERE " +
            "e.tenantId = :tenantId " +
            "AND e.orderNo LIKE CONCAT(:orderNo, '%') ")
    CompletableFuture<List<OrderEntity>> findAllOrderNoByTenantIdAndOrderNoLike(@Param("tenantId") UUID tenantId, @Param("orderNo") String orderNo);
}
