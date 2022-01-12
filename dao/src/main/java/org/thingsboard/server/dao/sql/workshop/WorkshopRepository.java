/**
 * Copyright © 2016-2021 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.sql.workshop;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.thingsboard.server.dao.model.sql.WorkshopEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
public interface WorkshopRepository extends PagingAndSortingRepository<WorkshopEntity, UUID>, JpaSpecificationExecutor<WorkshopEntity> {

    List<WorkshopEntity> findAllByTenantIdAndFactoryIdOrderByCreatedTimeDesc(UUID tenantId, UUID factoryId);

    @Async
    @Query("select t from WorkshopEntity t where " +
            "t.tenantId = :tenantId " +
            "AND t.factoryId = :factoryId " +
            "AND t.name like CONCAT('%', :name, '%') ")
    CompletableFuture<Page<WorkshopEntity>> findAllByTenantIdAndFactoryIdAndNameLike(@Param("tenantId") UUID tenantId, @Param("factoryId") UUID factoryId, @Param("name") String workshopName, Pageable pageable);

    Optional<WorkshopEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    /**
     * 根据工厂删除后刷新值(逻辑删除)
     * @param factoryId
     */
    @Query(value = "UPDATE WorkshopEntity t SET t.delFlag = 'D' WHERE t.factoryId = :factoryId")
    void delWorkshopByFactoryId(@Param("factoryId") UUID factoryId);
}
