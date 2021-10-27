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

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.menu.MenuDao;
import org.thingsboard.server.dao.model.sql.MenuEntity;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
@Component
public class JpaMenuDao extends JpaAbstractSearchTextDao<MenuEntity, Menu> implements MenuDao {

    @Autowired
    private MenuRepository menuRepository;

    @Override
    protected Class<MenuEntity> getEntityClass() {
        return MenuEntity.class;
    }

    @Override
    protected CrudRepository<MenuEntity, UUID> getCrudRepository() {
        return menuRepository;
    }


    /**
     * 分页查询
     * @param menuId
     * @param region
     * @param pageLink
     * @return
     */
    @Override
    public PageData<Menu> findMenusByRegion(MenuId menuId, String region, PageLink pageLink) {
        return DaoUtil.toPageData(menuRepository
                .findByRegionNextPage(
                        region,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));

    }
    /**
     *根据菜单名称查询
     * @param Name
     * @return
     */
    @Override
    public List<Menu> findMenusByName(String menuType,String Name) {
        List<Menu> menuList = new ArrayList<>();
        List<MenuEntity> menuEntityList = new ArrayList<>();
        if(StringUtils.isNotEmpty(Name)){
            menuEntityList = menuRepository.findMenusByName(menuType,Name);
        }else{
            menuEntityList = menuRepository.findMenusByMenuType(menuType);
        }
        if(!CollectionUtils.isEmpty(menuEntityList)){
            for (MenuEntity menuEntity : menuEntityList){
                menuList.add(menuEntity.toData());
            }
        }
        return menuList;
    }


}
