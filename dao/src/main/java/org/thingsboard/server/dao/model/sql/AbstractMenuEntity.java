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
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.SearchTextEntity;
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
public abstract class AbstractMenuEntity<T extends Menu> extends BaseSqlEntity<T> implements SearchTextEntity<T> {

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "region")
    private String region;

    @Column(name = "level")
    private Integer level;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "url")
    private String url;

    @Column(name = "menu_icon")
    private String menuIcon;

    @Column(name = "menu_images")
    private String menuImages;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "menu_type")
    private String menuType;

    @Column(name = "path")
    private String path;

    @CreatedDate
    @Column(name = "created_time")
    private long createdTime;

    @CreatedBy
    @Column(name = "created_user")
    private UUID createdUser;

    @CreatedDate
    @Column(name = "updated_time")
    private long updatedTime;

    @CreatedBy
    @Column(name = "updated_user")
    private UUID updatedUser;

    public AbstractMenuEntity() {
        super();
    }

    public AbstractMenuEntity(Menu menu) {
        if (menu.getId() != null) {
            this.setUuid(menu.getId().getId());
        }
        this.setCreatedTime(menu.getCreatedTime());
        this.code = menu.getCode();
        this.region = menu.getRegion();
        this.name = menu.getName();
        this.level = menu.getLevel();
        this.sort = menu.getSort();
        this.url = menu.getUrl();
        this.menuIcon = menu.getMenuIcon();
        this.menuImages = menu.getMenuImages();
        this.parentId = menu.getParentId();
        this.menuType = menu.getMenuType();
        this.path = menu.getPath();
        this.createdTime = menu.getUpdatedTime();
        this.createdUser = menu.getCreatedUser();
        this.updatedTime = menu.getUpdatedTime();
        this.updatedUser = menu.getUpdatedUser();
    }

    public AbstractMenuEntity(MenuEntity menuEntity) {
        this.setId(menuEntity.getId());
        this.setCreatedTime(menuEntity.getCreatedTime());
        this.code = menuEntity.getCode();
        this.region = menuEntity.getRegion();
        this.name = menuEntity.getName();
        this.level = menuEntity.getLevel();
        this.sort = menuEntity.getSort();
        this.url = menuEntity.getUrl();
        this.menuIcon = menuEntity.getMenuIcon();
        this.menuImages = menuEntity.getMenuImages();
        this.menuType = menuEntity.getMenuType();
        this.path = menuEntity.getPath();
        this.parentId = menuEntity.getParentId();
        this.createdTime = menuEntity.getUpdatedTime();
        this.createdUser = menuEntity.getCreatedUser();
        this.updatedTime = menuEntity.getUpdatedTime();
        this.updatedUser = menuEntity.getUpdatedUser();
    }

    protected Menu toMenu(){
        Menu menu = new Menu(new MenuId(this.getUuid()));
        menu.setCreatedTime(createdTime);
        menu.setCode(code);
        menu.setRegion(region);
        menu.setName(name);
        menu.setLevel(level);
        menu.setSort(sort);
        menu.setUrl(url);
        menu.setMenuIcon(menuIcon);
        menu.setMenuImages(menuImages);
        menu.setParentId(parentId);
        menu.setMenuType(menuType);
        menu.setPath(path);
        menu.setCreatedTime(createdTime);
        menu.setCreatedUser(createdUser);
        menu.setUpdatedTime(updatedTime);
        menu.setUpdatedUser(updatedUser);
        return menu;
    }

}
