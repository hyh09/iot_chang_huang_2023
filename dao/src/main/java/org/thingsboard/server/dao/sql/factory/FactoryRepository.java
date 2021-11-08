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

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.dao.model.sql.FactoryEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
public interface FactoryRepository extends PagingAndSortingRepository<FactoryEntity, UUID>, JpaSpecificationExecutor<FactoryEntity> {

    FactoryEntity findByTenantIdAndId(UUID tenantId, UUID id);

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
     * 根据工厂管理员查询
     * @param factoryAdminId
     * @return
     */
    @Query("SELECT t FROM FactoryEntity t WHERE t.adminUserId = :factoryAdminId ")
    Factory findFactoryByAdmin(@Param("factoryAdminId")UUID factoryAdminId);

    /**
     * 根据租户查询
     * @param tenantId
     * @return
     */
    @Query("SELECT t FROM FactoryEntity t WHERE t.tenantId = :tenantId ")
    List<Factory> findFactoryByTenantId(@Param("tenantId")UUID tenantId);
}
