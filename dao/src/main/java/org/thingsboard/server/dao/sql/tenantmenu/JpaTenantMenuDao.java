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
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.tenantmenu.TenantMenuId;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.menu.MenuDao;
import org.thingsboard.server.dao.model.sql.TenantMenuEntity;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;
import org.thingsboard.server.dao.sql.role.dao.TenantMenuRoleDao;
import org.thingsboard.server.dao.tenantmenu.TenantMenuDao;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
@Component
public class JpaTenantMenuDao extends JpaAbstractSearchTextDao<TenantMenuEntity, TenantMenu> implements TenantMenuDao {

    @Autowired
    private TenantMenuRepository tenantMenuRepository;
    @Autowired
    private MenuDao menuDao;
    @Autowired
    private TenantMenuRoleDao tenantMenuRoleDao;

    @Override
    protected Class<TenantMenuEntity> getEntityClass() {
        return TenantMenuEntity.class;
    }

    @Override
    protected CrudRepository<TenantMenuEntity, UUID> getCrudRepository() {
        return tenantMenuRepository;
    }

    /**
     * 根据租户删除菜单
     * @param tenantId
     */
    @Override
    public void deletedByTenant(UUID tenantId){
        tenantMenuRepository.deletedByTenant(tenantId);
    }

    /**
     * 根据系统菜单删除菜单
     * @param sysMenuId
     */
    @Override
    public void delByMenuId(UUID sysMenuId){
        tenantMenuRepository.delByMenuId(sysMenuId);
        //删除菜单角色
        tenantMenuRoleDao.deleteByMenuIds(Lists.newArrayList(sysMenuId));
    }
    /**
     *新增/修改租户菜单
     * @param tenantMenuList
     */
    @Override
    public void saveOrUpdTenantMenu(List<TenantMenu> tenantMenuList){
        List<TenantMenuEntity> saveTenantMenuEntityList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(tenantMenuList)){
            tenantMenuList.forEach(i->{
                saveTenantMenuEntityList.add(new TenantMenuEntity(i));
            });
            List<TenantMenuEntity> entityList = saveTenantMenuEntityList.stream().filter(e -> e.getSysMenuId() != null).collect(Collectors.toList());
            List<TenantMenuEntity> collectButton = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(entityList)){
                List<Menu> buttons = menuDao.getButtonListByIds(entityList.stream().map(TenantMenuEntity::getSysMenuId).collect(Collectors.toList()));
                if(CollectionUtils.isNotEmpty(buttons)) {
                    for (TenantMenuEntity entity:saveTenantMenuEntityList){
                        for (Menu button:buttons) {
                            if (entity.getSysMenuId() != null && entity.getSysMenuId().toString().equals(button.getParentId().toString())) {
                                TenantMenuEntity tenantMenuEntity = new TenantMenuEntity(button, entity.getLevel() + 1, button.getCreatedUser(),entity.getId());
                                tenantMenuEntity.setTenantId(entity.getTenantId());
                                collectButton.add(tenantMenuEntity);
                                System.out.println(tenantMenuEntity.getTenantMenuName());
                            }
                        }
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(collectButton)){
                saveTenantMenuEntityList.addAll(collectButton);
            }
        }
        tenantMenuRepository.saveAll(saveTenantMenuEntityList);
    }

    /**
     * 系统菜单变更，触发租户菜单更新
     * （新增按钮、按钮名称、lang_key、path、icon修改，变更租户菜单按钮）
     * @param tenantMenuList
     */
    @Override
    public void saveFromSysMenu(List<TenantMenu> tenantMenuList){
        List<TenantMenuEntity> tenantMenuEntityList = new ArrayList<>();
        tenantMenuList.forEach(i->{
            TenantMenuEntity entity = new TenantMenuEntity(i);
            if(i.getId()==null){
                //排序、编码、主键
                UUID uuid = Uuids.timeBased();
                entity.setUuid(uuid);
                entity.setCreatedTime(Uuids.unixTimestamp(uuid));
                if(i.getIsButton()){
                    entity.setTenantMenuCode("ZHAN"+String.valueOf(System.currentTimeMillis()));
                }else {
                    entity.setTenantMenuCode("ZHCD"+String.valueOf(System.currentTimeMillis()));
                }
                //查询相同父级下最大排序值
                entity.setSort(getMaxSortByParentId(i.getParentId()));
            }
            tenantMenuEntityList.add(entity);
        });
        tenantMenuRepository.saveAll(tenantMenuEntityList);
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
        Integer maxSortByParentId = tenantMenuRepository.getMaxSortByParentId(parentId);
        return maxSortByParentId == null ? 0:maxSortByParentId;
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
     * @param tenantMenu
     * @return
     */
    @Override
    public List<TenantMenu> getTenantMenuList(TenantMenu tenantMenu){
        return this.commonCondition(tenantMenu);
    }

    @Override
    public List<TenantMenu> findByIdIn(List<UUID> ids) {
        return   listToVo(tenantMenuRepository.findByIdIn(ids));
    }

    /**
     * 自定义查询菜单列表
     * @param menuType
     * @param tenantId
     * @param id
     * @return
     */
    @Override
    public List<TenantMenu> getTenantMenuListByIds(String menuType, UUID tenantId, List<UUID> id) {
        return  listToVo(tenantMenuRepository.getTenantMenuListByIds(menuType,tenantId,id));
    }

    @Override
    public List<TenantMenu>  getTenantMenuListByTenantId(String menuType,UUID tenantId)
    {
        List<TenantMenuEntity>  tenantMenuEntityList = tenantMenuRepository.getTenantMenuList(menuType,tenantId);
        return listToVo(tenantMenuEntityList);
    }
    @Override
    public List<TenantMenu> findAllByCdn(TenantMenu tenantMenu){
        return this.commonCondition(tenantMenu);
    }

    /**
     * 根据id查
     * @param id
     * @return
     */
    @Override
    public TenantMenu getMenuById(UUID id){
        Optional<TenantMenuEntity> optional = tenantMenuRepository.findById(id);
        if(!optional.isEmpty()){
            TenantMenuEntity entity = optional.get();
            if(entity != null){
                return entity.toData();
            }
        }
        return null;
    }

    /**
     * 构造查询条件,需要家条件在这里面加
     * @param tenantMenu
     * @return
     */
    private List<TenantMenu> commonCondition(TenantMenu tenantMenu){
        List<TenantMenu> resultTenantMenu = new ArrayList<>();
        Specification<TenantMenuEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(tenantMenu != null){
                if(tenantMenu.getTenantId() != null){
                    predicates.add(cb.equal(root.get("tenantId"),tenantMenu.getTenantId()));
                }
                if(tenantMenu.getIsButton() != null){
                    predicates.add(cb.equal(root.get("isButton"),tenantMenu.getIsButton()));
                }
                if(StringUtils.isNotEmpty(tenantMenu.getMenuType())){
                    predicates.add(cb.equal(root.get("menuType"),tenantMenu.getMenuType()));
                }
                if(tenantMenu.getSysMenuId() != null){
                    predicates.add(cb.equal(root.get("sysMenuId"),tenantMenu.getSysMenuId()));
                }
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<TenantMenuEntity> all = tenantMenuRepository.findAll(specification);
        if(CollectionUtils.isNotEmpty(all)){
            all.forEach(i->{
                resultTenantMenu.add(i.toData());
            });
        }
        return resultTenantMenu;
    }

    /**
     *
     * @param entities
     * @return
     */
    private  List<TenantMenu> listToVo(List<TenantMenuEntity>   entities)
    {
        List<TenantMenu> tenantMenuList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(entities)){
            entities.forEach(tenantMenuEntity->{
                if(tenantMenuEntity != null){
                    tenantMenuList.add(tenantMenuEntity.toData());
                }
            });

        }

        return  tenantMenuList;
    }

}
