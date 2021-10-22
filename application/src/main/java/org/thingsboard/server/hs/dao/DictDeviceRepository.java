package org.thingsboard.server.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 设备字典Repository
 *
 * @author wwj
 * @since 2021.10.21
 */
@Repository
public interface DictDeviceRepository extends PagingAndSortingRepository<DictDeviceEntity, UUID>, JpaSpecificationExecutor<DictDeviceEntity> {

    @Query("select d.code from DictDeviceEntity d where d.tenantId = :tenantId")
    List<String> findCodesByTenantId(@Param("tenantId") UUID tenantId);
}
