package org.thingsboard.server.common.data.memu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.UUID;

@Data
public class Menu{
    private UUID id;
    @NoXss
    private String code;
    @NoXss
    private String name;
    @NoXss
    private Integer level;
    @NoXss
    private Integer sort;
    @NoXss
    private String url;
    @NoXss
    private UUID parentId;
    @NoXss
    private String menuIcon;
    @NoXss
    private String menuImages;
    @NoXss
    private String menuType;

    //"是按钮（true/false）")
    public Boolean isButton;
    //多语言Key
    private String langKey;
    private String path;
    private long createdTime;
    @NoXss
    private UUID createdUser;
    @NoXss
    private long updatedTime;
    @NoXss
    private UUID updatedUser;
    @NoXss
    private String region;

    private UUID tenantId;

    /*****非数据库字段*****/
    @ApiModelProperty("上级菜单名称")
    private String parentName;

    public Menu() {
        super();
    }

    public Menu(MenuId id) {
        this.id = id.getId();
    }
    public Menu(UUID id) {
        this.id = id;
    }

    public void updMenu(Menu menu){
        this.id = menu.getId();
        this.tenantId = menu.getTenantId();
        this.name = menu.getName();
        this.path = menu.getPath();
        this.menuIcon= menu.getMenuIcon();
        this.menuImages = menu.getMenuImages();
        this.parentId = menu.getParentId();
    }

    public TenantMenu toTenantMenuByAddButton(TenantMenu tenantMenuSource){
        TenantMenu tenantMenu = new TenantMenu();
        tenantMenu.setParentId(tenantMenuSource.getParentId());
        tenantMenu.setLevel(tenantMenuSource.getLevel() + 1);
        tenantMenu.setTenantId(this.getTenantId());
        tenantMenu.setSysMenuId(this.getId());
        tenantMenu.setRegion(this.getRegion());
        tenantMenu.setSysMenuCode(this.getCode());
        tenantMenu.setSysMenuName(this.getName());
        tenantMenu.setTenantMenuName(this.getName());
        tenantMenu.setUrl(this.getUrl());
        tenantMenu.setTenantMenuIcon(this.getMenuIcon());
        tenantMenu.setTenantMenuImages(this.getMenuImages());
        tenantMenu.setMenuType(this.getMenuType());
        tenantMenu.setIsButton(this.getIsButton());
        tenantMenu.setLangKey(this.getLangKey());
        tenantMenu.setPath(this.getPath());
        tenantMenu.setHasChildren(false);
        tenantMenu.setCreatedTime(this.getCreatedTime());
        tenantMenu.setCreatedUser(this.getCreatedUser());
        tenantMenu.setUpdatedTime(this.getUpdatedTime());
        tenantMenu.setUpdatedUser(this.getUpdatedUser());
        tenantMenu.setName(this.getName());
        return tenantMenu;
    }



}

