package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 设备字典图表数据Repository
 *
 * @author wwj
 * @since 2021.11.10
 */
@Repository
public interface DictDeviceGraphRepository extends PagingAndSortingRepository<DictDeviceGraphEntity, UUID>, JpaSpecificationExecutor<DictDeviceGraphEntity> {

    @Async
    CompletableFuture<List<DictDeviceGraphEntity>> findAllByDictDeviceIdOrderByCreatedTimeDesc(UUID dictDeviceId);

    void deleteAllByDictDeviceId(UUID dictDeviceId);
}
