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
package org.thingsboard.server.entity.tenantmenu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.id.tenantmenu.TenantMenuId;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;

import java.util.UUID;

@Data
public abstract class AbstractTenantMenu{

    @ApiModelProperty("租户菜单标识")
    public UUID id;
    @ApiModelProperty("租户标识")
    public UUID tenantId;
    @ApiModelProperty("系统菜单标识")
    public UUID sysMenuId;
    @ApiModelProperty("系统菜单编码")
    public String sysMenuCode;
    @ApiModelProperty("系统菜单名称")
    public String sysMenuName;
    @ApiModelProperty("租户菜单名称")
    public String tenantMenuName;
    @ApiModelProperty("租户菜单编码")
    public String tenantMenuCode;
    @ApiModelProperty("层级")
    public Integer level;
    @ApiModelProperty("排序")
    public Integer sort;
    @ApiModelProperty("页面链接")
    public String url;
    @ApiModelProperty("父级租户菜单")
    public UUID parentId;
    @ApiModelProperty("租户菜单图标")
    public String tenantMenuIcon;
    @ApiModelProperty("租户菜单自定义图片")
    public String tenantMenuImages;
    @ApiModelProperty("菜单类型（PC/APP）")
    public String menuType;
    @ApiModelProperty("是按钮（true/false）")
    public Boolean isButton;
    @ApiModelProperty("路径")
    public String path;
    @ApiModelProperty("是否树节点（true/false）")
    public Boolean hasChildren;
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
    @ApiModelProperty("多语言Key")
    public String langKey;

    public AbstractTenantMenu() {
    }

    public AbstractTenantMenu(TenantMenu tenantMenu) {
        if (tenantMenu.getId() != null) {
            this.setId(tenantMenu.getId());
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
        this.isButton = tenantMenu.getIsButton();
        this.menuType = tenantMenu.getMenuType();
        this.langKey = tenantMenu.getLangKey();
        this.path = tenantMenu.getPath();
        this.hasChildren = tenantMenu.getHasChildren();
        this.createdTime = tenantMenu.getUpdatedTime();
        this.createdUser = tenantMenu.getCreatedUser();
        this.updatedTime = tenantMenu.getUpdatedTime();
        this.updatedUser = tenantMenu.getUpdatedUser();
    }

    public TenantMenu toTenantMenu(){
        TenantMenu tenantMenu = new TenantMenu(new TenantMenuId(this.getId()));
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
        tenantMenu.setPath(path);
        tenantMenu.setHasChildren(hasChildren);
        tenantMenu.setCreatedTime(createdTime);
        tenantMenu.setCreatedUser(createdUser);
        tenantMenu.setUpdatedTime(updatedTime);
        tenantMenu.setUpdatedUser(updatedUser);
        return tenantMenu;
    }

}
