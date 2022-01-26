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

/**
 * 设备字典图表分项数据Repository
 *
 * @author wwj
 * @since 2021.11.10
 */
@Repository
public interface DictDeviceGraphItemRepository extends PagingAndSortingRepository<DictDeviceGraphItemEntity, UUID>, JpaSpecificationExecutor<DictDeviceGraphItemEntity> {

    @Async
    CompletableFuture<List<DictDeviceGraphItemEntity>> findAllByGraphIdOrderBySortAsc(UUID graphId);

    void deleteAllByGraphId(UUID graphId);

    void deleteAllByDictDeviceId(UUID dictDeviceId);

    void deleteByPropertyIdAndPropertyType(UUID propertyId, String propertyType);

    Optional<DictDeviceGraphItemEntity> findByPropertyIdAndPropertyType(UUID propertyId, String propertyType);

    @Query("select d from DictDeviceGraphItemEntity d where d.graphId = :graphId order by d.sort desc ")
    List<DictDeviceGraphItemEntity> findAllByGraphId(@Param("graphId") UUID graphId);
}
