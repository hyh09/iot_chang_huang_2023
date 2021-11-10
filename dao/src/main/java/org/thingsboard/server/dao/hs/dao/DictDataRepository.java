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
 * 数据字典Repository
 *
 * @author wwj
 * @since 2021.10.19
 */
@Repository
public interface DictDataRepository extends PagingAndSortingRepository<DictDataEntity, UUID>, JpaSpecificationExecutor<DictDataEntity> {

    Optional<DictDataEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    @Query("select d.code from DictDataEntity d where d.tenantId = :tenantId")
    List<String> findAllCodesByTenantId(@Param("tenantId") UUID tenantId);

    @Query("select d from DictDataEntity d where d.tenantId = :tenantId order by d.createdTime desc")
    List<DictDataEntity> findAllByTenantId(@Param("tenantId") UUID tenantId);

    @Query("select d from DictDataEntity d where d.tenantId = :tenantId and d.name in :keys order by d.createdTime desc")
    List<DictDataEntity> findAllByTenantIdAndKeys(@Param("tenantId") UUID tenantId, @Param("keys") List<String> keys);
}
