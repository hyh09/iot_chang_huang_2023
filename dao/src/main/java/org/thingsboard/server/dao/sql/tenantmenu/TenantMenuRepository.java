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
package org.thingsboard.server.dao.sql.tenantmenu;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.model.sql.TenantMenuEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
public interface TenantMenuRepository extends PagingAndSortingRepository<TenantMenuEntity, UUID> {

    @Query("SELECT t FROM TenantMenuEntity t WHERE t.region = :region " +
            "AND LOWER(t.tenantMenuName) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<TenantMenuEntity> findByRegionNextPage(@Param("region") String region,
                                            @Param("textSearch") String textSearch,
                                            Pageable pageable);


    /**
     * 查询同级目录下最大排序值
     * @param parentId
     * @return
     */
    @Query("SELECT max(t.sort) FROM TenantMenuEntity t WHERE t.parentId = :parentId ")
    Integer getMaxSortByParentId(@Param("parentId") UUID parentId);

    /**
     * 查询同级下指定菜单后面所有菜单
     * @param sort
     * @param parentId
     * @return
     */
    @Query("SELECT t FROM TenantMenuEntity t WHERE t.parentId = :parentId AND t.sort > :sort ORDER BY t.sort ASC")
    List<TenantMenuEntity> findRearList(Integer sort, UUID parentId);

    /**
     * 查询租户PC/APP菜单列表
     * @param menuType
     * @param tenantId
     * @return
     */
    @Query("SELECT t FROM TenantMenuEntity t WHERE t.menuType = :menuType AND t.tenantId = :tenantId ORDER BY t.sort ASC")
    List<TenantMenuEntity> getTenantMenuList(String menuType, String tenantId);

    @Query("SELECT t FROM TenantMenuEntity t WHERE t.menuType = :menuType AND t.tenantId = :tenantId AND t.tenantMenuName = :tenantMenuName ORDER BY t.sort ASC")
    List<TenantMenuEntity> getTenantMenuList(String menuType, String tenantId,String tenantMenuName);

}
