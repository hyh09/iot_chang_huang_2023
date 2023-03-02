package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 文件Repository
 *
 * @author wwj
 * @since 2021.11.30
 */
@Repository
public interface FileRepository extends PagingAndSortingRepository<FileEntity, UUID>, JpaSpecificationExecutor<FileEntity> {

    @Query("select f from FileEntity f where " +
            "f.scope is null " +
            "AND f.entityId is null " +
            "AND f.createdTime <= :endTime ")
    List<FileEntity> findAllUnusedFiles(@Param("endTime") Long endTime);

    Optional<FileEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    Optional<FileEntity> findByTenantIdAndScopeAndEntityId(UUID tenantId, String scope, UUID entityId);

    List<FileEntity> findAllByTenantIdAndScopeAndEntityIdOrderByCreatedTimeDesc(UUID tenantId, String scope, UUID entityId);

    List<FileEntity> findAllByTenantIdAndScopeOrderByCreatedTimeDesc(UUID tenantId, String scope);

    void deleteAllByTenantIdAndScopeAndEntityId(UUID tenantId, String scope, UUID entityId);

    @Query(value = "select f from FileEntity f where f.scope ='DICT_DEVICE_MODEL' and f.entityId in :dictDeviceIds group by f.entityId,f.id")
    List<FileEntity> findDeviceModelByDictDeviceIds(@Param("dictDeviceIds") List<UUID> dictDeviceIds);
}
