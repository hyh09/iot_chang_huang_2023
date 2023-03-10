package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 设备字典Repository
 *
 * @author wwj
 * @since 2021.10.21
 */
@Repository
public interface DictDeviceRepository extends PagingAndSortingRepository<DictDeviceEntity, UUID>, JpaSpecificationExecutor<DictDeviceEntity> {

    @Query("select new DictDeviceEntity(d.id, d.name, d.isDefault, d.model) from DictDeviceEntity d where d.tenantId = :tenantId order by d.createdTime desc")
    List<DictDeviceEntity> findAllByTenantId(@Param("tenantId") UUID tenantId);

    Optional<DictDeviceEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    Optional<DictDeviceEntity> findByTenantIdAndCode(UUID tenantId, String code);

    @Query("select d.code from DictDeviceEntity d where d.tenantId = :tenantId")
    List<String> findAllCodesByTenantId(@Param("tenantId") UUID tenantId);

    List<DictDeviceEntity> findAllByTenantIdAndIdIn(UUID tenantId, List<UUID> ids);

    Optional<DictDeviceEntity> findByTenantIdAndIsDefaultIsTrue(UUID tenantId);

    List<DictDeviceEntity> findAllByTenantIdAndUpdatedTimeGreaterThan(UUID tenantId, Long startTime);
}
