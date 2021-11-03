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

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.tenantmenu.TenantMenuId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.TenantMenuEntity;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;
import org.thingsboard.server.dao.tenantmenu.TenantMenuDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
@Component
public class JpaTenantMenuDao extends JpaAbstractSearchTextDao<TenantMenuEntity, TenantMenu> implements TenantMenuDao {

    @Autowired
    private TenantMenuRepository tenantMenuRepository;

    @Override
    protected Class<TenantMenuEntity> getEntityClass() {
        return TenantMenuEntity.class;
    }

    @Override
    protected CrudRepository<TenantMenuEntity, UUID> getCrudRepository() {
        return tenantMenuRepository;
    }

    @Override
    public PageData<TenantMenu> findTenantMenusByRegion(TenantMenuId tenantMenuId, String region, PageLink pageLink) {
        return DaoUtil.toPageData(tenantMenuRepository
                .findByRegionNextPage(
                        region,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }
    /**
     * 查询同级目录下最大排序值
     * @param parentId
     * @return
     */
    public Integer getMaxSortByParentId(UUID parentId){
        return tenantMenuRepository.getMaxSortByParentId(parentId);
    }

    /**
     * 保存租户菜单信息
     * @param tenantMenu
     * @return
     */
    @Override
    public TenantMenu save(TenantMenu tenantMenu){
        TenantMenuEntity tenantMenuEntity = new TenantMenuEntity(tenantMenu);
        if (tenantMenuEntity.getUuid() == null) {
            UUID uuid = Uuids.timeBased();
            tenantMenuEntity.setUuid(uuid);
            tenantMenuEntity.setCreatedTime(Uuids.unixTimestamp(uuid));
        }
        return tenantMenuRepository.save(tenantMenuEntity).toTenantMenu();
    }

    /**
     * 查询同级下指定菜单后面所有菜单
     * @param sort
     * @param parentId
     * @return
     */
    @Override
    public List<TenantMenu> findRearList(Integer sort, UUID parentId){
        List<TenantMenu> tenantMenuList = new ArrayList<>();
        List<TenantMenuEntity> tenantMenuEntityList = tenantMenuRepository.findRearList(sort,parentId);
        if(!CollectionUtils.isEmpty(tenantMenuEntityList)){
            tenantMenuEntityList.forEach(tenantMenuEntity->{
                if(tenantMenuEntity != null){
                    tenantMenuList.add(tenantMenuEntity.toData());
                }
            });

        }
        return tenantMenuList;
    }

    /**
     * 查询租户PC/APP菜单列表
     * @param menuType
     * @param tenantId
     * @return
     */
    @Override
    public List<TenantMenu> getTenantMenuList(String menuType,String tenantId,String tenantMenuName){
        List<TenantMenu> tenantMenuList = new ArrayList<>();
        List<TenantMenuEntity> tenantMenuEntityList = new ArrayList<>();
        if(StringUtils.isNotEmpty(tenantMenuName)){
            tenantMenuEntityList = tenantMenuRepository.getTenantMenuList(menuType,tenantId,tenantMenuName);
        }else {
            tenantMenuEntityList = tenantMenuRepository.getTenantMenuList(menuType,UUID.fromString(tenantId));
        }
        if(!CollectionUtils.isEmpty(tenantMenuEntityList)){
            tenantMenuEntityList.forEach(tenantMenuEntity->{
                if(tenantMenuEntity != null){
                    tenantMenuList.add(tenantMenuEntity.toData());
                }
            });

        }
        return tenantMenuList;
    }

}
