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
package org.thingsboard.server.entity.menu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.dao.model.sql.MenuEntity;

import java.util.UUID;
@Data
public abstract class AbstractMenu {

    @ApiModelProperty(name = "菜单标识")
    public UUID id;

    @ApiModelProperty(name = "菜单编码")
    public String code;

    @ApiModelProperty("系统菜单名称")
    public String name;

    @ApiModelProperty("层级")

    public Integer level;
    @ApiModelProperty("排序")
    public Integer sort;

    @ApiModelProperty("页面链接")
    public String url;

    @ApiModelProperty("系统菜单图标")
    public String menuIcon;

    @ApiModelProperty("系统菜单自定义图片")
    public String menuImages;

    @ApiModelProperty("父级租户菜单")
    public UUID parentId;

    @ApiModelProperty("菜单类型（PC/APP）")
    public String menuType;

    @ApiModelProperty("路径")
    private String path;

    @ApiModelProperty("创建人标识")
    public UUID createdUser;
    @ApiModelProperty("创建时间")
    public long createdTime;
    @ApiModelProperty("修改时间")
    public long updatedTime;
    @ApiModelProperty("修改人")
    public UUID updatedUser;
    @ApiModelProperty("区域")
    public String region;

    public AbstractMenu() {
        super();
    }

    public AbstractMenu(Menu menu) {
        if (menu.getId() != null) {
            this.setId(menu.getId().getId());
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
        this.createdTime = menu.getUpdatedTime();
        this.createdUser = menu.getCreatedUser();
        this.updatedTime = menu.getUpdatedTime();
        this.updatedUser = menu.getUpdatedUser();
    }

    public AbstractMenu(MenuEntity menuEntity) {
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
        this.parentId = menuEntity.getParentId();
        this.createdTime = menuEntity.getUpdatedTime();
        this.createdUser = menuEntity.getCreatedUser();
        this.updatedTime = menuEntity.getUpdatedTime();
        this.updatedUser = menuEntity.getUpdatedUser();
    }

    protected Menu toMenu(){
        Menu menu = new Menu(new MenuId(this.getId()));
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
        menu.setCreatedTime(createdTime);
        menu.setCreatedUser(createdUser);
        menu.setUpdatedTime(updatedTime);
        menu.setUpdatedUser(updatedUser);
        return menu;
    }

}
