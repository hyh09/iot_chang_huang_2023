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
package org.thingsboard.server.dao.sql.menu;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.model.sql.MenuEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
public interface MenuRepository extends PagingAndSortingRepository<MenuEntity, UUID>, JpaSpecificationExecutor<MenuEntity> {

    @Query("SELECT t FROM MenuEntity t WHERE t.region = :region " +
            "AND LOWER(t.name) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<MenuEntity> findByRegionNextPage(@Param("region") String region,
                                            @Param("textSearch") String textSearch,
                                            Pageable pageable);

    /**
     *根据菜单名称查询
     * @param name
     * @return
     */
    @Query("SELECT t FROM MenuEntity t WHERE t.name like :name AND t.menuType = :menuType ")
    List<MenuEntity> findMenusByName(@Param("menuType")String menuType,@Param("name")String name);

    /**
     *根据菜单类型查询
     * @param menuType
     * @return
     */
    @Query("SELECT t FROM MenuEntity t WHERE t.menuType = :menuType ")
    List<MenuEntity> findMenusByMenuType(@Param("menuType")String menuType);

    /**
     * 查询同级目录下最大排序值
     * @param parentId
     * @return
     */
    @Query("SELECT max(t.sort) FROM MenuEntity t WHERE t.parentId = :parentId ")
    Integer getMaxSortByParentId(@Param("parentId") UUID parentId);

    /**
     * 查询同级下指定菜单后面所有菜单
     * @param sort
     * @param parentId
     * @return
     */
    @Query("SELECT t FROM MenuEntity t WHERE t.parentId = :parentId AND t.sort > :sort ORDER BY t.sort ASC")
    List<MenuEntity> findRearList(@Param("sort")Integer sort, @Param("parentId")UUID parentId);

    /**
     * 查询一级菜单
     * @param menuType
     * @return
     */
    @Query("SELECT t FROM MenuEntity t WHERE t.menuType = :menuType AND t.level = 1 ORDER BY t.sort ASC")
    List<MenuEntity> getOneLevel(@Param("menuType")String menuType);

    @Query(value = "SELECT t FROM MenuEntity t WHERE t.menuType = :menuType ",nativeQuery = true)
    Page<MenuEntity> findByPage(@Param("menuType") String menuType,
                                            Pageable pageable);

    @Query(value = "SELECT t FROM MenuEntity t")
    Page<MenuEntity> findByPage(Pageable pageable);

    @Query(value = "SELECT t FROM MenuEntity t WHERE t.parentId = :parentId AND t.name = :name ")
    MenuEntity findSameLevelName(@Param("parentId") UUID parentId,@Param("name") String name);


}
