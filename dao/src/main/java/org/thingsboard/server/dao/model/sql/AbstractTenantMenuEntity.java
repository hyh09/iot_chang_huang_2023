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
package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.common.data.id.tenantmenu.TenantMenuId;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractTenantMenuEntity<T extends TenantMenu> extends BaseSqlEntity<T>{

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "sys_menu_id")
    private UUID sysMenuId;

    @Column(name = "sys_menu_code")
    private String sysMenuCode;

    @Column(name = "sys_menu_name")
    private String sysMenuName;

    @Column(name = "tenant_menu_name")
    private String tenantMenuName;

    @Column(name = "tenant_menu_code")
    private String tenantMenuCode;

    @Column(name = "region")
    private String region;

    @Column(name = "level")
    private Integer level;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "url")
    private String url;

    @Column(name = "tenant_menu_icon")
    private String tenantMenuIcon;

    @Column(name = "tenant_menu_images")
    private String tenantMenuImages;

    @Column(name = "parent_id")
    private UUID parentId;

    //"是按钮（true/false）")
    @Column(name = "is_button")
    public Boolean isButton;

    @Column(name = "lang_key")
    private String langKey;

    @Column(name = "menu_type")
    private String menuType;

    @CreatedDate
    @Column(name = "created_time")
    private long createdTime;

   // @CreatedBy
    @Column(name = "created_user")
    private UUID createdUser;

    @CreatedDate
    @Column(name = "updated_time")
    private long updatedTime;

    //@CreatedBy
    @Column(name = "updated_user")
    private UUID updatedUser;

    public AbstractTenantMenuEntity() {
        super();
    }

    public AbstractTenantMenuEntity(AbstractTenantMenuEntity<T> abstractTenantMenuEntity){
        if (abstractTenantMenuEntity.getId() != null) {
            this.setUuid(abstractTenantMenuEntity.getId());
        }
        this.tenantId = abstractTenantMenuEntity.getTenantId();
        this.sysMenuId = abstractTenantMenuEntity.getSysMenuId();
        this.region = abstractTenantMenuEntity.getRegion();
        this.sysMenuCode = abstractTenantMenuEntity.getSysMenuCode();
        this.sysMenuName = abstractTenantMenuEntity.getSysMenuName();
        this.tenantMenuName = abstractTenantMenuEntity.getTenantMenuName();
        this.tenantMenuCode = abstractTenantMenuEntity.getTenantMenuCode();
        this.level = abstractTenantMenuEntity.getLevel();
        this.sort = abstractTenantMenuEntity.getSort();
        this.url = abstractTenantMenuEntity.getUrl();
        this.tenantMenuIcon = abstractTenantMenuEntity.getTenantMenuIcon();
        this.tenantMenuImages = abstractTenantMenuEntity.getTenantMenuImages();
        this.parentId = abstractTenantMenuEntity.getParentId();
        this.menuType = abstractTenantMenuEntity.getMenuType();
        this.isButton = abstractTenantMenuEntity.getIsButton();
        this.langKey = abstractTenantMenuEntity.getLangKey();
        this.createdTime = abstractTenantMenuEntity.getUpdatedTime();
        this.createdUser = abstractTenantMenuEntity.getCreatedUser();
        this.updatedTime = abstractTenantMenuEntity.getUpdatedTime();
        this.updatedUser = abstractTenantMenuEntity.getUpdatedUser();
    }

    public AbstractTenantMenuEntity(TenantMenu tenantMenu) {
        if (tenantMenu.getId() != null) {
            this.setUuid(tenantMenu.getId().getId());
        }
        this.tenantId = tenantMenu.getTenantId();
        this.sysMenuId = tenantMenu.getSysMenuId();
        this.region = tenantMenu.getRegion();
        this.sysMenuCode = tenantMenu.getSysMenuCode();
        this.sysMenuName = tenantMenu.getSysMenuName();
        this.tenantMenuName = tenantMenu.getTenantMenuName();
        this.tenantMenuCode = tenantMenu.getTenantMenuCode();
        this.level = tenantMenu.getLevel();
        this.sort = tenantMenu.getSort();
        this.url = tenantMenu.getUrl();
        this.tenantMenuIcon = tenantMenu.getTenantMenuIcon();
        this.tenantMenuImages = tenantMenu.getTenantMenuImages();
        this.parentId = tenantMenu.getParentId();
        this.menuType = tenantMenu.getMenuType();
        this.isButton = tenantMenu.getIsButton();
        this.langKey = tenantMenu.getLangKey();
        this.createdTime = tenantMenu.getUpdatedTime();
        this.createdUser = tenantMenu.getCreatedUser();
        this.updatedTime = tenantMenu.getUpdatedTime();
        this.updatedUser = tenantMenu.getUpdatedUser();
    }

    public TenantMenu toTenantMenu(){
        TenantMenu tenantMenu = new TenantMenu(new TenantMenuId(this.getUuid()));
        tenantMenu.setTenantId(tenantId);
        tenantMenu.setSysMenuId(sysMenuId);
        tenantMenu.setRegion(region);
        tenantMenu.setSysMenuCode(sysMenuCode);
        tenantMenu.setSysMenuName(sysMenuName);
        tenantMenu.setTenantMenuCode(tenantMenuCode);
        tenantMenu.setTenantMenuName(tenantMenuName);
        tenantMenu.setLevel(level);
        tenantMenu.setSort(sort);
        tenantMenu.setUrl(url);
        tenantMenu.setTenantMenuIcon(tenantMenuIcon);
        tenantMenu.setTenantMenuImages(tenantMenuImages);
        tenantMenu.setParentId(parentId);
        tenantMenu.setMenuType(menuType);
        tenantMenu.setIsButton(isButton);
        tenantMenu.setLangKey(langKey);
        tenantMenu.setCreatedTime(createdTime);
        tenantMenu.setCreatedUser(createdUser);
        tenantMenu.setUpdatedTime(updatedTime);
        tenantMenu.setUpdatedUser(updatedUser);
        tenantMenu.setName(tenantMenuName);
        return tenantMenu;
    }

}
