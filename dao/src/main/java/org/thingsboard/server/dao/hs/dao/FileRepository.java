package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
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

    Optional<FileEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    Optional<FileEntity> findByTenantIdAndScopeAndEntityId(UUID tenantId, String scope, UUID entityId);

    List<FileEntity> findAllByTenantIdAndScopeAndEntityIdOrderByCreatedTimeDesc(UUID tenantId, String scope, UUID entityId);

    void deleteAllByTenantIdAndScopeAndEntityId(UUID tenantId, String scope, UUID entityId);
}
