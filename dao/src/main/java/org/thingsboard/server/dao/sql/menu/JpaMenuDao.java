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

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.menu.MenuDao;
import org.thingsboard.server.dao.model.sql.MenuEntity;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
@Component
@Transactional
public class JpaMenuDao extends JpaAbstractSearchTextDao<MenuEntity, Menu> implements MenuDao {
    private final static Boolean FALSE = false;

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

    @Override
    public Menu saveMenu(Menu domain) throws ThingsboardException {
        Menu resultMenu = new Menu();
        MenuEntity menuEntity = new MenuEntity(domain);
        if (menuEntity.getUuid() == null) {
            UUID uuid = Uuids.timeBased();
            menuEntity.setUuid(uuid);
            menuEntity.setCreatedTime(Uuids.unixTimestamp(uuid));
        }else{
            menuRepository.deleteById(menuEntity.getUuid());
            menuEntity.setUpdatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
        }
        MenuEntity entity = menuRepository.save(menuEntity);
        if(entity != null){
            resultMenu = entity.toData();
        }
        return resultMenu;
    }

    @Override
    public Menu findSameLevelName(UUID parentId,String name){
        MenuEntity sameLevelName = menuRepository.findSameLevelName(parentId, name);
        if(sameLevelName != null){
            return sameLevelName.toData();
        }
        return null;
    }

    /**
     * 根据菜单id,查询菜单下按钮
     * @param ids
     * @return
     */
    @Override
    public List<Menu> getButtonListByIds(List<UUID> ids){
        List<Menu> resultMenuList = new ArrayList<>();
        List<MenuEntity> entityList = menuRepository.getButtonListByIds(ids);
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.forEach(i->{
                resultMenuList.add(i.toData());
            });
        }
        return resultMenuList;
    }


    /**
     * 查询系统菜单列表分页
     * @param menu
     * @param pageLink
     * @return
     */
    @Override
    public PageData<Menu> getMenuPage(Menu menu, PageLink pageLink) throws ThingsboardException {
        // 动态条件查询
        Specification<MenuEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(menu != null){
                if(StringUtils.isNotBlank(menu.getName())){
                    predicates.add(cb.like(root.get("name"),"%" + menu.getName().trim() + "%"));
                }
                if(StringUtils.isNotBlank(menu.getMenuType())){
                    predicates.add(cb.equal(root.get("menuType"),menu.getMenuType()));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = DaoUtil.toPageable(pageLink);
        Page<MenuEntity> menuEntities = menuRepository.findAll(specification, pageable);
        //转换数据
        List<MenuEntity> content = menuEntities.getContent();

        List<Menu> resultMenuList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(content)){
            content.forEach(i->{
                Menu resultMenu = i.toData();
                if(i.getParentId() != null){
                    MenuEntity perentEntity = menuRepository.findById(i.getParentId()).get();
                    if(perentEntity != null && StringUtils.isNotBlank(perentEntity.getName())){
                        resultMenu.setParentName(perentEntity.getName());
                    }
                }
                resultMenuList.add(resultMenu);
            });
        }
        PageData<Menu> resultPage = new PageData<>();
        resultPage = new PageData<Menu>(resultMenuList,menuEntities.getTotalPages(),menuEntities.getTotalElements(),menuEntities.hasNext());
        return resultPage;
    }

    /**
     * 条件查询系统菜单列表
     * @param menu
     * @return
     */
    @Override
    public List<Menu> getMenuListByCdn(Menu menu){
        return this.commonCondition(menu);
    }
    /**
     * 构造查询条件,需要家条件在这里面加
     * @param menu
     * @return
     */
    private List<Menu> commonCondition(Menu menu){
        List<Menu> resultTenantMenu = new ArrayList<>();
        Specification<MenuEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(menu != null){
                if(menu.getTenantId() != null){
                    predicates.add(cb.equal(root.get("tenantId"),menu.getTenantId()));
                }
                if(menu.getIsButton() != null){
                    predicates.add(cb.equal(root.get("isButton"),menu.getIsButton()));
                }
                if(org.thingsboard.server.common.data.StringUtils.isNotEmpty(menu.getMenuType())){
                    predicates.add(cb.equal(root.get("menuType"),menu.getMenuType()));
                }
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<MenuEntity> all = menuRepository.findAll(specification);
        if(CollectionUtils.isNotEmpty(all)){
            all.forEach(i->{
                resultTenantMenu.add(i.toData());
            });
        }
        return resultTenantMenu;
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
     * @param name
     * @return
     */
    @Override
    public List<Menu> findMenusByName(String menuType,String name) {
        List<Menu> menuList = new ArrayList<>();
        List<MenuEntity> menuEntityList = new ArrayList<>();
        Specification<MenuEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            //排除掉按钮
            predicates.add(cb.equal(root.get("isButton"),FALSE));
            if(StringUtils.isNotBlank(menuType)){
                predicates.add(cb.equal(root.get("menuType"),menuType));
            }
            if(StringUtils.isNotBlank(name)){
                predicates.add(cb.like(root.get("name"),"%" + name.trim() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        menuEntityList = menuRepository.findAll(specification);
        if(!CollectionUtils.isEmpty(menuEntityList)){
            for (MenuEntity menuEntity : menuEntityList){
                menuList.add(menuEntity.toData());
            }
        }
        return menuList;
    }

    /**
     * 查询同级下指定菜单后面所有菜单
     * @param sort
     * @param parentId
     * @return
     */
    @Override
    public List<Menu> findRearList(Integer sort, UUID parentId){
        List<Menu> menuList = new ArrayList<>();
        List<MenuEntity> menuEntityList = menuRepository.findRearList(sort,parentId);
        if(!org.springframework.util.CollectionUtils.isEmpty(menuEntityList)){
            menuEntityList.forEach(tenantMenuEntity->{
                if(tenantMenuEntity != null){
                    menuList.add(tenantMenuEntity.toData());
                }
            });

        }
        return menuList;
    }

    /**
     * 查询同级目录下最大排序值
     * @param parentId
     * @return
     */
    @Override
    public Integer getMaxSortByParentId(UUID parentId){
        return menuRepository.getMaxSortByParentId(parentId);
    }

    /**
     * 查询一级菜单
     * @param menuType
     * @return
     */
    @Override
    public List<Menu> getOneLevel(String menuType){
        List<Menu> menuList = new ArrayList<>();
        List<MenuEntity> menuEntityList = menuRepository.getOneLevel(menuType);
        if(!CollectionUtils.isEmpty(menuEntityList)){
            menuEntityList.forEach(i->{
                menuList.add(i.toData());
            });
        }
        return menuList;
    }

    @Override
    public void delMenu(UUID id){
        menuRepository.deleteById(id);
    }


    @Override
    public Menu getTenantById(UUID id){
        return menuRepository.findById(id).get().toData();
    }


}
