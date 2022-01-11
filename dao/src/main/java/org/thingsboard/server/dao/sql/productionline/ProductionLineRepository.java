/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.sql.productionline;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.thingsboard.server.dao.model.sql.ProductionLineEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
public interface ProductionLineRepository extends PagingAndSortingRepository<ProductionLineEntity, UUID>, JpaSpecificationExecutor<ProductionLineEntity> {

    List<ProductionLineEntity> findAllByTenantIdAndWorkshopIdOrderByCreatedTimeDesc(UUID tenantId, UUID id);

    @Async
    @Query("select t from ProductionLineEntity t where " +
            "t.tenantId = :tenantId " +
            "AND t.workshopId = :workshopId " +
            "AND t.name like CONCAT('%', :name, '%') ")
    CompletableFuture<Page<ProductionLineEntity>> findAllByTenantIdAndWorkshopIdAndNameLike(@Param("tenantId") UUID tenantId, @Param("workshopId") UUID workshopId, @Param("name") String workshopName, Pageable pageable);

    Optional<ProductionLineEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    /**
     * 根据车间id删除（逻辑删除）
     * @param workshopId
     */
    @Query(value = "UPDATE ProductionLineEntity t SET t.delFlag = 'D' WHERE t.workshopId = :workshopId")
    void delProductionLineByWorkshopId(@Param(("workshopId")) UUID workshopId);
}
