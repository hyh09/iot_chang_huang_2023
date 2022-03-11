package org.thingsboard.server.dao.sql.productioncalender;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.thingsboard.server.dao.model.sql.ProductionCalenderEntity;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ProductionCalenderRepository extends PagingAndSortingRepository<ProductionCalenderEntity, UUID>, JpaSpecificationExecutor<ProductionCalenderEntity> {

    @Async
    @Query("select p from ProductionCalenderEntity p where p.tenantId = :tenantId and p.deviceId = :deviceId and p.startTime is not null and p.endTime is not null and (p.endTime >= :startTime or p.startTime <= :endTime)")
    CompletableFuture<List<ProductionCalenderEntity>> findAllCross(@Param("tenantId") UUID tenantId, @Param("deviceId") UUID deviceId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
}
