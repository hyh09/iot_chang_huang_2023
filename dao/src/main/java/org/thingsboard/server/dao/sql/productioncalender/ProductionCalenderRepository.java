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

    List<ProductionCalenderEntity> findAllByTenantIdAndDeviceIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(UUID tenantId, UUID deviceId, Long startTime, Long endTime);

    @Async
    @Query("select p2 " +
            "from ProductionCalenderEntity p2 " +
            "where p2.tenantId = :tenantId and " +
            "p2.deviceId = :deviceId and " +
            "p2.startTime is not null and " +
            "p2.endTime is not null and " +
            "p2.id not in ( select p1.id from ProductionCalenderEntity p1 where " +
            "p1.tenantId = :tenantId and " +
            "p1.deviceId = :deviceId and " +
            "p1.startTime is not null and " +
            "p1.endTime is not null and (p1.endTime < :startTime or p1.startTime > :endTime))")
    CompletableFuture<List<ProductionCalenderEntity>> findAllCross(@Param("tenantId") UUID tenantId, @Param("deviceId") UUID deviceId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
}
