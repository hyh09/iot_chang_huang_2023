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
package org.thingsboard.server.dao.sql.factory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.dao.model.sql.FactoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
public interface FactoryRepository extends PagingAndSortingRepository<FactoryEntity, UUID>, JpaSpecificationExecutor<FactoryEntity> {

    Optional<FactoryEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    List<FactoryEntity> findAllByTenantIdOrderByCreatedTimeDesc(UUID tenantId);

    @Query("select new FactoryEntity(t.id, t.name, t.tenantId) from FactoryEntity t")
    List<FactoryEntity> findAllSimple();

    @Async
    @Query("select new FactoryEntity(t.id, t.name) from FactoryEntity t where " +
            "t.tenantId = :tenantId ")
    CompletableFuture<List<FactoryEntity>> findAllByTenantId(@Param("tenantId") UUID tenantId);

    @Async
    @Query("select new FactoryEntity(t.id, t.name) from FactoryEntity t where " +
            "t.tenantId = :tenantId and " +
            "t.id = :factoryId")
    CompletableFuture<FactoryEntity> findIdAndNameByTenantIdAndFactoryId(UUID tenantId, UUID factoryId);

    @Async
    @Query("select new FactoryEntity(t.id, t.name) from FactoryEntity t where " +
            "t.tenantId = :tenantId " +
            "order by t.createdTime desc")
    CompletableFuture<List<FactoryEntity>> findAllIdAndNameByTenantIdOrderByCreatedTimeDesc(@Param("tenantId") UUID tenantId);

    @Async
    @Query("select new FactoryEntity(t.id, t.name) from FactoryEntity t where " +
            "t.tenantId = :tenantId " +
            "order by t.sort asc, t.createdTime asc")
    CompletableFuture<List<FactoryEntity>> findAllIdAndNameAndSortByTenantIdOrderBySortAsc(@Param("tenantId") UUID tenantId);

    @Async
    @Query("select t from FactoryEntity t where " +
            "t.tenantId = :tenantId " +
            "AND t.name like CONCAT('%', :name, '%')")
    CompletableFuture<List<FactoryEntity>> findAllByTenantIdAndNameLike(@Param("tenantId") UUID tenantId, @Param("name") String name);

    @Async
    @Query("select t from FactoryEntity t where " +
            "t.tenantId = :tenantId " +
            "AND t.name like CONCAT('%', :name, '%')")
    CompletableFuture<Page<FactoryEntity>> findAllByTenantIdAndNameLike(@Param("tenantId") UUID tenantId, @Param("name") String name, Pageable pageable);

    @Async
    @Query("select t from FactoryEntity t where " +
            "t.tenantId = :tenantId " +
            "AND t.name = :name ")
    CompletableFuture<Page<FactoryEntity>> findAllByTenantIdAndName(@Param("tenantId") UUID tenantId, @Param("name") String name, Pageable pageable);

//    @Query("SELECT org.thingsboard.server.dao.model.sql.FactoryInfoEntity(f,w,p) " +
//            "FROM FactoryEntity f " +
//            "LEFT JOIN WorkshopEntity w on f.id = w.factoryId " +
//            "LEFT JOIN ProductionLineEntity p on wp.id = p.workshopId " +
//            "WHERE f.tenantId = :tenantId " +
//            "AND f.name = :name " +
//            "AND w.name = :workshopName " +
//            "AND p.name = :productionLineName ")
//    Page<FactoryInfoEntity> findFactoryListBuyCdn(@Param("tenantId") UUID tenantId,
//                                               @Param("name") String name,
//                                               @Param("workshopName") String workshopName,
//                                               @Param("productionLineName") String productionLineName,
//                                               Pageable pageable);

    @Query(value = "select * from hs_factory where if(?1!='',name=?1,1=1) and if(?2!='',code=?2,1=1)" ,nativeQuery = true)
    List<FactoryEntity> findFactoryListBuyCdn(@Param("name") String name,@Param("code") String code );


    /**
     * 根据租户查询
     * @param tenantId
     * @return
     */
    @Query("SELECT t FROM FactoryEntity t WHERE t.tenantId = :tenantId ")
    List<Factory> findFactoryByTenantId(@Param("tenantId")UUID tenantId);

    /**
     * 查询租户的第一条工厂数据
     */
    @Query(value = "SELECT * FROM hs_factory t WHERE t.tenant_id = :tenantId limit 1 ",nativeQuery = true)
    FactoryEntity findFactoryByTenantIdFirst(@Param("tenantId")UUID tenantId);


}
